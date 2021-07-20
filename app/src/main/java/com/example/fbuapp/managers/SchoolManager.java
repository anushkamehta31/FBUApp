package com.example.fbuapp.managers;

import com.example.fbuapp.models.School;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class SchoolManager {

    public School querySchool(School school) throws ParseException {
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        // Specify what other data we would like to get back
        query.whereEqualTo(School.KEY_NAME, school.getName());
        School temp = query.getFirst();
        if (temp != null) return temp;
        else return school;
    }
}
