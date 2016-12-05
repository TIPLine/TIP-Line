package io.github.tipline.android_app;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    final Boolean[] testState = new Boolean[1];
    private SharedPreferences settings;
    private String PREFS_NAME = "preferences";
    @Override
    /*
    creates settings page and test code toggle
     */
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        Switch switchButton;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // For first switch button
        switchButton = (Switch) findViewById(R.id.switch1);
        System.out.println("test mode status: " + settings.getBoolean("testMode", false));
        switchButton.setChecked(settings.getBoolean("testMode", false));

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    editor.putBoolean("testMode", true);
                    editor.commit();
                } else {
                    editor.putBoolean("testMode", false);
                    editor.commit();
                }
            }
        });
    }

    public Boolean getTestState() {
        return testState[0];
    }
}
