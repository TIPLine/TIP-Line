package io.github.tipline.android_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TipCall extends LocationGetterActivity  {
    private String locationCountry = "United States";
    private JSONObject jsonNumbers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tip_call);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        jsonNumbers = getPhoneNumbers();
        try {
            String phoneNum = (String) jsonNumbers.get(locationCountry); // get the appropriate phone number for country we are in or default to United States.
            call(phoneNum);
        } catch (JSONException e) {
            try {
                call((String) jsonNumbers.get("United States")); // if there is no phone number for this country, default to United States
            } catch (JSONException e1) {
                e1.printStackTrace(); //no number for united states???
            }
        }
    }

    public JSONObject getPhoneNumbers() {
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

    public void call(String phoneNum) {
        int permissionCheck = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CALL_PHONE}, 123);
        } else {
            phoneNum = "tel:" + phoneNum;
            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNum));
            System.out.println("phone num: " + phoneNum);
            //Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:6789100416"));
            try {
                startActivity(in);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "yourActivity is not founded", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        String phoneNum = (String) jsonNumbers.get(locationCountry); // get the appropriate phone number for country we are in or default to United States.
                        call(phoneNum);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
