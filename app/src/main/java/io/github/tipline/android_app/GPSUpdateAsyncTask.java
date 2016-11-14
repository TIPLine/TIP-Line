package io.github.tipline.android_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import im.delight.android.location.SimpleLocation;

/**
 * wait for gps to update (used in Tip Call)
 *  and then make the call
 */
public class GPSUpdateAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String DEFAULT_COUNTRY = "United States"; // the app will use the phone number for United States if no other is available.
    private final JSONObject jsonNumbers;
    private final AtomicBoolean callAttempted; //notify the tipcall activity that a call was attempted so it knows to go back to main page
    private SimpleLocation locator;
    private String countryName;
    private Context context;
    AtomicBoolean gotGpsLock;
    private static final long TIMEOUT = 15000; //15 second timeout for gps lock, after which the default country/number will be used.

    public GPSUpdateAsyncTask(final Context context, JSONObject jsonNumbers, AtomicBoolean callAttempted) {
        this.context = context;
        this.jsonNumbers = jsonNumbers;
        this.callAttempted = callAttempted;
    }

    @Override
    public void onPreExecute() {
        locator = new SimpleLocation(context, false, false, 500);
        gotGpsLock = new AtomicBoolean(false);
        locator.setListener(new SimpleLocation.Listener() {

            @Override
            public void onPositionChanged() {
                Log.d("location update", "location update " + locator.getLatitude() + ", " + locator.getLongitude());
                final double latitude = locator.getLatitude();
                final double longitude = locator.getLongitude();

                // http://stackoverflow.com/questions/11082681/get-country-from-coordinates
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                boolean countryLookupSuccess = true;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    countryName = "unknown country";
                    countryLookupSuccess = false;
                }

                if (countryLookupSuccess && addresses.size() > 0) {
                    countryName = addresses.get(0).getCountryName();
                } else {
                    countryName = "unknown country";
                    System.out.println(addresses);
                }
                gotGpsLock.set(true);
            }
        });
        if (!locator.hasLocationEnabled()) {
            // ask the user to enable location access
            Log.d(getClass().getSimpleName(), "opening settings for GPS");
            SimpleLocation.openSettings(context);
        }
        if (locator.hasLocationEnabled()) {
            locator.beginUpdates();
            Log.d(getClass().getSimpleName(), "location updates begun");
        } else {
            Log.d(getClass().getSimpleName(), "location not enabled");
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d(this.getClass().getSimpleName(), "started GPSAsyncTask");
        long startWaitTime = System.currentTimeMillis();
        // get the gps lock
        while (!gotGpsLock.get() && System.currentTimeMillis() - startWaitTime < TIMEOUT) {

        }
        Log.d(this.getClass().getSimpleName(), "done waiting for gps lock");
        if (!gotGpsLock.get()) {
            final CharSequence text = "Couldn't attain GPS lock. Calling phone number for " + DEFAULT_COUNTRY;
            final int duration = Toast.LENGTH_LONG;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });

        }
        String phoneNum = null;
        // look for the appropriate phone number
        if (!gotGpsLock.get() || countryName.equals("unknown country")) {
            // if can't find country, use a default phone number
            try {
                phoneNum = (String) jsonNumbers.get(DEFAULT_COUNTRY);
            } catch (JSONException e) {
                e.printStackTrace(); // if can't find default phone number, error out
            }
        } else { // if found country location
            try {
                phoneNum = (String) jsonNumbers.get(countryName); // look for the appropriate phone numb for this country
            } catch (JSONException e) {
                try {
                    phoneNum = (String) jsonNumbers.get(DEFAULT_COUNTRY); //if can't find the country in the list of phone numbers, use default phone number
                } catch (JSONException e1) {
                    e.printStackTrace(); // if can't find default phone number, error out
                }
            }
        }
        // call the chosen phone number
        Log.d(getClass().getSimpleName(), "starting to call");
        call(phoneNum);
        callAttempted.set(true);
        Log.d(getClass().getSimpleName(), "set callattempted to true");
        return true;
    }

    public void call(String phoneNum) {
        Log.d(this.getClass().getSimpleName(), "calling " + phoneNum);
        phoneNum = "tel:" + phoneNum;
        Intent in = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNum));
        System.out.println("phone num: " + phoneNum);
        try {
            context.startActivity(in);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "yourActivity is not founded", Toast.LENGTH_SHORT).show();
        }
    }

}
