package com.example.fbuapp.managers;

import android.util.Log;
import android.widget.TextView;

import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class SchoolManager {

    public static final String KEY_NAME = "name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ID = "objectId";
    public static final String TAG = "SchoolManager";

    public School querySchool(School school) throws ParseException {
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        // Specify what other data we would like to get back
        query.whereEqualTo(School.KEY_NAME, school.getName());
        School temp = query.getFirst();
        if (temp != null) return temp;
        else return school;
    }

    public void getSchoolName(String schoolID, TextView tvSchoolName) {
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        query.include(KEY_NAME);
        query.include(KEY_LOCATION);
        query.whereEqualTo(KEY_ID, schoolID);

        // Specify what other data we would like to get back
        query.getFirstInBackground(new GetCallback<School>() {
            @Override
            public void done(School school, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error fetching location");
                    return;
                }
                // TODO: connect to Google Maps if map icon is clicked
                tvSchoolName.setText(school.getName());
            }
        });
    }


}
