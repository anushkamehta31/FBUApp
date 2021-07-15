package com.example.fbuapp.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject implements Parcelable {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TOPICS = "topics";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_RESOURCES = "resources";
    public static final String KEY_MEETING_DAYS = "meetingDays";
    public static final String KEY_MEETING_TIMES = "meetingTimes";
    public static final String KEY_VIRTUAL = "virtual";
    public static final String KEY_SCHOOL = "school";
    public static final String KEY_MEETING_ID = "meetingID";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GROUP_ID = "groupID";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_IS_MEMBER = "isMember";
    public static final String TAG = "Group";

    public Group() {
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String name) {
        put(KEY_DESCRIPTION, name);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ArrayList<String> getTopics() {
        return (ArrayList<String>) get(KEY_TOPICS);
    }

    public void setTopics(ArrayList<String> topics) {
        put(KEY_TOPICS, topics);
    }

    public Location getLocation() {
        return (Location) get(KEY_LOCATION);
    }

    public void setLocation(Location location) {
        put(KEY_LOCATION, location);
    }

    public Resources getResources() {
        return (Resources) get(KEY_RESOURCES);
    }

    public void setResources(Resources resources) {
        put(KEY_RESOURCES, resources);
    }

    public ArrayList<String> getMeetingDays() {
        return (ArrayList<String>) get(KEY_MEETING_DAYS);
    }

    public void setMeetingDays(ArrayList<String> meetingDays) {
        put(KEY_MEETING_DAYS, meetingDays);
    }

    public ArrayList<String> getMeetingTimes() {
        return (ArrayList<String>) get(KEY_MEETING_TIMES);
    }

    public void setMeetingTimes(ArrayList<String> meetingTimes) {
        put(KEY_MEETING_DAYS, meetingTimes);
    }

    public boolean isVirtual() {
        return (boolean) get(KEY_VIRTUAL);
    }

    public void setIsVirtual(boolean virtual) {
        put(KEY_VIRTUAL, virtual);
    }

    public School getSchool() {
        return (School) get(KEY_SCHOOL);
    }

    public void setSchool(School school) {
        put(KEY_SCHOOL, school);
    }

    public String getMeetingID() {
        return getString(KEY_MEETING_ID);
    }

    public void setMeetingID(String meetingID) {
        put(KEY_MEETING_ID, meetingID);
    }

    public String getPassword() {
        return getString(KEY_PASSWORD);
    }

    public void setPassword(String password) {
        put(KEY_PASSWORD, password);
    }

    // TODO: Fix this method it's not returning a list of users.
    public List<ParseUser> getUsers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMappings");
        List<ParseUser> groupUsers = new ArrayList<>();
        query.include(KEY_GROUP_ID);
        query.include(KEY_USER_ID);
        query.whereEqualTo(KEY_GROUP_ID, this);
        query.whereEqualTo(KEY_IS_MEMBER, true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (ParseObject mappedUser : users) {
                    ParseUser user = (ParseUser) mappedUser.get(KEY_USER_ID);
                    Log.i(TAG, "Username: " + user.getUsername());
                    groupUsers.add(user);
                }
            }
        });

        Log.i(TAG, "Size of list: "+ groupUsers.size());
        return groupUsers;
    }
}
