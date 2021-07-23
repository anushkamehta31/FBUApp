package com.example.fbuapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Image")
public class Image extends ParseObject {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_GROUP = "group";
    public static final String TAG = "Image";
    public static final String KEY_CREATED_AT = "createdAt";


    // Get image
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    // Set image
    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    // Get group
    public ParseUser getUser() {
        return getParseUser(KEY_GROUP);
    }

    // Set the group
    public void setUser(ParseUser user) {
        put(KEY_GROUP, user);
    }
}
