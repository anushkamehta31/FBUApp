package com.example.fbuapp.managers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.SwipeAdapter;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void querySchools(Map<String, School> potentialSchools, Context context, Dialog dialog) {
        // Create a dropdown for schools
        ArrayList<String> schools = new ArrayList<>();
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        List<ParseUser> groupUsers = new ArrayList<>();
        query.include("name");
        query.findInBackground(new FindCallback<School>() {
            @Override
            public void done(List<School> allSchools, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for (School school : allSchools) {
                    School university = (School) school;
                    String schoolName = university.getName();
                    schools.add(schoolName);
                    potentialSchools.put(schoolName, university);
                }
                // Create dropdown for schools users textview
                ArrayAdapter<String> adapterSchool =
                        new ArrayAdapter<String>(context, R.layout.drop_down_item, schools);
                AutoCompleteTextView schoolAutoComplete = dialog.findViewById(R.id.autoCompleteSchool);
                schoolAutoComplete.setAdapter(adapterSchool);
            }
        });
    }


}
