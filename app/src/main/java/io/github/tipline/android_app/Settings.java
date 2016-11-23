package io.github.tipline.android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.util.Log;

public class Settings extends AppCompatActivity {

    final Boolean[] testState = new Boolean[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Switch switchButton;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // For first switch button
        switchButton = (Switch) findViewById(R.id.switch1);

        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    testState[0] = true;
                    Log.d("Hey", "hey");
                } else {
                    testState[0] = false;
                    Log.d("Off", "Off");
                }
            }
        });
    }

    public Boolean getTestState() {
        return testState[0];
    }
}
