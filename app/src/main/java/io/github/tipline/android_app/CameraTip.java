package io.github.tipline.android_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class CameraTip extends LocationGetterActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private Button submitButton;
    private Button cancelButton;
    private ImageButton addImageAttachmentButton;
    private LinearLayout thumbnailLinearLayout;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_VIDEO = 2;
    private File currentPhoto;
    private List<File> attachments; // locations of attached images
                                    // use these locations to construct xml and add attachments

    private SimpleLocation locator; // get gps location with this
    private GMailSender sender;
    private TextView titleView;
    private String xmlForEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachments = new ArrayList<>();

        setContentView(R.layout.activity_camera_tip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton = (Button) findViewById(R.id.textSubmit);
        submitButton.setOnClickListener(this);

        cancelButton = (Button) findViewById(R.id.textCancel);
        cancelButton.setOnClickListener(this);

        addImageAttachmentButton = (ImageButton) findViewById(R.id.add_image_attachment_button);
        addImageAttachmentButton.setOnClickListener(this);

        TextView addImageAttachmentText = (TextView) findViewById(R.id.add_image_attachment_text);
        addImageAttachmentText.setOnClickListener(this);

        ImageButton addVideoAttachmentButton = (ImageButton) findViewById(R.id.add_video_attachment_button);
        addVideoAttachmentButton.setOnClickListener(this);

        TextView addVideoAttachmentText = (TextView) findViewById(R.id.add_video_attachment_text);
        addVideoAttachmentText.setOnClickListener(this);


        thumbnailLinearLayout = (LinearLayout) findViewById(R.id.thumbnail_layout);

        // Setting up email info
        sender = new GMailSender("tiplinesenderemail@gmail.com", "juniordesign");
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

        switch (v.getId()) {
            case R.id.textSubmit:
                showConfirmationDialog();
                break;
            case R.id.textCancel:
                showCancellationDialog();
                break;
            case R.id.add_image_attachment_button:
                dispatchTakePictureIntent();
                break;
            case R.id.add_image_attachment_text:
                dispatchTakePictureIntent();
                break;
            case R.id.add_video_attachment_button:
                dispatchTakeVideoIntent();
                break;
            case R.id.add_video_attachment_text:
                dispatchTakeVideoIntent();
                break;
            default:
                break;
        }
    }

    /**
     * tell the camera app to open and take picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * do something once the image has been taken with the camera app
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (REQUEST_IMAGE_CAPTURE == requestCode) {
                //save the attachment path for sending it in email later
                attachments.add(currentPhoto);

                //inflate the attachment preview layout and populate it with a thumbnail
                View attachmentPreview = getLayoutInflater().inflate(R.layout.fragment_attachment_preview, null);
                ImageView imageView = (ImageView) attachmentPreview.findViewById(R.id.imageView);

                // Get the dimensions of the View
                int targetW = 80;
                int targetH = 80;

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(currentPhoto.getAbsolutePath(), bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;


                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhoto.getAbsolutePath(), bmOptions);
                imageView.setImageBitmap(imageBitmap);
                thumbnailLinearLayout.addView(attachmentPreview);
            } else if (REQUEST_TAKE_VIDEO == requestCode) {
                Uri videoUri = data.getData();
                attachments.add(new File(videoUri.getPath()));
                Log.d(getClass().getSimpleName(), "added video attachment");
            }
        }
    }


    private void showConfirmationDialog() {

        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(CameraTip.this);

        helpBuilder.setTitle("Confirm Camera Tip?");
        helpBuilder.setMessage("Use this message? The message will be " +
                "sent to law enforcement officials to investigate this suspicion of human trafficking.");
        helpBuilder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        XMLGenerator xmlGenerator = new XMLGenerator();
                        EditText titleEditText = (EditText) findViewById(R.id.subjectEditText);
                        EditText bodyEditText = (EditText) findViewById(R.id.infoEditText);
                        String country = getCountry();
                        double locationLongitude = getLongitude();
                        double locationLatitude = getLatitude();
                        try {
                            xmlForEmail = xmlGenerator.createXML("camera", "username",
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

                    }
                });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void showCancellationDialog() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Cancel Camera Tip?");
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
                        startActivity(new Intent(CameraTip.this, MainPage.class));
                    }
                });

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
                        startActivity(new Intent(CameraTip.this, MainPage.class));
                    }
                });
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhoto = image;
        return image;
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
        }
    }

    private void sendEmail() {
        for (int i = 0; i < attachments.size(); i++) {
            try {
                sender.addAttachment(attachments.get(i).getPath());
            } catch (Exception e) {

            }
        }
        try {
            // Add subject, Body, your mail Id, and receiver mail Id.
            sender.sendMail(titleView.getText().toString(), xmlForEmail, "tiplinesenderemail@gmail.com", "tiplinetestemail@gmail.com");
        }
        catch (Exception ex) {
        }
    }
}
