package com.example.fbuapp.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("GroupMappings")
public class GroupMappings extends ParseObject implements Parcelable {
    public static final String KEY_GROUP_ID = "groupID";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_IS_MEMBER = "isMember";

    public GroupMappings() {
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER_ID);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER_ID, user);
    }

    public Group getGroup() {
        return (Group) get(KEY_GROUP_ID);
    }

    public void setGroup(Group group) {
        put(KEY_GROUP_ID, group);
    }

    public boolean isMember() {
        return (boolean) get(KEY_IS_MEMBER);
    }

    public void setIsMember(boolean isMember) {
        put(KEY_IS_MEMBER, isMember);
    }
}
