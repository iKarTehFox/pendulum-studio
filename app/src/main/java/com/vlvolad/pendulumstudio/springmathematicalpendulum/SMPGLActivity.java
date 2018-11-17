package com.vlvolad.pendulumstudio.springmathematicalpendulum;

import com.vlvolad.pendulumstudio.InformationActivity;
import com.vlvolad.pendulumstudio.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class SMPGLActivity extends Activity implements SensorEventListener {

    private static final String TAG = "SMPGLActivity";
    private SMPGLSurfaceView mGLView;
	private SensorManager mSensorManager;
	private Sensor mGravity;
	private boolean useDynGravity;
	private boolean useDamping;
    private boolean isRunning;
	private Display display;

    static int frequency = 1000;
    private boolean paused;
    private long deltaT;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            deltaT = System.currentTimeMillis() - deltaT;
            float fps = SMPGLRenderer.mPendulum.frames / (float)(deltaT) * 1.e3f;
            ((TextView)findViewById(R.id.fps)).setText("FPS: " + String.format("%.0f", fps));
            SMPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            if (isRunning && !paused) timerHandler.postDelayed(this, frequency);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.springmathematicalpendulum_gl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setFullScreenMode();
        setFpsMode();
        
        mGLView = (SMPGLSurfaceView)findViewById(R.id.gl_surface_view);
        
        
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        useDynGravity = SMPGLRenderer.mPendulum.dynamicGravity;
        
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        if (Math.abs(SMPGLRenderer.mPendulum.gam)>1.e-7) useDamping = true;
        else useDamping = false;

        isRunning = !SMPGLRenderer.mPendulum.paused;
        if (!isRunning) ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
        else ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
        findViewById(R.id.button_playpause).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_play);
                else ((ImageButton)findViewById(R.id.button_playpause)).setImageResource(R.drawable.ic_action_pause);
                isRunning = !isRunning;
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        //MPGLRenderer.mPendulum.restart();
                        if (!isRunning) SMPGLRenderer.mPendulum.paused = true;
                        else SMPGLRenderer.mPendulum.paused = false;
                    }});
                if (isRunning) {
                    SMPGLRenderer.mPendulum.frames = 0;
                    deltaT = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, frequency);
                }

            }
        });
        
        findViewById(R.id.button_restart).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                    	SMPGLRenderer.mPendulum.restart();
                        SMPGLRenderer.resetAccumBuffer();
                    	if (useDamping) SMPGLRenderer.mPendulum.gam = SMPSimulationParameters.simParams.gam;
                        else SMPGLRenderer.mPendulum.gam = 0.;
                    }});
			}
		});

        findViewById(R.id.button_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLView.queueEvent(new Runnable() {
                    // This method will be called on the rendering
                    // thread:
                    public void run() {
                        Intent intentParam = new Intent(SMPGLActivity.this, SMPParametersActivity.class);
                        startActivity(intentParam);
                    }});
            }
        });
        
        findViewById(R.id.togglebutton_sensor_gravity).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SMPGLRenderer.mPendulum.toggleGravity();
                useDynGravity = !useDynGravity;
                if (useDynGravity) mSensorManager.registerListener(SMPGLActivity.this, mGravity, SensorManager.SENSOR_DELAY_GAME);
                else mSensorManager.unregisterListener(SMPGLActivity.this);
			}
		});
        
        findViewById(R.id.togglebutton_damping).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                useDamping = !useDamping;
                if (useDamping) SMPGLRenderer.mPendulum.gam = SMPSimulationParameters.simParams.gam;
                else SMPGLRenderer.mPendulum.gam = 0.;
			}
		});

        findViewById(R.id.togglebutton_trace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SMPSimulationParameters.simParams.showTrajectory = !SMPSimulationParameters.simParams.showTrajectory;
                if (SMPSimulationParameters.simParams.showTrajectory) {
                    mGLView.queueEvent(new Runnable() {
                        // This method will be called on the rendering
                        // thread:
                        public void run() {
                            SMPGLRenderer.mPendulum.clearTrajectory();
                            SMPGLRenderer.resetAccumBuffer();
                        }
                    });
                }
            }
        });
        
        if (!useDynGravity) ((ToggleButton)findViewById(R.id.togglebutton_sensor_gravity)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_sensor_gravity)).setChecked(true);
        
        if (!useDamping) ((ToggleButton)findViewById(R.id.togglebutton_damping)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_damping)).setChecked(true);

        if (!SMPSimulationParameters.simParams.showTrajectory) ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(true);

        paused = false;
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
      if (rotmode==Surface.ROTATION_0) SMPGLRenderer.mPendulum.setGravity(event.values[0], event.values[1], event.values[2]);
      else if (rotmode==Surface.ROTATION_90) SMPGLRenderer.mPendulum.setGravity(-event.values[1], event.values[0], event.values[2]);
      else if (rotmode==Surface.ROTATION_180) SMPGLRenderer.mPendulum.setGravity(event.values[0], -event.values[1], event.values[2]);
      else if (rotmode==Surface.ROTATION_270) SMPGLRenderer.mPendulum.setGravity(event.values[1], -event.values[0], event.values[2]);
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
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        setFullScreenMode();
        setFpsMode();

        if (SMPSimulationParameters.simParams.showTrajectory && !((ToggleButton)findViewById(R.id.togglebutton_trace)).isChecked())
            mGLView.queueEvent(new Runnable() {
                // This method will be called on the rendering
                // thread:
                public void run() {
                    SMPGLRenderer.mPendulum.clearTrajectory();
                }
            });
        if (!SMPSimulationParameters.simParams.showTrajectory) ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(false);
        else ((ToggleButton)findViewById(R.id.togglebutton_trace)).setChecked(true);

        mGLView.queueEvent(new Runnable() {
            // This method will be called on the rendering
            // thread:
            public void run() {
                SMPGLRenderer.mPendulum.setColorPendulum1(SMPSimulationParameters.simParams.pendulumColor);
                SMPGLRenderer.mPendulum.setColorPendulum2(SMPSimulationParameters.simParams.pendulumColor2);
            }
        });

        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        if (useDynGravity) mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);

        paused = false;
        if (isRunning && !paused) {
            SMPGLRenderer.mPendulum.frames = 0;
            deltaT = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, frequency);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.springmathematicalpendulum, menu);
        MenuItem item = menu.findItem(R.id.action_rate);
        item.setVisible(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("rate_clicked", false));
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.SMP_parameters:
            	Intent intentParam = new Intent(SMPGLActivity.this, SMPParametersActivity.class);
                startActivity(intentParam);
                return true;
            case R.id.action_rate:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("rate_clicked", true).apply();
                if(Build.VERSION.SDK_INT >= 11)
                    invalidateOptionsMenu();

                return true;
            case R.id.action_information:
                Intent intent = new Intent(SMPGLActivity.this, InformationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(Build.VERSION.SDK_INT >= 14 && featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        paused = true;
    }

    protected void setFullScreenMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        if (full_screen) {
            if (Build.VERSION.SDK_INT < 16) { //old method
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else { // Jellybean and up, new hotness
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = getActionBar();
                actionBar.hide();
            }
        }
        else {
            if (Build.VERSION.SDK_INT < 16) { //old method
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else { // Jellybean and up, new hotness
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                ActionBar actionBar = getActionBar();
                actionBar.show();
            }
        }
    }

    protected void setFpsMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_fps = sharedPref.getBoolean("pref_fps", false);
        LinearLayout view = (LinearLayout) findViewById(R.id.fps_layout);
        if (show_fps)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.INVISIBLE);
    }
}