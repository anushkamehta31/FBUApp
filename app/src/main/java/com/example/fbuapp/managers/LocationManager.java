package com.example.fbuapp.managers;

import android.util.Log;
import android.widget.TextView;

import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class LocationManager {

    public static final String TAG = "LocationManager";
    public static final String KEY_NAME = "name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ID = "objectId";
    public static final String KEY_ADDRESS = "address";

    public void getShoolFromGroup (TextView tvLocation, String locationID) {
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo(KEY_ID, locationID);
        query.include(KEY_NAME);
        query.include(KEY_LOCATION);
        query.include(KEY_ADDRESS);
        // Specify what other data we would like to get back
        query.getFirstInBackground(new GetCallback<Location>() {
            @Override
            public void done(Location location, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error fetching location");
                    return;
                }
                // TODO: connect to Google Maps if map icon is clicked
                tvLocation.setText(location.getName());
            }
        });
    }
}
