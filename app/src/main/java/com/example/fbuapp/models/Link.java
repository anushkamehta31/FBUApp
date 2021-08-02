package com.example.fbuapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Link")
public class Link extends ParseObject {

    public static final String KEY_URL = "url";
    public static final String KEY_GROUP = "group";
    public static final String KEY_DESCRIPTION = "description";
    public static final String TAG = "Link";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_USER = "user";

    // Get url
    public String getUrl() {
        return getString(KEY_URL);
    }

    // Set url
    public void setURL(String url) {
        put(KEY_URL, url);
    }

    // Get group
    public Group getGroup() {
        return (Group) get(KEY_GROUP);
    }

    // Set the group
    public void setGroup(Group group) {
        put(KEY_GROUP, group);
    }

    // Set Link Description
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    // Get link description
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getUser () {
        return getParseUser(KEY_USER);
    }

    public void setUser (ParseUser user) {
        put(KEY_USER, user);
    }

}
