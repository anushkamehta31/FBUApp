package com.example.fbuapp.managers;

import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.hootsuite.nachos.NachoTextView;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Map;

public class GroupMappingsManager {

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
}
