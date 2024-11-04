package com.ikartehfox.pendulumstudio;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;

import com.ikartehfox.pendulumstudio.mathematicalpendulum.MPGLActivity;
import com.ikartehfox.pendulumstudio.mathematicalpendulum.MPGLRenderer;
import com.ikartehfox.pendulumstudio.mathematicalpendulum.MPSimulationParameters;
import com.ikartehfox.pendulumstudio.pendulumwave.PWGLActivity;
import com.ikartehfox.pendulumstudio.pendulumwave.PWGLRenderer;
import com.ikartehfox.pendulumstudio.pendulumwave.PWSimulationParameters;
import com.ikartehfox.pendulumstudio.sphericalpendulum.SPGLActivity;
import com.ikartehfox.pendulumstudio.sphericalpendulum.SPGLRenderer;
import com.ikartehfox.pendulumstudio.sphericalpendulum.SPSimulationParameters;
import com.ikartehfox.pendulumstudio.springmathematicalpendulum.SMPGLActivity;
import com.ikartehfox.pendulumstudio.springmathematicalpendulum.SMPGLRenderer;
import com.ikartehfox.pendulumstudio.springmathematicalpendulum.SMPSimulationParameters;
import com.ikartehfox.pendulumstudio.springpendulum2d.SP2DGLActivity;
import com.ikartehfox.pendulumstudio.springpendulum2d.SP2DGLRenderer;
import com.ikartehfox.pendulumstudio.springpendulum2d.SP2DSimulationParameters;
import com.ikartehfox.pendulumstudio.springpendulum3d.SP3DGLActivity;
import com.ikartehfox.pendulumstudio.springpendulum3d.SP3DGLRenderer;
import com.ikartehfox.pendulumstudio.springpendulum3d.SP3DSimulationParameters;
import com.ikartehfox.pendulumstudio.springsphericalpendulum.SSPGLActivity;
import com.ikartehfox.pendulumstudio.springsphericalpendulum.SSPGLRenderer;
import com.ikartehfox.pendulumstudio.springsphericalpendulum.SSPSimulationParameters;
import com.ikartehfox.pendulumstudio.doublependulum.DPGLActivity;
import com.ikartehfox.pendulumstudio.doublependulum.DPGLRenderer;
import com.ikartehfox.pendulumstudio.doublependulum.DPSimulationParameters;
import com.ikartehfox.pendulumstudio.doublesphericalpendulum.DSPGLActivity;
import com.ikartehfox.pendulumstudio.doublesphericalpendulum.DSPGLRenderer;
import com.ikartehfox.pendulumstudio.doublesphericalpendulum.DSPSimulationParameters;

public class MainActivity extends ListActivity {

    private static final String TAG = "MainActivity";
    private static final String ITEM_IMAGE = "item_image";
    private static final String ITEM_TITLE = "item_title";
    private static final String ITEM_SUBTITLE = "item_subtitle";

    //private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.toc);
        setContentView(R.layout.activity_main);

        final List<Map<String, Object>> data = new ArrayList<>();
        final SparseArray<Class<? extends Activity>> activityMapping = new SparseArray<>();

