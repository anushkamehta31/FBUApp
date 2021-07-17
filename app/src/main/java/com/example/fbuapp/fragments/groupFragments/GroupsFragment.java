package com.example.fbuapp.fragments.groupFragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GroupsAdapter;
import com.example.fbuapp.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment implements CreateGroupFragment.CreateGroupDialogListener {

    protected RecyclerView rvGroups;
    public static final String TAG = "GroupsFragment";
    private List<Group> userGroups;
    private GroupsAdapter adapter;
    FloatingActionButton btnCreate;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvGroups = view.findViewById(R.id.rvGroups);

        userGroups = new ArrayList<>();
        adapter = new GroupsAdapter(getContext(), userGroups);

        rvGroups.setAdapter(adapter);

        GridLayoutManager layout = new GridLayoutManager(getContext(), 2);
        rvGroups.setLayoutManager(layout);
        // Get the groups
        queryGroups();

        // Create group button initialization
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCreate.show();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCreateGroup();
            }
        });

    }

    private void goCreateGroup() {
        MainActivity activity = (MainActivity) getContext();
        FragmentManager ft = activity.getSupportFragmentManager();
        CreateGroupFragment fragment = new CreateGroupFragment();
        fragment.show(ft, "fragment_create_group");
    }


    private void queryGroups() {
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
                    adapter.notifyDataSetChanged();
                    userGroups.add(group);
                }
                Log.i(TAG, "final Size"+ userGroups.size());
            }
        });

    }


    @Override
    public void onFinishCreateGroupDialog(Group group) {
        userGroups.add(group);
        adapter.notifyDataSetChanged();
    }
}