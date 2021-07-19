package com.example.fbuapp.models;

import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

@ParseClassName("School")
public class School extends ParseObject implements Parcelable {

    LatLng location;
    String name;
    public static final String KEY_LOCATION = "location";
    public static final String KEY_NAME = "name";
    public static final String TAG = "School";

    public School() {

    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        put(KEY_LOCATION, new ParseGeoPoint(location.latitude, location.longitude));
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        put(KEY_NAME, name);
        this.name = name;
    }

    public static void saveSchool(School school) {
        school.saveInBackground(new SaveCallback() {
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