        int i = 0;

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_mp);
            item.put(ITEM_TITLE, getText(R.string.mp));
            item.put(ITEM_SUBTITLE, getText(R.string.mp_subtitle));
            data.add(item);
            activityMapping.put(i++, MPGLActivity.class);

            MPSimulationParameters.simParams.readSettings(this.getSharedPreferences(MPSimulationParameters.PREFS_NAME, 0));
            MPGLRenderer.mPendulum.restart();
            MPGLRenderer.mPendulum.k = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_pw);
            item.put(ITEM_TITLE, getText(R.string.pw));
            item.put(ITEM_SUBTITLE, getText(R.string.pw_subtitle));
            data.add(item);
            activityMapping.put(i++, PWGLActivity.class);

            PWSimulationParameters.simParams.readSettings(this.getSharedPreferences(PWSimulationParameters.PREFS_NAME, 0));
            PWGLRenderer.mPendulum.restart();
            PWGLRenderer.mPendulum.k = 0.;
            PWGLRenderer.mPendulum.setDamping(0.);
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_sp);
            item.put(ITEM_TITLE, getText(R.string.sp));
            item.put(ITEM_SUBTITLE, getText(R.string.sp_subtitle));
            data.add(item);
            activityMapping.put(i++, SPGLActivity.class);

            SPSimulationParameters.simParams.readSettings(this.getSharedPreferences(SPSimulationParameters.PREFS_NAME, 0));
            SPGLRenderer.mPendulum.restart();
            SPGLRenderer.mPendulum.k = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_sp2d);
            item.put(ITEM_TITLE, getText(R.string.sp2d));
            item.put(ITEM_SUBTITLE, getText(R.string.sp2d_subtitle));
            data.add(item);
            activityMapping.put(i++, SP2DGLActivity.class);

            SP2DSimulationParameters.simParams.readSettings(this.getSharedPreferences(SP2DSimulationParameters.PREFS_NAME, 0));
            SP2DGLRenderer.mPendulum.restart();
            SP2DGLRenderer.mPendulum.gam = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_sp3d);
            item.put(ITEM_TITLE, getText(R.string.sp3d));
            item.put(ITEM_SUBTITLE, getText(R.string.sp3d_subtitle));
            data.add(item);
            activityMapping.put(i++, SP3DGLActivity.class);

            SP3DSimulationParameters.simParams.readSettings(this.getSharedPreferences(SP3DSimulationParameters.PREFS_NAME, 0));
            SP3DGLRenderer.mPendulum.restart();
            SP3DGLRenderer.mPendulum.gam = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_dp);
            item.put(ITEM_TITLE, getText(R.string.dp));
            item.put(ITEM_SUBTITLE, getText(R.string.dp_subtitle));
            data.add(item);
            activityMapping.put(i++, DPGLActivity.class);

            DPSimulationParameters.simParams.readSettings(this.getSharedPreferences(DPSimulationParameters.PREFS_NAME, 0));
            DPGLRenderer.mPendulum.restart();
            DPGLRenderer.mPendulum.k = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_dsp);
            item.put(ITEM_TITLE, getText(R.string.dsp));
            item.put(ITEM_SUBTITLE, getText(R.string.dsp_subtitle));
            data.add(item);
            activityMapping.put(i++, DSPGLActivity.class);

            DSPSimulationParameters.simParams.readSettings(this.getSharedPreferences(DSPSimulationParameters.PREFS_NAME, 0));
            DSPGLRenderer.mPendulum.restart();
            DSPGLRenderer.mPendulum.k = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_smp);
            item.put(ITEM_TITLE, getText(R.string.smp));
            item.put(ITEM_SUBTITLE, getText(R.string.smp_subtitle));
            data.add(item);
            activityMapping.put(i++, SMPGLActivity.class);

            SMPSimulationParameters.simParams.readSettings(this.getSharedPreferences(SMPSimulationParameters.PREFS_NAME, 0));
            SMPGLRenderer.mPendulum.restart();
            SMPGLRenderer.mPendulum.gam = 0.;
        }

        {
            final Map<String, Object> item = new HashMap<>();
            item.put(ITEM_IMAGE, R.drawable.ic_ssp3d);
            item.put(ITEM_TITLE, getText(R.string.ssp));
            item.put(ITEM_SUBTITLE, getText(R.string.ssp_subtitle));
            data.add(item);
            activityMapping.put(i++, SSPGLActivity.class);

            SSPSimulationParameters.simParams.readSettings(this.getSharedPreferences(SSPSimulationParameters.PREFS_NAME, 0));
            SSPGLRenderer.mPendulum.restart();
            SSPGLRenderer.mPendulum.gam = 0.;
        }

        final SimpleAdapter dataAdapter = new SimpleAdapter(this, data, R.layout.toc_item, new String[]{ITEM_IMAGE, ITEM_TITLE, ITEM_SUBTITLE}, new int[]{R.id.Image, R.id.Title, R.id.SubTitle});
        setListAdapter(dataAdapter);

        getListView().setOnItemClickListener((parent, view, position, id) -> {
            final Class<? extends Activity> activityToLaunch = activityMapping.get(position);

            if (activityToLaunch != null) {
                final Intent launchIntent = new Intent(MainActivity.this, activityToLaunch);
                startActivity(launchIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intentParam;
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getText(R.string.share_subject).toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getText(R.string.share_text).toString());
                startActivity(Intent.createChooser(sharingIntent, getText(R.string.share_via).toString()));
                return true;
            case R.id.action_settings:
                //showHelp();
                intentParam = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentParam);
                return true;
            case R.id.action_information:
                //showHelp();
                intentParam = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(intentParam);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, @NonNull Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
