package com.example.fbuapp.managers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GroupMemberAdapter;
import com.example.fbuapp.adapters.GroupsAdapter;
import com.example.fbuapp.adapters.PendingInvitesAdapter;
import com.example.fbuapp.fragments.groupFragments.CreateGroupFragment;
import com.example.fbuapp.fragments.groupFragments.GroupsFragment;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.google.android.material.chip.Chip;
import com.hootsuite.nachos.NachoTextView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class GroupManager {

    public static final String KEY_GROUP = "groupID";
    public static final String KEY_USER = "userID";
    public static final String KEY_MEMBER = "isMember";
    public static final String KEY_LOCATION = "location";
    public static final String TAG = "GroupManager";

    public GroupManager() {
    }

    public static int getMemberCount(Group group) {
        ParseQuery<GroupMappings> query = ParseQuery.getQuery(GroupMappings.class);
        // Specify what other data we would like to get back
        query.include("objectId");
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

    public static double getDistanceToGroup(Group group) throws ParseException {
        //ParseUser user = ParseUser.getCurrentUser();
        //String userLocation = ((Location) user.get(KEY_LOCATION)).getObjectId();
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(KEY_LOCATION);
        query.whereEqualTo("objectId", ((Location) ParseUser.getCurrentUser().get(KEY_LOCATION)).getObjectId());
        Location userLocation = query.find().get(0);
        ParseQuery<Location> queryGroup = ParseQuery.getQuery(Location.class);
        queryGroup.include(KEY_LOCATION);
        query.whereEqualTo("objectId", ((Location) group.get(KEY_LOCATION)).getObjectId());
        Location groupLocation = query.find().get(0);
        return userLocation.getLocation().distanceInMilesTo(groupLocation.getLocation());
    }

    public static double getDistanceToGroupUser(Group group, ParseUser user) throws ParseException {
        //ParseUser user = ParseUser.getCurrentUser();
        //String userLocation = ((Location) user.get(KEY_LOCATION)).getObjectId();
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(KEY_LOCATION);
        query.whereEqualTo("objectId", ((Location) user.get(KEY_LOCATION)).getObjectId());
        Location userLocation = query.find().get(0);
        ParseQuery<Location> queryGroup = ParseQuery.getQuery(Location.class);
        queryGroup.include(KEY_LOCATION);
        query.whereEqualTo("objectId", ((Location) group.get(KEY_LOCATION)).getObjectId());
        Location groupLocation = query.find().get(0);
        return userLocation.getLocation().distanceInMilesTo(groupLocation.getLocation());
    }

    public void createGroup(Group group, boolean isVirtual, String groupName, School school, Location meetingLocation,
                            String description, String day, String time, ArrayList<String> topics, Map<String, ParseUser> map,
                            NachoTextView nUsers, Fragment fragment, Context context, long timestamp) throws ParseException {
        if (isVirtual) group.setIsVirtual(true);
        else {
            group.setLocation(meetingLocation);
            group.setIsVirtual(false);
        }
        group.setName(groupName);
        SchoolManager schoolManager = new SchoolManager();
        school = schoolManager.querySchool(school);
        group.setSchool(school);
        group.setDescription(description);
        group.setMeetingDay(day);
        group.setMeetingTime(time);
        group.setTopics(topics);
        group.setTimeStamp(timestamp);
        // Save to parse
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e!= null) {
                    Log.e(TAG, "Error while saving group", e);
                    return;
                }
                // Create mappings and save
                GroupMappingsManager mappingsManager = new GroupMappingsManager();
                try {
                    mappingsManager.setUserMappings(map, nUsers, group);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                // Return instance of group back to parent fragment
                MainActivity activity = (MainActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                Fragment fragment = new GroupsFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                DialogFragment dialogFragment = (DialogFragment) activity.getSupportFragmentManager().findFragmentByTag("fragment_create_group");
                dialogFragment.dismiss();
            }
        });
    }

    public void queryGroups(RecyclerView.Adapter adapter, List<Group> userGroups) {
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
                    Log.i(TAG, "Group: " + group.getName());
                    userGroups.add(group);
                    adapter.notifyDataSetChanged();
                }
                Log.i(TAG, "final Size"+ userGroups.size());
            }
        });
    }

    public void queryGroupsUpcoming(RecyclerView.Adapter adapter, List<Group> userGroups, TextView tvNoMeetings, LottieAnimationView lottieAnimationView, TextView tvPendingInvites) {
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
                userGroups.clear();
                for (ParseObject mapping : userMappings) {
                    Group group = (Group) mapping.getParseObject(Group.KEY_GROUP_ID);

                    long timestamp = group.getTimeStamp();
                    long currentTime = System.currentTimeMillis() / 1000L;
                    if (currentTime > (timestamp + 3900)) {
                        while (currentTime > timestamp) {
                            timestamp += 604800;
                        }
                    }
                    if ((currentTime >= (group.getTimeStamp()) - 300) && currentTime <= (group.getTimeStamp() + 3900)) {
                        Log.i(TAG, group.getName());
                        userGroups.add(group);
                        adapter.notifyDataSetChanged();
                    }
                }

                // Update the UI with placeholder if there are no upcoming meetings
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvPendingInvites.getLayoutParams();

                if (userGroups.size() == 0) {
                    tvNoMeetings.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    params.addRule(RelativeLayout.BELOW, R.id.tvNoMeetings);
                } else {
                    tvNoMeetings.setVisibility(GONE);
                    lottieAnimationView.setVisibility(GONE);
                    params.addRule(RelativeLayout.BELOW, R.id.rvUpcomingMeetings);
                }
                Log.i(TAG, "final Size"+ userGroups.size());
            }
        });
    }


    public double getUserGroupCount() {
        // Specify which class to query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMappings");
        query.include(Group.KEY_GROUP_ID);
        // Get current users groups only
        query.whereEqualTo(Group.KEY_USER_ID, ParseUser.getCurrentUser());
        // Make sure user is a member of a group (not a pending invitation)
        query.whereEqualTo(Group.KEY_IS_MEMBER, true);
        // Get all the groups and add to the array
        try {
            List<ParseObject> userMappings = query.find();
            return userMappings.size();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void queryPendingGroups(PendingInvitesAdapter pendingInvitesAdapter, List<Group> pendingGroups) {
        // Specify which class to query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMappings");
        query.include(Group.KEY_GROUP_ID);
        // Get current users groups only
        query.whereEqualTo(Group.KEY_USER_ID, ParseUser.getCurrentUser());
        // Make sure user is a member of a group (not a pending invitation)
        query.whereEqualTo(Group.KEY_IS_MEMBER, false);
        // Get all the groups and add to the array
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> userMappings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting groups", e);
                    return;
                }
                pendingGroups.clear();
                for (ParseObject mapping : userMappings) {
                    Group group = (Group) mapping.getParseObject(Group.KEY_GROUP_ID);
                    Log.i(TAG, "Group: " + group.getName());
                    pendingGroups.add(group);
                    pendingInvitesAdapter.notifyDataSetChanged();
                }
                Log.i(TAG, "final Size"+ pendingGroups.size());
            }
        });
    }
}
