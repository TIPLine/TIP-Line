package io.github.tipline.android_app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.tipline.android_app.async.GPSUpdateAsyncTask;

public class TipCall extends LocationGetterActivity  {
    private JSONObject jsonNumbers;
    private ProgressBar progressBar;
    private final int PHONE_PERMISSION_CODE = 876;
    private AtomicBoolean callAttempted;
    private GPSUpdateAsyncTask gpsCallAsyncTask;
    private boolean inTestMode;
    private String PREFS_NAME = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        inTestMode = settings.getBoolean("testMode", false);
        Log.d("test mode status", Boolean.toString(inTestMode));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_call);
        callAttempted = new AtomicBoolean(false);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        jsonNumbers = getPhoneNumbers();


        int permissionCheck = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) { //see if we need to ask for phone usage permission
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
        } else { //permission was already granted
            Log.d(getClass().getSimpleName(), "permission was already granted");
            gpsCallAsyncTask = new GPSUpdateAsyncTask(this, jsonNumbers, callAttempted, inTestMode);
            gpsCallAsyncTask.execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "resumed");
        if (callAttempted.get()) {
            startActivity(new Intent(TipCall.this, MainPage.class));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (gpsCallAsyncTask != null) {
            gpsCallAsyncTask.cancel(true);
        }
    }

    @Override
    public void onPause() {
        super.onStop();
        if (gpsCallAsyncTask != null) {
            gpsCallAsyncTask.cancel(true);
        }
    }

    private JSONObject getPhoneNumbers() {
        //see if a file of nums has been downloaded from internet yet (http://tipnumbers.airlineamb.org/mynumbers.txt)
        BufferedReader input = null;
        File file = null;
        StringBuffer buffer = null;
        boolean foundDownloadedFile = false;
        try {
            file = new File(getFilesDir(), this.getString(R.string.phone_num_file));
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            buffer = new StringBuffer();
            String line;
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }
            foundDownloadedFile = true;
            System.out.println("read " + buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fallback to the numbers included in the app install if there are none that have been downloaded from the internet in the past
        String jsonStr = null;
        try {
            if (foundDownloadedFile) { //use the numbesr downloaded from internet
                jsonStr = buffer.toString();
            } else { //fallback to assets numbers file packaged with the app download
                InputStream inputStream = getAssets().open("mynumbers.txt");
                int size = inputStream.available();
                byte[] byteBuffer = new byte[size];
                inputStream.read(byteBuffer);
                inputStream.close();
                jsonStr = new String(byteBuffer, "UTF-8");
            }

        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        try {
            return new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * after getting permission to use phone, start the asynctask which will get a gps lock and then will make the call
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PHONE_PERMISSION_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    gpsCallAsyncTask = new GPSUpdateAsyncTask(this, jsonNumbers, callAttempted, inTestMode);
                    gpsCallAsyncTask.execute();

                } else {
                    Log.d("TAG", "Call Permission Not Granted -- returning to Main page");
                    startActivity(new Intent(TipCall.this, MainPage.class));
                }
                break;

            default:
                break;
        }
    }
}
