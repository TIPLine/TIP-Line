package io.github.tipline.android_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.io.File;
import java.util.Locale;

import io.github.tipline.android_app.util.GMailSender;
import io.github.tipline.android_app.util.XMLGenerator;


public class TextTip extends LocationGetterActivity implements View.OnClickListener {

    Button submitButton;
    Button cancelButton;
    EditText editSubject;
    EditText editMessage;

    String name;
    String phoneNumber;
    String title;
    String body;
    File file;
    String type = "text";
    String xml;

    GMailSender sender;

    XMLGenerator xmlGenerator = new XMLGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_tip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton = (Button) findViewById(R.id.textSubmit);
        submitButton.setOnClickListener(this);

        cancelButton = (Button) findViewById(R.id.textCancel);
        cancelButton.setOnClickListener(this);

        editSubject = (EditText) findViewById(R.id.editSubject);
        editMessage = (EditText) findViewById(R.id.editMessage);

        name = "Bob Smith";
        phoneNumber = "555-1234";

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
            //Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.textSubmit:

                //Creating the XML File
                try {
                    //This needs to be kept here
                    title = editSubject.getText().toString();
                    body = editMessage.getText().toString();
                    String locationCountry = getCountry();
                    double locationLongitude = getLongitude();
                    double locationLatitude = getLatitude();
                    xml = xmlGenerator.createXML(type, name, getCurrentTime(),
                            locationCountry, locationLongitude, locationLatitude, phoneNumber, title, body);
                    Log.v("XML FILE", xml);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                showConfirmationDialog();
                break;
            case R.id.textCancel:
                showCancellationDialog();

            default:
                break;
        }
    }

    /*
    confirm tip submission
     */
    private void showConfirmationDialog() {

        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(TextTip.this);
        helpBuilder.setTitle("Confirm Text Tip?");
        helpBuilder.setMessage("Use this message? The message will be " +
                "sent to law enforcement officials to investigate this suspicion of human trafficking.");
        helpBuilder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendEmail();
                        showTipSentDialog();
                    }
                });
        helpBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }
    /* confirm tip cancellation

     */
    private void showCancellationDialog() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Cancel Text Tip?");
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
                        startActivity(new Intent(TextTip.this, MainPage.class));
                    }
                });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }
    /*
    feedback that tip was sent
     */
    private void showTipSentDialog() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Tip Sent");
        helpBuilder.setMessage("Your tip was successfully sent to the authorities. " +
                "Thank you for reporting this incident.");
        helpBuilder.setPositiveButton("Home",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(TextTip.this, MainPage.class));
                    }
                });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }
    /*
   send information to database
     */
    private void sendEmail() {
        try {
            // Add subject, Body, your mail Id, and receiver mail Id.
            sender.sendMail(title, xml, "tiplinesenderemail@gmail.com", "tip@airlineamb.org");
        }
        catch (Exception ex) {
        }
    }

    private String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
}
