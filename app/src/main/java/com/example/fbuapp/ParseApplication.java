package com.example.fbuapp;

import android.app.Application;

import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.Image;
import com.example.fbuapp.models.Link;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.Note;
import com.example.fbuapp.models.Resources;
import com.example.fbuapp.models.School;
import com.example.fbuapp.models.Video;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(School.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Resources.class);
        ParseObject.registerSubclass(GroupMappings.class);
        ParseObject.registerSubclass(Video.class);
        ParseObject.registerSubclass(Image.class);
        ParseObject.registerSubclass(Link.class);
        ParseObject.registerSubclass(Note.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("NNqUGawKPZBgeFxVQR6DPrL7PGO8jRc1srjtJcC5")
                .clientKey("G71oQJPFXgN3A2fxCvaDu81oF7VjWEWWQjP0mTVk")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
