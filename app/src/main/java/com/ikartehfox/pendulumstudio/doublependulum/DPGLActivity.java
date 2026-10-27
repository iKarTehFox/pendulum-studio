package com.ikartehfox.pendulumstudio.doublependulum;

import com.ikartehfox.pendulumstudio.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;


public class DPGLActivity extends AppCompatActivity implements SensorEventListener {
    private DPGLSurfaceView mGLView;
    //private DSPGLRenderer mRenderer;
    private SensorManager mSensorManager;
    private Sensor mGravity;
    private boolean useDynGravity;
    private boolean useDamping;
    private boolean isRunning;
    private Display display;

    static final int frequency = 1000;
    static final int buttonsFadeOutTime = 4000;
    static final int buttonsFadeAnimationTime = 300;
    private boolean paused;
    private long deltaT;
    final Handler timerHandler = new Handler();
    final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            deltaT = System.currentTimeMillis() - deltaT;
            float fps = DPGLRenderer.mPendulum.frames / (float) (deltaT) * 1.e3f;
            ((TextView) findViewById(R.id.fps)).setText("FPS: " + String.format("%.0f", fps));
            DPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            if (isRunning && !paused) timerHandler.postDelayed(this, frequency);
        }
    };

    // Updates the angle/velocity readout once per rendered frame (driven by the
    // display refresh via postOnAnimation), independently of the 1 Hz FPS counter.
    final Runnable timerStateRunnable = new Runnable() {
        @Override
        public void run() {
            ((TextView) findViewById(R.id.fps_info)).setText(buildStateInfo(DPGLRenderer.mPendulum));
            if (!paused) findViewById(R.id.fps_info).postOnAnimation(this);
        }
    };

    boolean buttonsAreOff;
    final Runnable timerButtonsOff = new Runnable() {
        @Override
        public void run() {
            //Log.d("Act","ButtonsOff");
            if (paused || !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("pref_buttons_fade", true))
                return;

            findViewById(R.id.DP_buttons).animate()
                    .alpha(0f)
                    .setDuration(buttonsFadeAnimationTime)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            findViewById(R.id.DP_buttons).setVisibility(View.GONE);
                            buttonsAreOff = true;
                        }
                    });

        }
    };

    final Runnable timerButtonsOn = new Runnable() {
        @Override
        public void run() {
            //Log.d("Act","ButtonsOn");
            findViewById(R.id.DP_buttons).setAlpha(0f);
            findViewById(R.id.DP_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.DP_buttons).animate()
                    .alpha(1f)
                    .setDuration(buttonsFadeAnimationTime)
                    .setListener(null);

            buttonsAreOff = false;

            if (!paused) {
                timerHandler.removeCallbacks(timerButtonsOff);
                timerHandler.postDelayed(timerButtonsOff, buttonsFadeOutTime);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_landscape", false)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.doublependulum_gl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setFullScreenMode();
        setFpsMode();

        mGLView = findViewById(R.id.gl_surface_view);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        useDynGravity = DPGLRenderer.mPendulum.dynamicGravity;

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        useDamping = Math.abs(DPGLRenderer.mPendulum.k) > 1.e-7;

        isRunning = !DPGLRenderer.mPendulum.paused;
        if (!isRunning)
            ((ImageButton) findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
        else
            ((ImageButton) findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
        findViewById(R.id.button_playpause).setOnClickListener(v -> {
            if (isRunning)
                ((ImageButton) findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
            else
                ((ImageButton) findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
            isRunning = !isRunning;
            // This method will be called on the rendering
// thread:
            mGLView.queueEvent(() -> {
                //MPGLRenderer.mPendulum.restart();
                DPGLRenderer.mPendulum.paused = !isRunning;
            });
            if (isRunning) {
                DPGLRenderer.mPendulum.frames = 0;
                deltaT = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, frequency);
            }
            // Keep the state readout live regardless of the play/pause state.
            findViewById(R.id.fps_info).removeCallbacks(timerStateRunnable);
            findViewById(R.id.fps_info).postOnAnimation(timerStateRunnable);

        });

        // This method will be called on the rendering
// thread:
        findViewById(R.id.button_restart).setOnClickListener(v -> mGLView.queueEvent(() -> {
            DPGLRenderer.mPendulum.restart();
            DPGLRenderer.resetAccumBuffer();
            if (useDamping) DPGLRenderer.mPendulum.k = DPSimulationParameters.simParams.k;
            else DPGLRenderer.mPendulum.k = 0.;
        }));

        // This method will be called on the rendering
// thread:
        findViewById(R.id.button_settings).setOnClickListener(v -> mGLView.queueEvent(() -> {
            Intent intentParam = new Intent(DPGLActivity.this, DPParametersActivity.class);
            startActivity(intentParam);
        }));

        findViewById(R.id.button_reset_zoom).setOnClickListener(v -> mGLView.queueEvent(() -> {
            DPGLRenderer.mPendulum.resetZoom();
            DPGLRenderer.resetAccumBuffer();
        }));

        findViewById(R.id.togglebutton_sensor_gravity).setOnClickListener(v -> {
            DPGLRenderer.mPendulum.toggleGravity();
            useDynGravity = !useDynGravity;
            if (useDynGravity)
                mSensorManager.registerListener(DPGLActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
            else mSensorManager.unregisterListener(DPGLActivity.this);
        });

        findViewById(R.id.togglebutton_damping).setOnClickListener(v -> {

            useDamping = !useDamping;
            if (useDamping) DPGLRenderer.mPendulum.k = DPSimulationParameters.simParams.k;
            else DPGLRenderer.mPendulum.k = 0.;
        });

        findViewById(R.id.togglebutton_trace).setOnClickListener(v -> {
            DPSimulationParameters.simParams.showTrajectory = !DPSimulationParameters.simParams.showTrajectory;
            if (DPSimulationParameters.simParams.showTrajectory) {
                // This method will be called on the rendering
// thread:
                mGLView.queueEvent(() -> {
                    DPGLRenderer.mPendulum.clearTrajectory();
                    DPGLRenderer.resetAccumBuffer();
                });
            }
        });

        ((ToggleButton) findViewById(R.id.togglebutton_sensor_gravity)).setChecked(useDynGravity);

        ((ToggleButton) findViewById(R.id.togglebutton_damping)).setChecked(useDamping);

        ((ToggleButton) findViewById(R.id.togglebutton_trace)).setChecked(DPSimulationParameters.simParams.showTrajectory);

        paused = false;

        buttonsAreOff = false;
        timerHandler.postDelayed(timerButtonsOff, buttonsFadeOutTime);
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
            DPGLRenderer.mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
        else if (rotmode == Surface.ROTATION_90)
            DPGLRenderer.mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
        else if (rotmode == Surface.ROTATION_180)
            DPGLRenderer.mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
        else if (rotmode == Surface.ROTATION_270)
            DPGLRenderer.mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
        // Do something with this sensor value.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        if (useDynGravity) mSensorManager.unregisterListener(this);
        paused = true;
        findViewById(R.id.fps_info).removeCallbacks(timerStateRunnable);
        makeButtonsVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setFullScreenMode();
        setFpsMode();

        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        if (DPSimulationParameters.simParams.showTrajectory && !((ToggleButton) findViewById(R.id.togglebutton_trace)).isChecked())
            // This method will be called on the rendering
// thread:
            mGLView.queueEvent(() -> DPGLRenderer.mPendulum.clearTrajectory());
        ((ToggleButton) findViewById(R.id.togglebutton_trace)).setChecked(DPSimulationParameters.simParams.showTrajectory);

        // This method will be called on the rendering
// thread:
        mGLView.queueEvent(() -> {
            DPGLRenderer.mPendulum.setColorPendulum1(DPSimulationParameters.simParams.pendulumColor);
            DPGLRenderer.mPendulum.setColorPendulum2(DPSimulationParameters.simParams.pendulumColor2);
        });

        mGLView.onResume();
        if (useDynGravity)
            mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);

        makeButtonsVisible();

        paused = false;
        // Keep the state readout live whenever the activity is visible.
        findViewById(R.id.fps_info).removeCallbacks(timerStateRunnable);
        findViewById(R.id.fps_info).postOnAnimation(timerStateRunnable);
        if (isRunning) {
            DPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, frequency);

            if (!buttonsAreOff) {
                timerHandler.removeCallbacks(timerButtonsOff);
                timerHandler.postDelayed(timerButtonsOff, buttonsFadeOutTime);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        paused = true;
        findViewById(R.id.fps_info).removeCallbacks(timerStateRunnable);
    }

    protected void setFullScreenMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        View decorView = getWindow().getDecorView();
        int uiOptions;

        if (full_screen) {

            // Full screen logic
            if (Build.VERSION.SDK_INT == 15) { // Old method for API 15 (Ice Cream Sandwich)
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT <= 18) { // API 16 - 18 (Jellybean)
                // Original code for API 16-18
                uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            } else { // API 19 and above (KitKat and newer)
                // New method for API 19 and above
                uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);

                // Adjust GLSurfaceView's boundaries
                DPGLSurfaceView glSurfaceView = findViewById(R.id.gl_surface_view);
                glSurfaceView.setSystemUiVisibility(uiOptions);
            }
        } else {

            // Full screen logic
            if (Build.VERSION.SDK_INT == 15) { // Old method for API 15 (Ice Cream Sandwich)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT <= 18) { // API 16 - 18 (Jellybean)
                // Original code for disabling full screen in API 16-18
                uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            } else if (Build.VERSION.SDK_INT >= 19) { // API 19 and above (KitKat and newer)
                // New method for disabling full screen in API 19 and above
                uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);

                // Reset GLSurfaceView's boundaries
                DPGLSurfaceView glSurfaceView = findViewById(R.id.gl_surface_view);
                glSurfaceView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    protected void setFpsMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_fps = sharedPref.getBoolean("pref_fps", false);
        LinearLayout view = findViewById(R.id.fps_layout);
        if (show_fps)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.INVISIBLE);
    }

    /**
     * Builds a multi-line string describing each generalized coordinate of the
     * system, shown alongside the FPS counter. Angular coordinates are shown in
     * degrees (normalized to [0, 360)), linear coordinates in centimeters.
     */
    protected String buildStateInfo(com.ikartehfox.pendulumstudio.common.GenericPendulum pendulum) {
        StringBuilder sb = new StringBuilder();
        int n = pendulum.getNumMasses();
        for (int i = 0; i < n; ++i) {
            if (i > 0) sb.append('\n');
            String name = pendulum.getCoordinateName(i);
            if (pendulum.isCoordinateAngular(i)) {
                double angleDeg = pendulum.getAngle(i) * 180. / Math.PI;
                // Normalize into [0, 360) so the displayed angle stays within bounds.
                angleDeg = ((angleDeg % 360.) + 360.) % 360.;
                sb.append(String.format("%s: %.1f°  ω%d: %.1f°/s", name, angleDeg,
                        i + 1, pendulum.getAngularVelocity(i) * 180. / Math.PI));
            } else {
                sb.append(String.format("%s: %.1f cm  v%d: %.1f cm/s", name,
                        pendulum.getAngle(i),
                        i + 1, pendulum.getAngularVelocity(i)));
            }
        }
        return sb.toString();
    }

    protected void makeButtonsVisible() {
        timerHandler.removeCallbacks(timerButtonsOff);
        findViewById(R.id.DP_buttons).setAlpha(1f);
        findViewById(R.id.DP_buttons).setVisibility(View.VISIBLE);
        buttonsAreOff = false;
    }
}
