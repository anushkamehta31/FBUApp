package com.example.fbuapp.fragments;

import android.app.AlarmManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GroupsAdapter;
import com.example.fbuapp.adapters.UpcomingMeetingsAdapter;
import com.example.fbuapp.databinding.FragmentFindGroupBinding;
import com.example.fbuapp.databinding.FragmentHomeBinding;
import com.example.fbuapp.managers.GaleShapley;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.models.Group;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeTag";
    public List<Group> userGroups;
    public RecyclerView rvUpcomingMeetings;
    public UpcomingMeetingsAdapter adapter;
    public FragmentHomeBinding binding;

    public HomeFragment() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the current time in milliseconds and convert to UTC timestamp (time in seconds)
        long currentTime = System.currentTimeMillis() / 1000L;

        // Instantiate items
        userGroups = new ArrayList<>();
        rvUpcomingMeetings = binding.rvUpcomingMeetings;
        adapter = new UpcomingMeetingsAdapter(userGroups, getContext());
        rvUpcomingMeetings.setAdapter(adapter);
        rvUpcomingMeetings.setLayoutManager(new LinearLayoutManager(getContext()));

        // Query the user's groups to be added to the views
        GroupManager groupManager = new GroupManager();
        groupManager.queryGroups(adapter, userGroups);
    }
}