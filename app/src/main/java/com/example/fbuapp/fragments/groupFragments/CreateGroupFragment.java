package com.example.fbuapp.fragments.groupFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fbuapp.R;
import com.google.android.material.chip.Chip;
import com.hootsuite.nachos.NachoTextView;
import com.parse.ParseUser;

import java.util.List;
import java.util.Map;


public class CreateGroupFragment extends DialogFragment {

    private Map<String, ParseUser> users;
    private List<String> usernames;
    private NachoTextView acTopics;
    private Chip cVirtual;
    private Chip cInPerson;
    private TextView tvLocation;
    private int shortAnimationDuration;
    public static final String TAG = "CreateGroupFragment";

    public CreateGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        acTopics = view.findViewById(R.id.nTopics);
        String[] topics = getResources().getStringArray(R.array.topics_array);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, topics);
        acTopics.setAdapter(adapter);

        // Create the tvLocation reference variable and set it to Gone initially
        tvLocation = view.findViewById(R.id.tvLoc);
        tvLocation.setVisibility(View.GONE);

        // Get tags to chips
        cVirtual = view.findViewById(R.id.chipVirtual);
        cInPerson = view.findViewById(R.id.chipInPerson);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // If the In-Person tag is checked, we want to display the Location View
        cInPerson.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvLocation.setVisibility(View.VISIBLE);
            }
        });

        // If the In-Person tag is checked, we want to display the Location View
        cVirtual.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvLocation.setVisibility(View.GONE);
            }
        });


    }

    public void queryUsers() {

    }

    public void createGroup() {
        // Check that all fields are filled out
        // Create new Group Object
        // Populate group object fields
        // Save to parse
        // Return instance of group back to parent fragment
    }
}