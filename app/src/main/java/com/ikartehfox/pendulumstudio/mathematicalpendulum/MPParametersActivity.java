package com.ikartehfox.pendulumstudio.mathematicalpendulum;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;

import com.ikartehfox.pendulumstudio.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MPParametersActivity extends Activity {
    private int settingsEvent, pendulumColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mathematicalpendulum_parameters);
        readParameters();
        settingsEvent = -1;
    }

    public void readParameters() {
        EditText editparam = findViewById(R.id.MP_editl);
        editparam.setText(Float.toString(MPSimulationParameters.simParams.l));
        editparam = findViewById(R.id.MP_editm);
        editparam.setText(Float.toString(MPSimulationParameters.simParams.m));
        ((TextView) findViewById(R.id.MP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.MP_label_g)));
        editparam = findViewById(R.id.MP_editg);
        editparam.setText(Float.toString(MPSimulationParameters.simParams.g / 100.f));
        editparam = findViewById(R.id.MP_editk);
        editparam.setText(Float.toString(MPSimulationParameters.simParams.k * 1.e3f));

        RadioButton rbrand = findViewById(R.id.MP_radioRand);
        rbrand.setChecked(MPSimulationParameters.simParams.initRandom);
        rbrand = findViewById(R.id.MP_radioFixed);
        rbrand.setChecked(!MPSimulationParameters.simParams.initRandom);

        ((TextView) findViewById(R.id.MP_label_th0)).setText(Html.fromHtml(getResources().getString(R.string.MP_label_th0)));
        editparam = findViewById(R.id.MP_editth0);
        editparam.setText(Float.toString((float) (MPSimulationParameters.simParams.th0 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.MP_label_thv0)).setText(Html.fromHtml(getResources().getString(R.string.MP_label_thv0)));
        editparam = findViewById(R.id.MP_editthv0);
        editparam.setText(Float.toString((float) (MPSimulationParameters.simParams.thv0 * 180.f / Math.PI)));

        CheckBox checkTraj = findViewById(R.id.MP_checkBoxTraj);
        checkTraj.setChecked(MPSimulationParameters.simParams.showTrajectory);

        CheckBox checkTrajInfinite = findViewById(R.id.MP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(MPSimulationParameters.simParams.infiniteTrajectory);

        editparam = findViewById(R.id.MP_edittrajle);
        editparam.setText(Integer.toString(MPSimulationParameters.simParams.traceLength));

        pendulumColor = MPSimulationParameters.simParams.pendulumColor;
        Button PColor = findViewById(R.id.MP_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        boolean full_screen = sharedPref.getBoolean("pref_fullscreen", false);
        CheckBox checkFullScreen = findViewById(R.id.pref_fullscreen_loc);
        checkFullScreen.setChecked(full_screen);

        boolean fps = sharedPref.getBoolean("pref_fps", false);
        CheckBox checkFps = findViewById(R.id.pref_fps_loc);
        checkFps.setChecked(fps);

        boolean fade = sharedPref.getBoolean("pref_buttons_fade", true);
        CheckBox checkFade = findViewById(R.id.pref_buttons_fade_loc);
        checkFade.setChecked(fade);
    }


    public void okButton(View v) {
        EditText editparam = findViewById(R.id.MP_editl);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            MPSimulationParameters.simParams.l = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.MP_editm);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            MPSimulationParameters.simParams.m = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.MP_editg);
        if (!editparam.getText().toString().isEmpty())
            MPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
        editparam = findViewById(R.id.MP_editk);
        if (!editparam.getText().toString().isEmpty())
            MPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString()) / 1.e3f;

        RadioButton rbrand = findViewById(R.id.MP_radioRand);
        MPSimulationParameters.simParams.initRandom = rbrand.isChecked();

        editparam = findViewById(R.id.MP_editth0);
        if (!editparam.getText().toString().isEmpty())
            MPSimulationParameters.simParams.th0 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.MP_editthv0);
        if (!editparam.getText().toString().isEmpty())
            MPSimulationParameters.simParams.thv0 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        CheckBox checkTraj = findViewById(R.id.MP_checkBoxTraj);
        MPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = findViewById(R.id.MP_checkBoxTrajInfinite);
        MPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();

        editparam = findViewById(R.id.MP_edittrajle);
        if (!editparam.getText().toString().isEmpty()) {
            int trlength = 0;
            try {
                trlength = Integer.parseInt(editparam.getText().toString());
            } catch (NumberFormatException e) {
                trlength = -1;
            }
            if (trlength > 100000) {
                trlength = 100000;
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.TraceTooLong);//"Trace too long! Setting to 100000...";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (trlength > 0)
                MPSimulationParameters.simParams.traceLength = trlength;
            else {
                MPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        MPSimulationParameters.simParams.pendulumColor = pendulumColor;

        MPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(MPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.apply();

        MPParametersActivity.this.finish();
    }

    public void cancelButton(View v) {
        MPParametersActivity.this.finish();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            pendulumColor = colorDialog.getColor();
            Button PColor = findViewById(R.id.MP_PendulumColor);
            PColor.setBackgroundColor(pendulumColor);
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> {
            //Nothing to do here.
        });
        colorDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsEvent == 1) {
            Button PColor = findViewById(R.id.MP_PendulumColor);
            PColor.setBackgroundColor(pendulumColor);
        }
        settingsEvent = -1;
    }

    public void resetButton(View v) {
        MPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(MPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

}
