package com.example.fbuapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Note")
public class Note extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    public static final String KEY_SUBTITLE = "subtitle";
    public static final String KEY_GROUP = "group";
    public static final String KEY_USER = "user";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getSubtitle() {
        return getString(KEY_SUBTITLE);
    }

    public void setSubtitle(String subtitle) {
        put(KEY_SUBTITLE, subtitle);
    }

    public Group getGroup() {
        return (Group) get(KEY_GROUP);
    }

    public void setGroup(Group group) {
        put(KEY_GROUP, group);
    }

    public ParseUser getUser () {
        return getParseUser(KEY_USER);
    }

    public void setUser (ParseUser user) {
        put(KEY_USER, user);
    }

}
