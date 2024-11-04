package com.ikartehfox.pendulumstudio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class InformationActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        setTitle(R.string.information);

        TextView tver = findViewById(R.id.version_text);
        tver.setText(getText(R.string.version) + " " + BuildConfig.VERSION_NAME);

        TextView tv = findViewById(R.id.github);
        //makeTextViewHyperlink(tv);
        tv.setOnClickListener(v -> {
            String githubUrl = "https://github.com/iKarTehFox/pendulum-studio";

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)));
            } catch (Exception e) {
                Log.d("Information", "Message =" + e);
            }
        });


        TextView tv2 = findViewById(R.id.description);
        if (tv2 != null)
            tv2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView tv5 = findViewById(R.id.share_link);
        //makeTextViewHyperlink(tv5);
        tv5.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.share_subject).toString());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.share_text).toString());
            startActivity(Intent.createChooser(sharingIntent, getText(R.string.share_via).toString()));
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
