package com.ikartehfox.pendulumstudio.sphericalpendulum;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class SPGLSurfaceView extends GLSurfaceView {

    SPGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mTapDetector;

    public SPGLSurfaceView(Context context) {
        super(context);

        init();
    }

    public SPGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void init() {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the EGL config chooser to specify the pixel formats and depth size
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SPGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mTapDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                final SPGLActivity act = (SPGLActivity) getContext();
                act.runOnUiThread(() -> {
                    if (act.buttonsAreOff)
                        act.timerHandler.post(act.timerButtonsOn);
                    else {
                        act.timerHandler.removeCallbacks(act.timerButtonsOff);
                        act.timerHandler.postDelayed(act.timerButtonsOff, act.buttonsFadeOutTime);
                    }
                });
                return true;
            }
        });

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        mScaleDetector.onTouchEvent(e);
        mTapDetector.onTouchEvent(e);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = SPGLRenderer.mPendulum.zoomIn;
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));

            SPGLRenderer.mPendulum.zoomIn = mScaleFactor;


            invalidate();
            return true;
        }
    }

}
