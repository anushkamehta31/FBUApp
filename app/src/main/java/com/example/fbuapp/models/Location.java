package com.example.fbuapp.models;

import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

@ParseClassName("Location")
public class Location extends ParseObject implements Parcelable {

    LatLng location;
    String name;
    String address;
    public static final String KEY_LOCATION = "location";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String TAG = "Location";

    public Location() {
    }

    // Need to define getters and setters for each Key that we have defined (names of Parse columns)
    public ParseGeoPoint getLocation() {
        return this.getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(LatLng location) {
        put(KEY_LOCATION, new ParseGeoPoint(location.latitude, location.longitude));
        this.location = location;
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        put(KEY_ADDRESS, address);
        this.address = address;
    }

    public String getID() {
        return getString(KEY_OBJECT_ID);
    }

    public static void saveLocation(Location location) {
        location.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                }
                Log.i(TAG, "Post save was successful!");

            }
        });
    }

}
