package io.github.tipline.android_app.async;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private final boolean inTestMode;
    private SimpleLocation locator;
    private String countryName;
    private Context context;
    private AtomicBoolean gotGpsLock;
    private static final long TIMEOUT = 15000; //15 second timeout for gps lock, after which the default country/number will be used.
    private JSONObject testCallJson;



    /*
    this is the constructor for the GPS location
    feature. This allows users to simply click a button and have the application determine their location
    and the relevant phone numbers
     */
    public GPSUpdateAsyncTask(final Context context, JSONObject jsonNumbers, AtomicBoolean callAttempted, boolean inTestMode) {
        this.context = context;
        this.jsonNumbers = jsonNumbers;
        this.callAttempted = callAttempted;
        this.inTestMode = inTestMode;


        try {
            testCallJson = jsonNumbers.getJSONObject("Test Number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonNumbers.remove("Test Number");
    }


    @Override
    /*
    this is where the GPS sets up location data before execution.
     */
    public void onPreExecute() {
        locator = new SimpleLocation(context, false, false, 500);
        gotGpsLock = new AtomicBoolean(false);
        locator.setListener(new SimpleLocation.Listener() {

            @Override
            public void onPositionChanged() {
                Log.d("location update", "location update " + locator.getLatitude() + ", " + locator.getLongitude());
                final double latitude = locator.getLatitude();
                final double longitude = locator.getLongitude();

                gotGpsLock.set(true);
            }
        });
        //if location is not enabled open it
        if (!locator.hasLocationEnabled()) {
            // ask the user to enable location access
            Log.d(getClass().getSimpleName(), "opening settings for GPS");
            SimpleLocation.openSettings(context);
        }
        // if GPS is enabled begin GPS updates
        if (locator.hasLocationEnabled()) {
            locator.beginUpdates();
            Log.d(getClass().getSimpleName(), "location updates begun");
        } else {
            Log.d(getClass().getSimpleName(), "location not enabled");
        }
    }

    @Override
    /*
    Gps works that is going on in background while
    the application is running
     */
    protected Boolean doInBackground(Void... params) {
        Log.d(this.getClass().getSimpleName(), "started GPSAsyncTask");
        final CharSequence text0 = "Obtaining GPS Lock";
        final int duration0 = Toast.LENGTH_LONG;


        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, text0, duration0);
                toast.show();
            }
        });

        long startWaitTime = System.currentTimeMillis();
        waitFor(1000); //give user time to read toast
        // get the gps lock
        while (!gotGpsLock.get() && System.currentTimeMillis() - startWaitTime < TIMEOUT) {
            waitFor(1000);
        }
        if (isCancelled()) {
            return false;
        }

        Log.d(this.getClass().getSimpleName(), "done waiting for gps lock");
        // if unable to obtain lock, execute this block
        if (!gotGpsLock.get()) {
            final CharSequence text = "Couldn't attain GPS lock";
            final int duration = Toast.LENGTH_LONG;

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            waitFor(2000); //give user time to read toast
        }
        String phoneNum = null;

        if (gotGpsLock.get()) {
            // TRY TO GET THE COUNTRY NAME if we found our long and lat
            // http://stackoverflow.com/questions/11082681/get-country-from-coordinates
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            boolean countryLookupSuccess = true;
            try {
                addresses = gcd.getFromLocation(locator.getLatitude(), locator.getLongitude(), 1);
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
        }


        // look for the appropriate phone number

        //if couldnt get long and lat or couldnt get country name
        if (!gotGpsLock.get() || countryName.equals("unknown country")) {
            // if can't find what country the user is in, ask user which country to call.
            userSelectCountryToCall();
        } else { // if found country location, look up the numbers for their location and present them with the options
            try {
                final JSONObject phoneOptionsJson = inTestMode ? testCallJson : jsonNumbers.getJSONObject(countryName); //the json object with phone numbers for this country
                createPhoneNumberChooserForCountry(phoneOptionsJson);
            } catch (JSONException e) {
                userSelectCountryToCall();//if can't find the user's location country in the list of phone numbers, ask user to select country to call
            }

        }

        return true;
    }
    /*
    if GPS cannot get a lockon, ask user to select their country
    and use this data to populate a phone number
     */
    private void userSelectCountryToCall() {

        try {
            List<CharSequence> countryOptions = new ArrayList<>();
            for (int i = 0; i < jsonNumbers.names().length(); i++) {
                countryOptions.add(jsonNumbers.names().getString(i));
                System.out.println("added country" + jsonNumbers.names().getString(i));

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Could not geolocate - Choose a country");
            builder.setItems(countryOptions.toArray(new CharSequence[countryOptions.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        JSONObject phoneOptionsJson = inTestMode ? testCallJson : jsonNumbers.getJSONObject(jsonNumbers.names().getString(which)); //the json object with phone numbers for this country


                        createPhoneNumberChooserForCountry(phoneOptionsJson);

                        // call the chosen phone number
                        Log.d(getClass().getSimpleName(), "starting to call user-selected country (geolocation has failed)");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.show();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace(); // if can't find default phone number, error out
        }
    }
    /*
    this create a menu for user to select from a list
    of phone numbers that are provided for each country
     */
    private void createPhoneNumberChooserForCountry(final JSONObject phoneOptionsJson) {

            //create and populate a list of phone numbers for the chosen country
        List<CharSequence> phoneOptions = new ArrayList<>();
        for (int i = 0; i < phoneOptionsJson.names().length(); i++) {
            try {
                phoneOptions.add(phoneOptionsJson.names().getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a phone number");

        builder.setItems(phoneOptions.toArray(new CharSequence[phoneOptions.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String phoneNum = phoneOptionsJson.getString(phoneOptionsJson.names().getString(which));
                    callAttempted.set(true);
                    call(phoneNum);
                    Log.d(getClass().getSimpleName(), "set call attempted to true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }

    private void waitFor(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*
    use selected phone number to call authorities
     */
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
