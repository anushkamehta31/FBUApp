package com.example.fbuapp;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("NNqUGawKPZBgeFxVQR6DPrL7PGO8jRc1srjtJcC5")
                .clientKey("G71oQJPFXgN3A2fxCvaDu81oF7VjWEWWQjP0mTVk")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
