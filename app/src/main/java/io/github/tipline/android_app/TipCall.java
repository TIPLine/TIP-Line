package io.github.tipline.android_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

public class TipCall extends LocationGetterActivity  {
    private static String locationCountry = "United States";
    private JSONObject jsonNumbers;
    private ProgressBar progressBar;
    private final int PHONE_PERMISSION_CODE = 876;
    private AtomicBoolean callAttempted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_call);
        callAttempted = new AtomicBoolean(false);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        jsonNumbers = getPhoneNumbers();

        int permissionCheck = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) { //see if we need to ask for phone usage permission
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
        } else { //permission was already granted
            Log.d(getClass().getSimpleName(), "permission was already granted");
            new GPSUpdateAsyncTask(this, jsonNumbers, callAttempted).execute();
        }


        final CharSequence text = "Obtaining GPS Lock";
        final int duration = Toast.LENGTH_LONG;


        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "resumed");
        if (callAttempted.get()) {
            startActivity(new Intent(TipCall.this, MainPage.class));
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
//TODO Permission check -> start asynctask -> gps lock in asynctask -> call from asynctask



    /**
     * after getting permission to use phone, start the asynctask which will get a gps lock and then will make the call
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PHONE_PERMISSION_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    new GPSUpdateAsyncTask(this, jsonNumbers, callAttempted).execute();

                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = TipCall.this.getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    TipCall.this.getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }
}
