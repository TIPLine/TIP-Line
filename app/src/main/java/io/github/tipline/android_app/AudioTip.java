package io.github.tipline.android_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AudioTip extends AppCompatActivity implements View.OnClickListener {

    private Button submitButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_tip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton = (Button) findViewById(R.id.audioSubmit);
        cancelButton = (Button) findViewById(R.id.audioCancel);

        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //Controls back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.audioSubmit:
                showConfirmationDialog();
                break;
            case R.id.audioCancel:
                showCancellationDialog();

            default:
                break;
        }

    }

    private void showConfirmationDialog() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Confirm Audio Tip?");
        helpBuilder.setMessage("Use this message? The message will be " +
                "sent to law enforcement officials to investigate this suspicion of human trafficking.");
        helpBuilder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showTipSentDialog();
                    }
                });
        helpBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //Nothing but close dialog box
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void showCancellationDialog() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Cancel Audio Tip?");
        helpBuilder.setMessage("Are you sure you want to cancel this message? " +
                "Your message and any attachments will be lost and will not be sent to the authorities");
        helpBuilder.setPositiveButton("Return to Message",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });
        helpBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AudioTip.this, MainPage.class));
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void showTipSentDialog() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Tip Sent");
        helpBuilder.setMessage("Your tip was successfully sent to the authorities. " +
                "Thank you for reporting this incident.");
        helpBuilder.setPositiveButton("Home",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AudioTip.this, MainPage.class));
                    }
                });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }
}
