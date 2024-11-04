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
    public int LUDecomposited;

    public LESolver(int sz, double[][] inA, double[] inB) {
        LUDecomposited = 0;
        order = sz;
        a = new double[sz][sz];
        for (int i = 0; i < sz; ++i)
            System.arraycopy(inA[i], 0, a[i], 0, sz);
        b = new double[sz];
        System.arraycopy(inB, 0, b, 0, sz);
        x = new double[sz];
    }

    boolean LUDecompose() {
        if (LUDecomposited == -1) return false;
        else if (LUDecomposited == 1) return true;
        for (int i = 0; i < order; ++i) {
            if (a[i][i] == 0) {
                LUDecomposited = 0;
                return false;
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
