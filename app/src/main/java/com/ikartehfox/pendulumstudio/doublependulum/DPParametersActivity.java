package com.ikartehfox.pendulumstudio.doublependulum;

import com.ikartehfox.pendulumstudio.R;
//import com.ikartehfox.pendulumstudio.mathematicalpendulum.MPSimulationParameters;

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

public class DPParametersActivity extends Activity {
    private int settingsEvent, pendulumColor, pendulumColor2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doublependulum_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = findViewById(R.id.DP_editl);
        ((TextView) findViewById(R.id.DP_labell1)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_l)));
        editparam.setText(Float.toString(DPSimulationParameters.simParams.l1));
        editparam = findViewById(R.id.DP_editl2);
        ((TextView) findViewById(R.id.DP_labell2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_l2)));
        editparam.setText(Float.toString(DPSimulationParameters.simParams.l2));
        editparam = findViewById(R.id.DP_editm);
        ((TextView) findViewById(R.id.DP_labelm1)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_m)));
        editparam.setText(Float.toString(DPSimulationParameters.simParams.m1));
        editparam = findViewById(R.id.DP_editm2);
        ((TextView) findViewById(R.id.DP_labelm2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_m2)));
        editparam.setText(Float.toString(DPSimulationParameters.simParams.m2));
        ((TextView) findViewById(R.id.DP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_g)));
        editparam = findViewById(R.id.DP_editg);
        editparam.setText(Float.toString(DPSimulationParameters.simParams.g / 100.f));
        editparam = findViewById(R.id.DP_editk);
        editparam.setText(Float.toString(DPSimulationParameters.simParams.k * 1.e3f));

        RadioButton rbrand = findViewById(R.id.DP_radioRand);
        rbrand.setChecked(DPSimulationParameters.simParams.initRandom);
        rbrand = findViewById(R.id.DP_radioFixed);
        rbrand.setChecked(!DPSimulationParameters.simParams.initRandom);

        ((TextView) findViewById(R.id.DP_labelth0)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_th0)));
        editparam = findViewById(R.id.DP_editth0);
        editparam.setText(Float.toString((float) (DPSimulationParameters.simParams.th1 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.DP_labelthv0)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_thv0)));
        editparam = findViewById(R.id.DP_editthv0);
        editparam.setText(Float.toString((float) (DPSimulationParameters.simParams.thv1 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.DP_labelth2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_th2)));
        editparam = findViewById(R.id.DP_editth2);
        editparam.setText(Float.toString((float) (DPSimulationParameters.simParams.th2 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.DP_labelthv2)).setText(Html.fromHtml(getResources().getString(R.string.DSP_label_thv2)));
        editparam = findViewById(R.id.DP_editthv2);
        editparam.setText(Float.toString((float) (DPSimulationParameters.simParams.thv2 * 180.f / Math.PI)));

        CheckBox checkTraj = findViewById(R.id.DP_checkBoxTraj);
        checkTraj.setChecked(DPSimulationParameters.simParams.showTrajectory);

        CheckBox checkTrajInfinite = findViewById(R.id.DP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(DPSimulationParameters.simParams.infiniteTrajectory);

        editparam = findViewById(R.id.DP_edittrajle);
        editparam.setText(Integer.toString(DPSimulationParameters.simParams.traceLength));

        pendulumColor = DPSimulationParameters.simParams.pendulumColor;
        Button PColor = findViewById(R.id.DP_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);

        pendulumColor2 = DPSimulationParameters.simParams.pendulumColor2;
        Button PColor2 = findViewById(R.id.DP_PendulumColor2);
        PColor2.setBackgroundColor(pendulumColor2);

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
        EditText editparam = findViewById(R.id.DP_editl);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            DPSimulationParameters.simParams.l1 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.DP_editl2);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            DPSimulationParameters.simParams.l2 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.DP_editm);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            DPSimulationParameters.simParams.m1 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.DP_editm2);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            DPSimulationParameters.simParams.m2 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.DP_editg);
        if (!editparam.getText().toString().isEmpty())
            DPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
        editparam = findViewById(R.id.DP_editk);
        if (!editparam.getText().toString().isEmpty())
            DPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString()) / 1.e3f;

        RadioButton rbrand = findViewById(R.id.DP_radioRand);
        DPSimulationParameters.simParams.initRandom = rbrand.isChecked();

        editparam = findViewById(R.id.DP_editth0);
        if (!editparam.getText().toString().isEmpty())
            DPSimulationParameters.simParams.th1 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.DP_editthv0);
        if (!editparam.getText().toString().isEmpty())
            DPSimulationParameters.simParams.thv1 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        editparam = findViewById(R.id.DP_editth2);
        if (!editparam.getText().toString().isEmpty())
            DPSimulationParameters.simParams.th2 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.DP_editthv2);
        if (!editparam.getText().toString().isEmpty())
            DPSimulationParameters.simParams.thv2 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        CheckBox checkTraj = findViewById(R.id.DP_checkBoxTraj);
        DPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = findViewById(R.id.DP_checkBoxTrajInfinite);
        DPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();

        editparam = findViewById(R.id.DP_edittrajle);
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
                DPSimulationParameters.simParams.traceLength = trlength;
            else {
                DPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        DPSimulationParameters.simParams.pendulumColor = pendulumColor;
        DPSimulationParameters.simParams.pendulumColor2 = pendulumColor2;

        DPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(DPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        editor.apply();

        DPParametersActivity.this.finish();
    }

    public void cancelButton(View v) {
        DPParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        DPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(DPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            pendulumColor = colorDialog.getColor();
            Button PColor = findViewById(R.id.DP_PendulumColor);
            PColor.setBackgroundColor(pendulumColor);
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> {
            //Nothing to do here.
        });
        colorDialog.show();
    }

    public void PendulumColor2(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, pendulumColor2);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            pendulumColor2 = colorDialog.getColor();
            Button PColor = findViewById(R.id.DP_PendulumColor2);
            PColor.setBackgroundColor(pendulumColor2);
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> {
            //Nothing to do here.
        });
        colorDialog.show();
    }
}
