package com.ikartehfox.pendulumstudio.sphericalpendulum;

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

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;

public class SPParametersActivity extends Activity {
    private int settingsEvent, pendulumColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sphericalpendulum_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = findViewById(R.id.SP_editl);
        editparam.setText(Float.toString(SPSimulationParameters.simParams.l));
        editparam = findViewById(R.id.SP_editm);
        editparam.setText(Float.toString(SPSimulationParameters.simParams.m));
        ((TextView) findViewById(R.id.SP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.SP_label_g)));
        editparam = findViewById(R.id.SP_editg);
        editparam.setText(Float.toString(SPSimulationParameters.simParams.g / 100.f));
        editparam = findViewById(R.id.SP_editk);
        editparam.setText(Float.toString(SPSimulationParameters.simParams.k * 1.e3f));

        RadioButton rbrand = findViewById(R.id.SP_radioRand);
        rbrand.setChecked(SPSimulationParameters.simParams.initRandom);
        rbrand = findViewById(R.id.SP_radioFixed);
        rbrand.setChecked(!SPSimulationParameters.simParams.initRandom);

        ((TextView) findViewById(R.id.SP_label_th0)).setText(Html.fromHtml(getResources().getString(R.string.SP_label_th0)));
        editparam = findViewById(R.id.SP_editth0);
        editparam.setText(Float.toString((float) (SPSimulationParameters.simParams.th0 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.SP_label_thv0)).setText(Html.fromHtml(getResources().getString(R.string.SP_label_thv0)));
        editparam = findViewById(R.id.SP_editthv0);
        editparam.setText(Float.toString((float) (SPSimulationParameters.simParams.thv0 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.SP_label_ph0)).setText(Html.fromHtml(getResources().getString(R.string.SP_label_ph0)));
        editparam = findViewById(R.id.SP_editph0);
        editparam.setText(Float.toString((float) (SPSimulationParameters.simParams.ph0 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.SP_label_phv0)).setText(Html.fromHtml(getResources().getString(R.string.SP_label_phv0)));
        editparam = findViewById(R.id.SP_editphv0);
        editparam.setText(Float.toString((float) (SPSimulationParameters.simParams.phv0 * 180.f / Math.PI)));

        CheckBox checkTraj = findViewById(R.id.SP_checkBoxTraj);
        checkTraj.setChecked(SPSimulationParameters.simParams.showTrajectory);

        editparam = findViewById(R.id.SP_edittrajle);
        editparam.setText(Integer.toString(SPSimulationParameters.simParams.traceLength));

        CheckBox checkTrajInfinite = findViewById(R.id.SP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(SPSimulationParameters.simParams.infiniteTrajectory);

        pendulumColor = SPSimulationParameters.simParams.pendulumColor;
        Button PColor = findViewById(R.id.SP_PendulumColor);
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
        EditText editparam = findViewById(R.id.SP_editl);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            SPSimulationParameters.simParams.l = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.SP_editm);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            SPSimulationParameters.simParams.m = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.SP_editg);
        if (!editparam.getText().toString().isEmpty())
            SPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
        editparam = findViewById(R.id.SP_editk);
        if (!editparam.getText().toString().isEmpty())
            SPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString()) / 1.e3f;

        RadioButton rbrand = findViewById(R.id.SP_radioRand);
        SPSimulationParameters.simParams.initRandom = rbrand.isChecked();

        editparam = findViewById(R.id.SP_editth0);
        if (!editparam.getText().toString().isEmpty())
            SPSimulationParameters.simParams.th0 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.SP_editthv0);
        if (!editparam.getText().toString().isEmpty())
            SPSimulationParameters.simParams.thv0 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.SP_editph0);
        if (!editparam.getText().toString().isEmpty())
            SPSimulationParameters.simParams.ph0 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.SP_editphv0);
        if (!editparam.getText().toString().isEmpty())
            SPSimulationParameters.simParams.phv0 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        CheckBox checkTraj = findViewById(R.id.SP_checkBoxTraj);
        SPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = findViewById(R.id.SP_checkBoxTrajInfinite);
        SPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();

        editparam = findViewById(R.id.SP_edittrajle);
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
                SPSimulationParameters.simParams.traceLength = trlength;
            else {
                SPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        SPSimulationParameters.simParams.pendulumColor = pendulumColor;

        SPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(SPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.apply();

        SPParametersActivity.this.finish();
    }

    public void cancelButton(View v) {
        SPParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        SPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(SPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            pendulumColor = colorDialog.getColor();
            Button PColor = findViewById(R.id.SP_PendulumColor);
            PColor.setBackgroundColor(pendulumColor);
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> {
            //Nothing to do here.
        });
        colorDialog.show();
    }
}
