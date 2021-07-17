package com.example.fbuapp.managers;

import com.example.fbuapp.models.Group;
import com.parse.ParseException;

public class GroupManager {

    public GroupManager() {
    }

    public void saveGroup(Group group) throws ParseException {
        group.save();
    }
}
