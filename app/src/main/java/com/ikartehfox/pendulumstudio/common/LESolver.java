package com.ikartehfox.pendulumstudio.common;

/**
 * Class for solving the linear system of equations
 * using the LU decomposition.
 */
public class LESolver {
    final int order;
    final double[][] a;
    final double[] b;
    final double[] x;
    // Row scaling used for scaled partial pivoting.
    private final double[] scale;
    // Row permutation produced by pivoting (perm[i] = original row now at position i).
    private final int[] perm;
    // Row of the original matrix whose pivot sits at each step (used by Solve).
    private final int[] pivotRow;
    public int LUDecomposited;
    // Relative magnitude below which a pivot is treated as singular.
    private static final double PIVOT_TOL = 1e-12;

    public LESolver(int sz, double[][] inA, double[] inB) {
        LUDecomposited = 0;
        order = sz;
        a = new double[sz][sz];
        for (int i = 0; i < sz; ++i)
            System.arraycopy(inA[i], 0, a[i], 0, sz);
        b = new double[sz];
        System.arraycopy(inB, 0, b, 0, sz);
        x = new double[sz];
        scale = new double[sz];
        perm = new int[sz];
        pivotRow = new int[sz];
        for (int i = 0; i < sz; ++i) perm[i] = i;
    }

    boolean LUDecompose() {
        if (LUDecomposited == -1) return false;
        else if (LUDecomposited == 1) return true;

        // Compute a scale factor (largest-magnitude element) for each row so the
        // pivot test is relative, not absolute. This avoids both the old exact
        // `== 0` test and catastrophic amplification from near-zero pivots that
        // occur when sin(theta) -> 0 in the spherical systems.
        for (int i = 0; i < order; ++i) {
            double s = 0.;
            for (int j = 0; j < order; ++j) {
                double v = Math.abs(a[i][j]);
                if (v > s) s = v;
            }
            scale[i] = s;
        }

        for (int i = 0; i < order; ++i) {
            // Scaled partial pivoting: pick the row with the largest ratio
            // |a[j][i]| / scale[j] among the remaining rows.
            int piv = i;
            double best = -1.;
            for (int j = i; j < order; ++j) {
                double r = scale[j] > 0. ? Math.abs(a[j][i]) / scale[j] : Math.abs(a[j][i]);
                if (r > best) {
                    best = r;
                    piv = j;
                }
            }
            pivotRow[i] = piv;
            if (best <= PIVOT_TOL || a[piv][i] == 0) {
                LUDecomposited = 0;
                return false;
            }
            // Swap rows i and piv (matrix, right-hand side, scale, permutation).
            if (piv != i) {
                double[] tmpRow = a[i];
                a[i] = a[piv];
                a[piv] = tmpRow;
                double tb = b[i];
                b[i] = b[piv];
                b[piv] = tb;
                double ts = scale[i];
                scale[i] = scale[piv];
                scale[piv] = ts;
                int tp = perm[i];
                perm[i] = perm[piv];
                perm[piv] = tp;
            }
            for (int j = i + 1; j < order; ++j) a[i][j] /= a[i][i];
            for (int j = i + 1; j < order; ++j) {
                for (int k = i + 1; k < order; ++k) {
                    a[j][k] -= a[j][i] * a[i][k];
                }
            }
        }
        LUDecomposited = 1;
        return true;
    }

    public double[] Solve() {
        if (LUDecomposited == -1 || (LUDecomposited == 0 && !LUDecompose())) {
            // Singular / ill-conditioned system: signal failure with NaN instead
            // of silently returning the all-zero x (which the integrator would
            // otherwise accept as a valid zero acceleration).
            for (int i = 0; i < order; ++i) x[i] = Double.NaN;
            return x;
        }
        for (int i = 0; i < order; ++i) {
            x[i] = b[i] / a[i][i];
            for (int j = 0; j < i; ++j)
                x[i] += -a[i][j] * x[j] / a[i][i];
        }
        for (int i = order - 1; i >= 0; --i) {
            for (int j = i + 1; j < order; ++j)
                x[i] += -a[i][j] * x[j];
        }
        return x;
    }
}
