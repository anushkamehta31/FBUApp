package com.example.fbuapp.managers;

import android.util.Log;

import com.example.fbuapp.adapters.GridAdapter;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class ResourceManager {

    public static final String TAG = "ResourceManager";


    public void queryImages(Group group, List<Image> groupImages, GridAdapter gridAdapter) {
        // Specify which class to query
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        // Specify what other data we would like to get back
        query.include(Image.KEY_GROUP);
        // Get current users posts only
        query.whereEqualTo(Image.KEY_GROUP, group);
        // Get most recent posts at top
        query.addDescendingOrder(Image.KEY_CREATED_AT);
        // Get all the posts
        query.findInBackground(new FindCallback<Image>() {
            @Override
            public void done(List<Image> images, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                groupImages.addAll(images);
                gridAdapter.notifyDataSetChanged();
            }
        });
    }
}
