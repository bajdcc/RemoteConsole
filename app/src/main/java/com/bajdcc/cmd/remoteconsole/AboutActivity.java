package com.bajdcc.cmd.remoteconsole;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Fragment fragment =
                new AboutFragmentBuilder("bajdcc software")
                        .build();


        // Fragment Transaction
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.about_content, fragment)
                .commit();
    }
}
