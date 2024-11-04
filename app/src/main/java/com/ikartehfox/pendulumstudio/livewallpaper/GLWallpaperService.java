package com.ikartehfox.pendulumstudio.livewallpaper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import android.preference.PreferenceManager;

import com.ikartehfox.pendulumstudio.common.GenericPendulum;
import com.ikartehfox.pendulumstudio.pendulumwave.PendulumWave;

public abstract class GLWallpaperService extends WallpaperService implements SensorEventListener {

    private GenericPendulum mPendulum;
    private SensorManager mSensorManager;
    private Sensor mGravity;
    private boolean useDynGravity;
    private Display display;

    GenericPendulum getCurrentPendulum() {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
        switch (pendulum) {
            case "0":
                return PendulumRenderer.mPendulumMP;
            case "1":
                return PendulumRenderer.mPendulumSP;
            case "2":
                return PendulumRenderer.mPendulumSP2D;
            case "3":
                return PendulumRenderer.mPendulumSP3D;
            case "4":
                return PendulumRenderer.mPendulumDP;
            case "5":
                return PendulumRenderer.mPendulumDSP;
            case "6":
                return PendulumRenderer.mPendulumSMP;
            case "7":
                return PendulumRenderer.mPendulumSSP;
            default:
                return PendulumRenderer.mPendulumPW;
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        int rotmode = display.getRotation();
        if (rotmode == Surface.ROTATION_0)
            mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
        else if (rotmode == Surface.ROTATION_90)
            mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
        else if (rotmode == Surface.ROTATION_180)
            mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
        else if (rotmode == Surface.ROTATION_270)
            mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
        // Do something with this sensor value.
    }

    public class GLEngine extends Engine {
        private PendulumRenderer mRenderer;

        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";

            WallpaperGLSurfaceView(Context context) {
                super(context);

            }

            @Override
            public SurfaceHolder getHolder() {

                return getSurfaceHolder();
            }

            public void onDestroy() {

                super.onDetachedFromWindow();
                mSensorManager.unregisterListener(GLWallpaperService.this);
            }
        }

        private static final String TAG = "GLEngine";

        private WallpaperGLSurfaceView glSurfaceView;
        private boolean rendererHasBeenSet;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {

            super.onCreate(surfaceHolder);

            mPendulum = getCurrentPendulum();

            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            useDynGravity = PreferenceManager.getDefaultSharedPreferences(
                    GLWallpaperService.this).getBoolean("use_accelerometer", true);
            boolean PWP = PreferenceManager.getDefaultSharedPreferences(
                    GLWallpaperService.this).getString("pendulum_selection", "5").equals("8");
            if (PWP) useDynGravity = false;
            mPendulum.setGravityMode(useDynGravity);
            if (mPendulum.firsttime) {
                if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                    ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                            Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                            30. / 180. * Math.PI);
                else mPendulum.restart();
            }
            mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_1", 0xFFFF0000));
            mPendulum.setColorPendulum2(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_2", 0xFF0000FF));
            if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_wave", 0xFF0000FF));
            display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (rendererHasBeenSet) {
                if (visible) {
                    // This method will be called on the rendering
// thread:
                    glSurfaceView.queueEvent(() -> {
                        String oname = mRenderer.mPendulum.name;
                        mRenderer.switchPendulum(PreferenceManager.getDefaultSharedPreferences(
                                        GLWallpaperService.this).getString("pendulum_selection", "5"),
                                PreferenceManager.getDefaultSharedPreferences(
                                        GLWallpaperService.this).getBoolean("use_damping", true),
                                PreferenceManager.getDefaultSharedPreferences(
                                        GLWallpaperService.this).getBoolean("show_trace", true));
                        if (mRenderer.mPendulum.firsttime && !PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                            mRenderer.mPendulum.restart();
                        if (mRenderer.mPendulum.firsttime && PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                            ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                                    Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                                    30. / 180. * Math.PI);
                        if ((mRenderer.mPendulum.firsttime || !oname.equals(mRenderer.mPendulum.name)) && PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                            ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                                    Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                                    30. / 180. * Math.PI);
                        else if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8")
                                && (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")) != ((PendulumWave) mRenderer.mPendulum).NP || Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")) != ((PendulumWave) mRenderer.mPendulum).NT))
                            ((PendulumWave) mPendulum).restartPW(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NP", "12")),
                                    Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pref_NT", "40")),
                                    30. / 180. * Math.PI);
                        mRenderer.mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_1", 0xFFFF0000));
                        mRenderer.mPendulum.setColorPendulum2(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_2", 0xFF0000FF));
                        if (PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getString("pendulum_selection", "5").equals("8"))
                            mPendulum.setColorPendulum1(PreferenceManager.getDefaultSharedPreferences(GLWallpaperService.this).getInt("wallpaper_pendulum_color_wave", 0xFF0000FF));
                        mRenderer.recompileProgram();
                    });
//                        Log.v("Wallpaper renderer", "After: rendering pendulum is " + mRenderer.mPendulum.name);
                    mPendulum = getCurrentPendulum();
                    useDynGravity = PreferenceManager.getDefaultSharedPreferences(
                            GLWallpaperService.this).getBoolean("use_accelerometer", true);
//                        Log.v("Wallpaper:", "Changing");
                    //}
                    boolean PWP = PreferenceManager.getDefaultSharedPreferences(
                            GLWallpaperService.this).getString("pendulum_selection", "5").equals("8");
                    if (PWP) useDynGravity = false;
                    glSurfaceView.onResume();
                    mPendulum.setGravityMode(useDynGravity);

                    if (useDynGravity)
                        mSensorManager.registerListener(GLWallpaperService.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
                } else {
                    glSurfaceView.onPause();
                    if (useDynGravity) mSensorManager.unregisterListener(GLWallpaperService.this);
                }
            }
        }

        @Override
        public void onDestroy() {

            super.onDestroy();
            glSurfaceView.onDestroy();
        }

        protected void setRenderer(Renderer renderer) {
            mRenderer = (PendulumRenderer) renderer;
            glSurfaceView.setRenderer(renderer);
            rendererHasBeenSet = true;
        }

        protected void setPreserveEGLContextOnPause(boolean preserve) {

            glSurfaceView.setPreserveEGLContextOnPause(preserve);
        }

        protected void setEGLContextClientVersion(int version) {

            glSurfaceView.setEGLContextClientVersion(version);
        }

    }

    abstract boolean needNewPendulum();
}
