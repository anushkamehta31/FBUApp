package com.example.fbuapp.managers;

import android.util.Log;
import android.widget.TextView;

import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LocationManager {

    public static final String TAG = "LocationManager";
    public static final String KEY_NAME = "name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ID = "objectId";
    public static final String KEY_ADDRESS = "address";

    public void getSchoolFromGroup(TextView tvLocation, String locationID) {
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

    public ParseGeoPoint getUserLocation() {
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo(KEY_ID, ((Location) ParseUser.getCurrentUser().get(KEY_LOCATION)).getObjectId());
        query.include(KEY_NAME);
        query.include(KEY_LOCATION);
        query.include(KEY_ADDRESS);
        try {
            Location location = query.getFirst();
            return location.getLocation();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ParseGeoPoint getGroupLocation(Group group) {
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo(KEY_ID, ((Location) group.get(KEY_LOCATION)).getObjectId());
        query.include(KEY_NAME);
        query.include(KEY_LOCATION);
        query.include(KEY_ADDRESS);
        try {
            Location location = query.getFirst();
            return location.getLocation();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
