package com.example.fbuapp.managers;

import android.util.Log;

import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.School;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.fbuapp.models.Group.KEY_LIKES;
import static com.example.fbuapp.models.Group.KEY_PASSES;
import static com.example.fbuapp.models.Group.KEY_SCHOOL;

public class GaleShapley {

    public static final String TAG = "GaleShapley";

    public List<Object> predictGroupPreferences(Group group) {
        // Compatibility score of user * distance * school
        // Compatibility = actual members / invited members
        HashMap<Object, Double> groupPreferenceList = new HashMap<>();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("objectId");
        List<Object> groupPreference = new ArrayList<>();
        try {
            List<ParseUser> users = query.find();
            for (ParseUser user : users) {
                double compatibilityScore = 0;
                if (ParseUser.getCurrentUser().get(KEY_LIKES) != null) {
                    int likes = (int) ParseUser.getCurrentUser().get(KEY_LIKES);
                    GroupManager gm = new GroupManager();
                    compatibilityScore += gm.getUserGroupCount();
                    if (likes != 0) {
                        compatibilityScore /= likes;
                    }
                }
                // If the school matches the user's school, increase the compatibility score
                if (((School) user.get(KEY_SCHOOL)).getObjectId().equals(group.getSchool().getObjectId())) {
                    compatibilityScore *= 10;
                }
                try {
                    if (!group.isVirtual() && GroupManager.getDistanceToGroupUser(group, user) < 20) {
                        compatibilityScore *= 5;
                    }
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                groupPreferenceList.put(user, compatibilityScore);
            }
            HashMap<Object, Double> hml = sortByValue(groupPreferenceList);
            for ( Object key : hml.keySet() ) {
                groupPreference.add(key);
            }
            return groupPreference;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object> predictUserPreferences(ParseUser user) {
        // Compatibility score of user * distance * school
        // Compatibility = actual members / invited members
        HashMap<Object, Double> userPreferenceList = new HashMap<>();
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.include("objectId");
        List<Object> userPreference = new ArrayList<>();
        try {
            List<Group> groups = query.find();
            for (Group group : groups) {
                double compatibilityScore = 0;
                if (group.get(KEY_PASSES) != null && group.get(KEY_LIKES) != null && group.getPasses()!=0) {
                    compatibilityScore = group.getLikes()/group.getPasses();
                }
                // If the school matches the user's school, increase the compatibility score
                if (((School) user.get(KEY_SCHOOL)).getObjectId().equals(group.getSchool().getObjectId())) {
                    compatibilityScore *= 10;
                }
                try {
                    if (!group.isVirtual() && GroupManager.getDistanceToGroupUser(group, user) < 20) {
                        compatibilityScore *= 5;
                    }
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                userPreferenceList.put(group, compatibilityScore);
            }
            HashMap<Object, Double> hml = sortByValue(userPreferenceList);
            for ( Object key : hml.keySet() ) {
                userPreference.add(key);
            }
            return userPreference;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Use Gale Shapley algorithm to calculate matches
    public Map<ParseUser, Group> calculateMatches() throws ParseException {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.include("objectId");
        ParseQuery<Group> groupQuery = ParseQuery.getQuery(Group.class);
        groupQuery.include("objectId");
        int engagedCount = 0;
        int usersCount = query.count();
        int groupCount = groupQuery.count();
        List<ParseUser> users = query.find();
        List<Group> groups = groupQuery.find();
        ParseUser[] groupPreference = new ParseUser[groupCount];
        boolean[] userEngaged = new boolean[usersCount];

        for (ParseUser user:users) {
            Log.i(TAG, user.getUsername());
        }


        while (engagedCount < Math.min(groupCount, usersCount)) {
            int free;
            for (free = 0; free < usersCount; free++) {
                if (!userEngaged[free]) break;
            }
            for (int i = 0; i < groupCount && !userEngaged[free]; i++) {
                int index = -1;
                Group nextChoice = (Group) predictUserPreferences(users.get(free)).get(i);
                Log.i(TAG, nextChoice.getName());
                for (int j = 0; j < groupCount; j++) {
                    if (groups.get(j).getObjectId().equals(nextChoice.getObjectId())) {
                        index = j;
                    }
                }
                List<Object> userPreferences = predictUserPreferences(users.get(free));
                if (index != -1) {
                    if (groupPreference[index] == null)
                    {
                        groupPreference[index] = users.get(free);
                        userEngaged[free] = true;
                        engagedCount++;
                        Log.i(TAG, (users.get(free)).getUsername() + " was recommended to " + groups.get(index).getName());
                    }
                    else
                    {
                        ParseUser currentUser = groupPreference[index];
                        if (morePreference(currentUser, users.get(free), index, usersCount, groups.get(index)))
                        {
                            groupPreference[index] = users.get(free);
                            userEngaged[free] = true;
                            userEngaged[users.indexOf(currentUser)] = false;
                        }
                    }
                }
            }
        }
        // Return assignment
        Map<ParseUser, Group> matchedPairs = new HashMap<>();
        for (int i = 0; i < groupPreference.length; i++) {
            if (groupPreference[i] != null) {
                matchedPairs.put(groupPreference[i], groups.get(i));
            }
        }

        return matchedPairs;
    }

    private boolean morePreference(ParseUser currentUser, ParseUser newUser, int index, int usersCount, Group group)
    {
        List<Object> preferences = predictGroupPreferences(group);
        for (int i = 0; i < preferences.size(); i++)
        {
            ParseUser user = (ParseUser) preferences.get(i);
            if (user.getObjectId().equals(newUser.getObjectId())) {
                return true;
            }
            if (user.getObjectId().equals(currentUser.getObjectId())) {
                return false;
            }
        }
        return false;
    }

    // function to sort hashmap by values
    public static HashMap<Object, Double> sortByValue(HashMap<Object, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Object, Double> > list =
                new LinkedList<Map.Entry<Object, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Object, Double> >() {
            public int compare(Map.Entry<Object, Double> o1,
                               Map.Entry<Object, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Object, Double> temp = new LinkedHashMap<Object, Double>();
        for (Map.Entry<Object, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
