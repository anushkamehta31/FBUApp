package com.example.fbuapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Video")
public class Video extends ParseObject {

    public static final String KEY_VIDEO_ID = "videoId";
    public static final String KEY_GROUP = "group";
    public static final String TAG = "Video";

    // Get image
    public String getKeyVideoId() {
        return getString(KEY_VIDEO_ID);
    }

    // Set image
    public void setKeyVideoId(String videoId) {
        put(KEY_VIDEO_ID, videoId);
    }

    // Get group
    public Group getGroup() {
        return (Group) get(KEY_GROUP);
    }

    // Set the group
    public void setGroup(Group group) {
        put(KEY_GROUP, group);
    }
}
