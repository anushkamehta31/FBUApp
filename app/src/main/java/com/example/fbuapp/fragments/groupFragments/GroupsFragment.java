package com.example.fbuapp.fragments.groupFragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GroupsAdapter;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    protected RecyclerView rvGroups;
    public static final String TAG = "GroupsFragment";
    public List<Group> userGroups;
    public GroupsAdapter adapter;
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

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        rvGroups.setLayoutManager(layout);
        // Get the groups
        GroupManager groupManager = new GroupManager();
        groupManager.queryGroups(adapter, userGroups);

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
        // Launch create group dialog fragment and set the target fragment for later use when
        // sending results
        fragment.setTargetFragment(GroupsFragment.this, 300);
        fragment.show(ft, "fragment_create_group");
    }


}