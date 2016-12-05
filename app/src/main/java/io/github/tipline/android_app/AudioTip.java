package io.github.tipline.android_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Environment;
import java.io.IOException;
import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.tipline.android_app.util.GMailSender;
import io.github.tipline.android_app.util.XMLGenerator;

public class AudioTip extends LocationGetterActivity implements View.OnClickListener, MediaStore.Audio.AudioColumns {

    private Button submitButton;
    private Button cancelButton;
    private Button record, play, stop;
    public static int RQS_RECORDING = 1;
    private MediaRecorder audioRecorder = null;
    private MediaPlayer mediaPlayer = null;
    //private String outputFile = null;
    private Uri saved;
    private boolean startRecording = false;
    private File newFile;

    private GMailSender sender;
    private String xmlForEmail;
    private EditText titleEditText;
    private List<File> attachments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_tip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attachments = new ArrayList<>();

        //initialize buttons
        submitButton = (Button) findViewById(R.id.audioSubmit);
        cancelButton = (Button) findViewById(R.id.audioCancel);
        record = (Button) findViewById(R.id.record);
        play = (Button) findViewById(R.id.playAudio);
        stop = (Button) findViewById(R.id.stopAudio);

        //set play and stop to disabled
        stop.setEnabled(false);
        play.setEnabled(false);
        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gpp";

        //set up Media Recorder
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancellationDialog();
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    beginRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopRec();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(newFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            }
        });

        // Setting up email info
        sender = new GMailSender("tiplinesenderemail@gmail.com", "juniordesign", this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
    }
    /*
    confirmation popup before tip is sent to database
     */
    private void showConfirmationDialog() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Confirm Audio Tip?");
        helpBuilder.setMessage("Use this message? The message will be " +
                "sent to law enforcement officials to investigate this suspicion of human trafficking.");
        helpBuilder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        XMLGenerator xmlGenerator = new XMLGenerator();
                        titleEditText = (EditText) findViewById(R.id.audioSubject);
                        EditText bodyEditText = (EditText) findViewById(R.id.audioBody);
                        String country = getCountry();
                        double locationLongitude = getLongitude();
                        double locationLatitude = getLatitude();
                        try {

                            xmlForEmail = xmlGenerator.createXML("audio", "username", getCurrentTime(),
                                    country, locationLongitude, locationLatitude, "placeholder phone number",
                                    titleEditText.getText().toString(), bodyEditText.getText().toString(),
                                    attachments);
                            Log.v("XML FILE", xmlForEmail);
                        } catch (IOException e) {
                            Log.e(CameraTip.class.getSimpleName(), "Issue creating XML");
                        }
                        sendEmail();
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
    /*
    cancellation confirmation popup before tip is deleted
     */
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
    /*
    tip sent feedback and confirmation
     */
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
    /*
    this creates a custom recorder in application
    to capture audio tips
     */
    private void beginRecording() throws IOException{
        boolean externalStorageAvailible = false;
        boolean externalStorageWritable = false;
        String state = Environment.getExternalStorageState();
        //check external storage and set booleans to declare type
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
        //if you have the ability to store data, begin recording
        if(externalStorageAvailible && !sdCar.exists()) {
            sdCar.mkdir();
        }
        newFile = new File(sdCar.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3");
        if(audioRecorder == null) {
            audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            audioRecorder.setOutputFile(newFile.getPath());
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            attachments.add(newFile);
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
    /*
    method to stop recording and saving data to the device
     */
    private void stopRec() throws IOException {
        try {
            if (startRecording) {
                startRecording = false;
                audioRecorder.stop();
                audioRecorder.reset();
                audioRecorder.release();
                audioRecorder = null;
                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
            }
        } catch(RuntimeException stopException) {
            stopException.printStackTrace();
        }
    }
    /*
    method to time stamp recording
     */
    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
    /* send the recording to the customer's central database via
    email
     */
    private void sendEmail() {
        try {
            sender.addAttachment(newFile.getPath());
        } catch (Exception e) {
        }
        try {
            // Add subject, Body, your mail Id, and receiver mail Id.
            sender.sendMail(titleEditText.getText().toString(), xmlForEmail, "tiplinesenderemail@gmail.com", "tip@airlineamb.org");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
