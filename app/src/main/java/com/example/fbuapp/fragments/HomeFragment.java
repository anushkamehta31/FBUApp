package com.example.fbuapp.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.PendingInvitesAdapter;
import com.example.fbuapp.adapters.UpcomingMeetingsAdapter;
import com.example.fbuapp.databinding.FragmentHomeBinding;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.models.Group;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeTag";
    public static List<Group> userGroups;
    public RecyclerView rvUpcomingMeetings;
    public static UpcomingMeetingsAdapter adapter;
    public FragmentHomeBinding binding;
    JobScheduler jobScheduler;
    public static GroupManager groupManager;
    JobInfo jobInfo;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Toolbar toolbar;
    public ViewPager viewPager;
    public static PendingInvitesAdapter pendingInvitesAdapter;
    public static List<Group> pendingGroups;


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
    public void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
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
        groupManager = new GroupManager();

        // Create the adapter for the pending invites list
        pendingGroups = new ArrayList<>();
        pendingInvitesAdapter = new PendingInvitesAdapter(pendingGroups, getContext());
        viewPager = binding.viewPagerPending;
        viewPager.setAdapter(pendingInvitesAdapter);
        viewPager.setPadding(15, 0, 200, 0);

        groupManager.queryPendingGroups(pendingInvitesAdapter, pendingGroups);






        // Set an alarm to query the user's groups every five minutes and check if there are any changes
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, 3000, pendingIntent);
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            groupManager.queryGroupsUpcoming(adapter, userGroups);
        }
    }
}