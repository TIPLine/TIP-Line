package io.github.tipline.android_app;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;

/**
 * Extend this activity if you want your activity to have access to the phone's country location
 */
public class LocationGetterActivity extends AppCompatActivity {
    private SimpleLocation locator; //used to get gps coords
                            // https://github.com/delight-im/Android-SimpleLocation
    private String countryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryName = "unknown country";
        locator = new SimpleLocation(this, false, false, 500);
        locator.setListener(new SimpleLocation.Listener() {

            @Override
            public void onPositionChanged() {
                Log.d("location update", "location update " + locator.getLatitude() + ", " + locator.getLongitude());
                final double latitude = locator.getLatitude();
                final double longitude = locator.getLongitude();

                // http://stackoverflow.com/questions/11082681/get-country-from-coordinates
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                boolean countryLookupSuccess = true;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    countryName = "unknown country (no internet access to look up country from gps coordinates)";
                    countryLookupSuccess = false;
                }

                if (countryLookupSuccess && addresses.size() > 0) {
                    countryName = addresses.get(0).getCountryName();
                } else {
                    countryName = "unknown country";
                    System.out.println(addresses);
                }
            }
        });
        if (!locator.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }
    }
    protected String getCountry() {
        return countryName;
    }

    protected double getLongitude() {
        return locator.getLongitude();
    }

    protected double getLatitude() {
        return locator.getLatitude();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        if (locator.hasLocationEnabled()) {
            Log.d(getClass().getSimpleName(), "begining location updates");
            locator.beginUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates (saves battery)
        if (locator.hasLocationEnabled()) {
            locator.endUpdates();
        }
    }

}
