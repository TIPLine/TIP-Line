package io.github.tipline.android_app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.Context;
import java.util.List;

import java.io.IOException;

public class AudioTip extends AppCompatActivity implements View.OnClickListener {

    private Button submitButton;
    private Button cancelButton;
    private Button record, play, stop;
    public static int RQS_RECORDING = 1;
    private MediaRecorder audioRecorder = null;
    private String outputFile = null;
    private Uri saved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_tip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize buttons
        submitButton = (Button) findViewById(R.id.audioSubmit);
        cancelButton = (Button) findViewById(R.id.audioCancel);
        record = (Button) findViewById(R.id.record);
        play = (Button) findViewById(R.id.play_audio);
        stop = (Button) findViewById(R.id.stop_audio);

        //set play and stop to disabled
        stop.setEnabled(false);
        play.setEnabled(false);
        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gpp";

        //set up Media Recorder

        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);

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

            case R.id.record:
                beginRecording();

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
    
    private void beginRecording() {
//        audioRecorder = new MediaRecorder();
//        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        audioRecorder.setOutputFile(outputFile);
//        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//        try {
//            audioRecorder.prepare();
//            audioRecorder.start();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        record.setEnabled(false);
//        stop.setEnabled(true);
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if(recorderIsAvailible(getApplicationContext(), intent)) {
            startActivityForResult(intent, RQS_RECORDING);
        }
    }
    private boolean recorderIsAvailible(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();

        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

//    @Override
//    protected void onActivityResult(int resCode, int resultCode, Intent data) {
//        if (resCode == RECORD_REQUEST) {
//            saved = data.getData();
//        }
//    }
}
