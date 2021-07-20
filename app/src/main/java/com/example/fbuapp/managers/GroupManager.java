package com.example.fbuapp.managers;

import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class GroupManager {

    public static final String KEY_GROUP = "groupID";
    public static final String KEY_USER = "userID";
    public static final String KEY_MEMBER = "isMember";
    public static final String KEY_LOCATION = "location";

    public GroupManager() {
    }

    public static int getMemberCount(Group group) {
        ParseQuery<GroupMappings> query = ParseQuery.getQuery(GroupMappings.class);
        // Specify what other data we would like to get back
        query.include("objectID");
        query.whereEqualTo(KEY_GROUP, group);
        query.whereEqualTo(KEY_MEMBER, true);
        int members = 0;
        try {
            List<GroupMappings> mappings = query.find();
            members += mappings.size();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return members;
    }

    public static double getDistanceToGroup(Group group) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint userLocation = ((Location) user.get(KEY_LOCATION)).getLocation();
        ParseGeoPoint groupLocation = group.getLocation().getLocation();
        return userLocation.distanceInMilesTo(groupLocation);
    }
}
