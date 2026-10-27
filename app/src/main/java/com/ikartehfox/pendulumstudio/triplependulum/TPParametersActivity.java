package com.ikartehfox.pendulumstudio.triplependulum;

import com.ikartehfox.pendulumstudio.R;

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

import androidx.appcompat.app.AppCompatActivity;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;

public class TPParametersActivity extends AppCompatActivity {
    private int settingsEvent, pendulumColor, pendulumColor2, pendulumColor3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.triplependulum_parameters);
        readParameters();
    }

    public void readParameters() {
        EditText editparam = findViewById(R.id.TP_editl);
        ((TextView) findViewById(R.id.TP_labell1)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_l)));
        editparam.setText(Float.toString(TPSimulationParameters.simParams.l1));
        editparam = findViewById(R.id.TP_editl2);
        ((TextView) findViewById(R.id.TP_labell2)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_l2)));
        editparam.setText(Float.toString(TPSimulationParameters.simParams.l2));
        editparam = findViewById(R.id.TP_editl3);
        ((TextView) findViewById(R.id.TP_labell3)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_l3)));
        editparam.setText(Float.toString(TPSimulationParameters.simParams.l3));
        editparam = findViewById(R.id.TP_editm);
        ((TextView) findViewById(R.id.TP_labelm1)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_m)));
        editparam.setText(Float.toString(TPSimulationParameters.simParams.m1));
        editparam = findViewById(R.id.TP_editm2);
        ((TextView) findViewById(R.id.TP_labelm2)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_m2)));
        editparam.setText(Float.toString(TPSimulationParameters.simParams.m2));
        editparam = findViewById(R.id.TP_editm3);
        ((TextView) findViewById(R.id.TP_labelm3)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_m3)));
        editparam.setText(Float.toString(TPSimulationParameters.simParams.m3));
        ((TextView) findViewById(R.id.TP_labelg)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_g)));
        editparam = findViewById(R.id.TP_editg);
        editparam.setText(Float.toString(TPSimulationParameters.simParams.g / 100.f));
        editparam = findViewById(R.id.TP_editk);
        editparam.setText(Float.toString(TPSimulationParameters.simParams.k * 1.e3f));

        RadioButton rbrand = findViewById(R.id.TP_radioRand);
        rbrand.setChecked(TPSimulationParameters.simParams.initRandom);
        rbrand = findViewById(R.id.TP_radioFixed);
        rbrand.setChecked(!TPSimulationParameters.simParams.initRandom);

        ((TextView) findViewById(R.id.TP_labelth0)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_th0)));
        editparam = findViewById(R.id.TP_editth0);
        editparam.setText(Float.toString((float) (TPSimulationParameters.simParams.th1 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.TP_labelthv0)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_thv0)));
        editparam = findViewById(R.id.TP_editthv0);
        editparam.setText(Float.toString((float) (TPSimulationParameters.simParams.thv1 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.TP_labelth2)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_th2)));
        editparam = findViewById(R.id.TP_editth2);
        editparam.setText(Float.toString((float) (TPSimulationParameters.simParams.th2 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.TP_labelthv2)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_thv2)));
        editparam = findViewById(R.id.TP_editthv2);
        editparam.setText(Float.toString((float) (TPSimulationParameters.simParams.thv2 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.TP_labelth3)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_th3)));
        editparam = findViewById(R.id.TP_editth3);
        editparam.setText(Float.toString((float) (TPSimulationParameters.simParams.th3 * 180.f / Math.PI)));
        ((TextView) findViewById(R.id.TP_labelthv3)).setText(Html.fromHtml(getResources().getString(R.string.TP_label_thv3)));
        editparam = findViewById(R.id.TP_editthv3);
        editparam.setText(Float.toString((float) (TPSimulationParameters.simParams.thv3 * 180.f / Math.PI)));

        CheckBox checkTraj = findViewById(R.id.TP_checkBoxTraj);
        checkTraj.setChecked(TPSimulationParameters.simParams.showTrajectory);

        CheckBox checkTrajInfinite = findViewById(R.id.TP_checkBoxTrajInfinite);
        checkTrajInfinite.setChecked(TPSimulationParameters.simParams.infiniteTrajectory);

        editparam = findViewById(R.id.TP_edittrajle);
        editparam.setText(Integer.toString(TPSimulationParameters.simParams.traceLength));

        pendulumColor = TPSimulationParameters.simParams.pendulumColor;
        Button PColor = findViewById(R.id.TP_PendulumColor);
        PColor.setBackgroundColor(pendulumColor);

        pendulumColor2 = TPSimulationParameters.simParams.pendulumColor2;
        Button PColor2 = findViewById(R.id.TP_PendulumColor2);
        PColor2.setBackgroundColor(pendulumColor2);

        pendulumColor3 = TPSimulationParameters.simParams.pendulumColor3;
        Button PColor3 = findViewById(R.id.TP_PendulumColor3);
        PColor3.setBackgroundColor(pendulumColor3);

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

        boolean touch = sharedPref.getBoolean("pref_touch", true);
        CheckBox checkTouch = findViewById(R.id.pref_touch_loc);
        checkTouch.setChecked(touch);
    }


    public void okButton(View v) {
        EditText editparam = findViewById(R.id.TP_editl);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            TPSimulationParameters.simParams.l1 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.TP_editl2);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            TPSimulationParameters.simParams.l2 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.TP_editl3);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            TPSimulationParameters.simParams.l3 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.TP_editm);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            TPSimulationParameters.simParams.m1 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.TP_editm2);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            TPSimulationParameters.simParams.m2 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.TP_editm3);
        if (!editparam.getText().toString().isEmpty() && Float.parseFloat(editparam.getText().toString()) > 0.)
            TPSimulationParameters.simParams.m3 = Float.parseFloat(editparam.getText().toString());
        editparam = findViewById(R.id.TP_editg);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.g = Float.parseFloat(editparam.getText().toString()) * 100.f;
        editparam = findViewById(R.id.TP_editk);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.k = Float.parseFloat(editparam.getText().toString()) / 1.e3f;

        RadioButton rbrand = findViewById(R.id.TP_radioRand);
        TPSimulationParameters.simParams.initRandom = rbrand.isChecked();

        editparam = findViewById(R.id.TP_editth0);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.th1 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.TP_editthv0);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.thv1 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        editparam = findViewById(R.id.TP_editth2);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.th2 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.TP_editthv2);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.thv2 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        editparam = findViewById(R.id.TP_editth3);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.th3 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);
        editparam = findViewById(R.id.TP_editthv3);
        if (!editparam.getText().toString().isEmpty())
            TPSimulationParameters.simParams.thv3 = (float) (Float.parseFloat(editparam.getText().toString()) *
                    Math.PI / 180.f);

        CheckBox checkTraj = findViewById(R.id.TP_checkBoxTraj);
        TPSimulationParameters.simParams.showTrajectory = checkTraj.isChecked();

        CheckBox checkTrajInfinite = findViewById(R.id.TP_checkBoxTrajInfinite);
        TPSimulationParameters.simParams.infiniteTrajectory = checkTrajInfinite.isChecked();

        editparam = findViewById(R.id.TP_edittrajle);
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
                CharSequence text = getString(R.string.TraceTooLong);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (trlength > 0)
                TPSimulationParameters.simParams.traceLength = trlength;
            else {
                TPSimulationParameters.simParams.infiniteTrajectory = true;
            }
        }

        TPSimulationParameters.simParams.pendulumColor = pendulumColor;
        TPSimulationParameters.simParams.pendulumColor2 = pendulumColor2;
        TPSimulationParameters.simParams.pendulumColor3 = pendulumColor3;

        TPSimulationParameters.simParams.writeSettings(this.getSharedPreferences(TPSimulationParameters.PREFS_NAME, 0));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox checkFullScreen = findViewById(R.id.pref_fullscreen_loc);
        editor.putBoolean("pref_fullscreen", checkFullScreen.isChecked());
        CheckBox checkFps = findViewById(R.id.pref_fps_loc);
        editor.putBoolean("pref_fps", checkFps.isChecked());
        CheckBox checkFade = findViewById(R.id.pref_buttons_fade_loc);
        editor.putBoolean("pref_buttons_fade", checkFade.isChecked());
        CheckBox checkTouch = findViewById(R.id.pref_touch_loc);
        editor.putBoolean("pref_touch", checkTouch.isChecked());
        editor.apply();

        TPParametersActivity.this.finish();
    }

    public void cancelButton(View v) {
        TPParametersActivity.this.finish();
    }

    public void resetButton(View v) {
        TPSimulationParameters.simParams.clearSettings(this.getSharedPreferences(TPSimulationParameters.PREFS_NAME, 0));
        readParameters();
    }

    public void PendulumColor(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, pendulumColor);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            pendulumColor = colorDialog.getColor();
            Button PColor = findViewById(R.id.TP_PendulumColor);
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
            Button PColor = findViewById(R.id.TP_PendulumColor2);
            PColor.setBackgroundColor(pendulumColor2);
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> {
            //Nothing to do here.
        });
        colorDialog.show();
    }

    public void PendulumColor3(View v) {
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, pendulumColor3);
        colorDialog.setTitle(R.string.pick_color);

        colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialog, which) -> {
            pendulumColor3 = colorDialog.getColor();
            Button PColor = findViewById(R.id.TP_PendulumColor3);
            PColor.setBackgroundColor(pendulumColor3);
        });

        colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialog, which) -> {
            //Nothing to do here.
        });
        colorDialog.show();
    }
}
