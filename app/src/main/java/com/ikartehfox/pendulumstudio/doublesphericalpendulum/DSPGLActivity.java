package com.ikartehfox.pendulumstudio.doublesphericalpendulum;

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


public class DSPGLActivity extends AppCompatActivity implements SensorEventListener {
    private DSPGLSurfaceView mGLView;
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
            float fps = DSPGLRenderer.mPendulum.frames / (float) (deltaT) * 1.e3f;
            ((TextView) findViewById(R.id.fps)).setText("FPS: " + String.format("%.0f", fps));
            DSPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            if (isRunning && !paused) timerHandler.postDelayed(this, frequency);
        }
    };

    boolean buttonsAreOff;
    final Runnable timerButtonsOff = new Runnable() {
        @Override
        public void run() {
            if (paused || !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("pref_buttons_fade", true))
                return;

            findViewById(R.id.DSP_buttons).animate()
                    .alpha(0f)
                    .setDuration(buttonsFadeAnimationTime)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            findViewById(R.id.DSP_buttons).setVisibility(View.GONE);
                            buttonsAreOff = true;
                        }
                    });

        }
    };

    final Runnable timerButtonsOn = new Runnable() {
        @Override
        public void run() {
            //Log.d("Act","ButtonsOn");
            findViewById(R.id.DSP_buttons).setAlpha(0f);
            findViewById(R.id.DSP_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.DSP_buttons).animate()
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
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.doublesphericalpendulum_gl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setFullScreenMode();
        setFpsMode();

        mGLView = findViewById(R.id.DSP_gl_surface_view);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        useDynGravity = DSPGLRenderer.mPendulum.dynamicGravity;

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        useDamping = Math.abs(DSPGLRenderer.mPendulum.k) > 1.e-7;


        isRunning = !DSPGLRenderer.mPendulum.paused;
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
                DSPGLRenderer.mPendulum.paused = !isRunning;
            });
            if (isRunning) {
                DSPGLRenderer.mPendulum.frames = 0;
                deltaT = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, frequency);
            }

        });

        // This method will be called on the rendering
// thread:
        findViewById(R.id.button_restart).setOnClickListener(v -> mGLView.queueEvent(() -> {
            DSPGLRenderer.mPendulum.restart();
            DSPGLRenderer.resetAccumBuffer();
            if (useDamping) DSPGLRenderer.mPendulum.k = DSPSimulationParameters.simParams.k;
            else DSPGLRenderer.mPendulum.k = 0.;
        }));

        // This method will be called on the rendering
// thread:
        findViewById(R.id.button_settings).setOnClickListener(v -> mGLView.queueEvent(() -> {
            Intent intentParam = new Intent(DSPGLActivity.this, DSPParametersActivity.class);
            startActivity(intentParam);
        }));

        findViewById(R.id.togglebutton_sensor_gravity).setOnClickListener(v -> {
            DSPGLRenderer.mPendulum.toggleGravity();
            useDynGravity = !useDynGravity;
            if (useDynGravity)
                mSensorManager.registerListener(DSPGLActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
            else mSensorManager.unregisterListener(DSPGLActivity.this);
//                updateGravitySensorStatus();
        });

        findViewById(R.id.togglebutton_damping).setOnClickListener(v -> {

            useDamping = !useDamping;
            if (useDamping) DSPGLRenderer.mPendulum.k = DSPSimulationParameters.simParams.k;
            else DSPGLRenderer.mPendulum.k = 0.;
        });

        findViewById(R.id.togglebutton_trace).setOnClickListener(v -> {
            //MPGLRenderer.mPendulum.trmd = !MPGLRenderer.mPendulum.trmd;
            DSPSimulationParameters.simParams.showTrajectory = !DSPSimulationParameters.simParams.showTrajectory;
            if (DSPSimulationParameters.simParams.showTrajectory) {
                // This method will be called on the rendering
// thread:
                mGLView.queueEvent(() -> {
                    DSPGLRenderer.mPendulum.clearTrajectory();
                    DSPGLRenderer.resetAccumBuffer();
                });
            }
        });

        ((ToggleButton) findViewById(R.id.togglebutton_sensor_gravity)).setChecked(useDynGravity);

        ((ToggleButton) findViewById(R.id.togglebutton_damping)).setChecked(useDamping);

        ((ToggleButton) findViewById(R.id.togglebutton_trace)).setChecked(DSPSimulationParameters.simParams.showTrajectory);

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
            DSPGLRenderer.mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
        else if (rotmode == Surface.ROTATION_90)
            DSPGLRenderer.mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
        else if (rotmode == Surface.ROTATION_180)
            DSPGLRenderer.mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
        else if (rotmode == Surface.ROTATION_270)
            DSPGLRenderer.mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
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
        makeButtonsVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setFullScreenMode();
        setFpsMode();

        if (DSPSimulationParameters.simParams.showTrajectory && !((ToggleButton) findViewById(R.id.togglebutton_trace)).isChecked())
            // This method will be called on the rendering
// thread:
            mGLView.queueEvent(() -> DSPGLRenderer.mPendulum.clearTrajectory());
        ((ToggleButton) findViewById(R.id.togglebutton_trace)).setChecked(DSPSimulationParameters.simParams.showTrajectory);

        // This method will be called on the rendering
// thread:
        mGLView.queueEvent(() -> {
            DSPGLRenderer.mPendulum.setColorPendulum1(DSPSimulationParameters.simParams.pendulumColor);
            DSPGLRenderer.mPendulum.setColorPendulum2(DSPSimulationParameters.simParams.pendulumColor2);
        });

        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        if (useDynGravity)
            mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);

        makeButtonsVisible();

        paused = false;
        if (isRunning) {
            DSPGLRenderer.mPendulum.frames = 0;
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
                DSPGLSurfaceView glSurfaceView = findViewById(R.id.DSP_gl_surface_view);
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
                DSPGLSurfaceView glSurfaceView = findViewById(R.id.DSP_gl_surface_view);
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

    protected void makeButtonsVisible() {
        timerHandler.removeCallbacks(timerButtonsOff);
        findViewById(R.id.DSP_buttons).setAlpha(1f);
        findViewById(R.id.DSP_buttons).setVisibility(View.VISIBLE);
        buttonsAreOff = false;
    }
}
