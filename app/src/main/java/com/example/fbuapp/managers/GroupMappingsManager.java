package com.example.fbuapp.managers;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.fbuapp.adapters.GroupMemberAdapter;
import com.example.fbuapp.adapters.SwipeAdapter;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.School;
import com.hootsuite.nachos.NachoTextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.fbuapp.CreateAccountActivity.KEY_SCHOOL;
import static com.example.fbuapp.managers.GroupManager.KEY_LOCATION;

public class GroupMappingsManager {
    public static final String KEY_GROUP = "groupID";
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_MEMBER = "isMember";
    public static final String TAG = "GroupMappingsManager";

    public void setUserMappings(Map<String, ParseUser> map, NachoTextView nUsers, Group group) throws ParseException {
        for (com.hootsuite.nachos.chip.Chip chip : nUsers.getAllChips()) {
            ParseUser user = map.get(chip.getText().toString());
            GroupMappings mappings = new GroupMappings();
            mappings.setGroup(group);
            mappings.setIsMember(false);
            mappings.setUser(user);
            mappings.save();
        }
        GroupMappings mappings = new GroupMappings();
        mappings.setGroup(group);
        mappings.setIsMember(true);
        mappings.setUser(ParseUser.getCurrentUser());
        mappings.save();
    }

    public void setUserMapping(ParseUser user, Group group) {
        GroupMappings groupMapping = new GroupMappings();
        groupMapping.setUser(user);
        groupMapping.setGroup(group);
        groupMapping.setIsMember(true);
        try {
            groupMapping.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getGroupMembers(GroupMemberAdapter groupMemberAdapter, List<ParseUser> groupMembers, Group group) {
        ParseQuery<GroupMappings> query = ParseQuery.getQuery(GroupMappings.class);
        // Specify what other data we would like to get back
        query.include("objectId");
        query.whereEqualTo(KEY_GROUP, group);
        query.whereEqualTo(KEY_MEMBER, true);
        // query.whereEqualTo(KEY_MEMBER, true);
        // Specify what other data we would like to get back
        query.findInBackground(new FindCallback<GroupMappings>() {
            @Override
            public void done(List<GroupMappings> mappings, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error while fetching group members");
                    return;
                }

                for (GroupMappings memberMapped : mappings) {
                   ParseQuery userQuery = ParseQuery.getQuery(ParseUser.class);
                   userQuery.include("objectId");
                   userQuery.whereEqualTo("objectId", memberMapped.getUser().getObjectId());
                   userQuery.findInBackground(new FindCallback<ParseUser>() {
                       @Override
                       public void done(List<ParseUser> users, ParseException e) {
                           if (e != null) {
                               Log.i(TAG, "Error getting usernames");
                               return;
                           }
                           ParseUser user = users.get(0);
                           groupMembers.add(user);
                           groupMemberAdapter.notifyDataSetChanged();
                       }
                   });
                }
            }
        });
    }

    public void findPotentialMatches(List<Group> potentialGroups, SwipeAdapter adapter) {
        ParseQuery<Group> queryGroup = new ParseQuery<Group>(Group.class);
        queryGroup.include(KEY_LOCATION);
        queryGroup.include(KEY_SCHOOL);
        queryGroup.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error finding groups");
                    return;
                }
                potentialGroups.addAll(groups);
                // Remove the groups that the user is part of
                // Specify which class to query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMappings");
                query.include(Group.KEY_GROUP_ID);
                // Get current users groups only
                query.whereEqualTo(Group.KEY_USER_ID, ParseUser.getCurrentUser());
                // Make sure user is a member of a group (not a pending invitation)
                query.whereEqualTo(Group.KEY_IS_MEMBER, true);
                // Get all the groups and add to the array
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> userMappings, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting groups", e);
                            return;
                        }
                        for (ParseObject mapping : userMappings) {
                            Group group = (Group) mapping.getParseObject(Group.KEY_GROUP_ID);
                            for (int i=0; i < potentialGroups.size(); i++) {
                                if (potentialGroups.get(i).getObjectId().equals(group.getObjectId())) {
                                    potentialGroups.remove(i);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void editUserGroupMapping(Group group, boolean isMember) {
        ParseQuery<GroupMappings> query = ParseQuery.getQuery(GroupMappings.class);
        // Specify what other data we would like to get back
        query.include("objectId");
        query.whereEqualTo(KEY_GROUP, group);
        query.whereEqualTo(KEY_USER_ID, ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<GroupMappings>() {
            @Override
            public void done(GroupMappings mapping, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting group mapping", e);
                    return;
                }
                mapping.setIsMember(isMember);
                mapping.saveInBackground();
            }
        });
    }

    public void deleteMapping(Group group, ParseUser currentUser) {
        ParseQuery<GroupMappings> query = ParseQuery.getQuery(GroupMappings.class);
        // Specify what other data we would like to get back
        query.include("objectId");
        query.whereEqualTo(KEY_GROUP, group);
        query.whereEqualTo(KEY_USER_ID, currentUser);
        query.getFirstInBackground(new GetCallback<GroupMappings>() {
            @Override
            public void done(GroupMappings mapping, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting group mapping", e);
                    return;
                }
                mapping.deleteInBackground();
            }
        });
    }

    public void setInviteMapping(ParseUser user, Group group) {
        GroupMappings groupMapping = new GroupMappings();
        groupMapping.setUser(user);
        groupMapping.setGroup(group);
        groupMapping.setIsMember(false);
        try {
            groupMapping.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
