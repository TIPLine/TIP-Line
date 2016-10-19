package io.github.tipline.android_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.os.Environment;
import java.io.IOException;
import java.io.File;

import java.util.List;

public class AudioTip extends AppCompatActivity implements View.OnClickListener, MediaStore.Audio.AudioColumns {

    private Button submitButton;
    private Button cancelButton;
    private Button record, play, stop;
    public static int RQS_RECORDING = 1;
    private MediaRecorder audioRecorder = null;
//    private String outputFile = null;
    private Uri saved;
    boolean startRecording = false;


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
                try {
                    beginRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case R.id.stop_audio:
                try {
                    stopRec();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
    private void beginRecording() throws IOException{
        boolean externalStorageAvailible = false;
        boolean externalStorageWritable = false;
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageAvailible = true;
            externalStorageWritable =true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageAvailible = true;
            externalStorageWritable = false;
        } else {
            externalStorageAvailible = false;
            externalStorageWritable = false;
        }
        File sdCar = Environment.getExternalStorageDirectory();
        if(externalStorageAvailible && !sdCar.exists()) {
            sdCar.mkdir();
        }
        File newFile = new File(sdCar.getPath() + "/" + System.currentTimeMillis() + ".mp3");
        if(audioRecorder == null) {
            audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            audioRecorder.setOutputFile(newFile.getPath());
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        if(!startRecording) {
            try {
                audioRecorder.prepare();
                audioRecorder.start();
                startRecording = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stop.setEnabled(true);
        record.setEnabled(false);
    }
    private void stopRec() throws IOException {
        if(startRecording) {
            startRecording = false;
            audioRecorder.stop();
            audioRecorder.reset();
            audioRecorder.release();
            audioRecorder = null;
            record.setEnabled(true);
            play.setEnabled(true);
        }
    }
}
