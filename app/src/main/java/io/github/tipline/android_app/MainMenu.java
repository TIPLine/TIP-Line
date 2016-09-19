package io.github.tipline.android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    private Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        test = (Button) findViewById(R.id.button);
        test.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){

        test.setText("We changed the word");
        //Intent myIntent = new Intent(this, Page3.class);
        //this.startActivity(myIntent);
    }

}
