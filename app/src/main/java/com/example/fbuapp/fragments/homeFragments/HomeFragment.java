package com.example.fbuapp.fragments.homeFragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.PendingInvitesAdapter;
import com.example.fbuapp.adapters.UpcomingMeetingsAdapter;
import com.example.fbuapp.databinding.FragmentHomeBinding;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.models.Group;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public static TextView tvNoMeetings;
    public static LottieAnimationView lottieAnimationView;
    public static LottieAnimationView noPendingGroups;
    public static TextView tvNoPendingInvites;
    public static TextView tvPendingInvites;


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
        tvNoMeetings = binding.tvNoMeetings;
        lottieAnimationView = binding.animationView;
        noPendingGroups = binding.noPendingAnimation;
        tvPendingInvites = binding.tvPendingInvites;
        tvNoPendingInvites = binding.tvNoPending;

        // Query the user's groups to be added to the views
        groupManager = new GroupManager();

        // Create the adapter for the pending invites list
        pendingGroups = new ArrayList<>();

        // Create interface for click listener
        PendingInvitesAdapter.OnClickListener onClickListener = new PendingInvitesAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                showInfoDialog(position, view);
            }
        };

        pendingInvitesAdapter = new PendingInvitesAdapter(pendingGroups, getContext(), onClickListener);
        viewPager = binding.viewPagerPending;
        viewPager.setAdapter(pendingInvitesAdapter);
        viewPager.setPadding(15, 0, 200, 0);

        // Run initial data query before setting repeated alarm
        queryData();


        // Set an alarm to query the user's groups every five minutes and check if there are any changes
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, 3000, pendingIntent);
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           queryData();
        }
    }

    public static void queryData() {
        groupManager.queryGroupsUpcoming(adapter, userGroups, tvNoMeetings, lottieAnimationView, tvPendingInvites);
        groupManager.queryPendingGroups(pendingInvitesAdapter, pendingGroups, tvNoPendingInvites, noPendingGroups);
    }

    // Method to display the dialog of a given pending group
    private void showInfoDialog(int position, View view) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pending_group_dialog_layout);
        Group group = pendingGroups.get(position);

        // Grab references to every view in the dialog here and set on click listeners to perform actions
        CircleImageView ivImage = dialog.findViewById(R.id.ivImage);
        TextView tvGroupName = dialog.findViewById(R.id.tvGroupName);
        TextView tvGroupDescription = dialog.findViewById(R.id.tvDescription);
        MaterialButton btnAccept = dialog.findViewById(R.id.btnAcceptGroupInvite);
        TextView tvRejectGroup = dialog.findViewById(R.id.tvRejectGroup);
        GroupMappingsManager groupMappingsManager = new GroupMappingsManager();

        // Instantiate all fields of dialog
        if (group.getImage() != null) {
            Glide.with(getContext()).load(group.getImage().getUrl()).into(ivImage);
        }
        tvGroupName.setText(group.getName());
        tvGroupDescription.setText(group.getDescription());

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupMappingsManager.editUserGroupMapping(group, true);
                dialog.dismiss();

                // Remove the group from the view
                pendingGroups.remove(position);
                pendingInvitesAdapter.notifyDataSetChanged();

                // Display a snackbar to show the action
                Snackbar.make(view, getString(R.string.group_joined), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pendingGroups.add(group);
                                pendingInvitesAdapter.notifyDataSetChanged();
                                groupMappingsManager.editUserGroupMapping(group, false);
                            }
                        })
                        .show();
            }
        });

        tvRejectGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupMappingsManager.deleteMapping(group, ParseUser.getCurrentUser());
                dialog.dismiss();

                // Remove the group from the view
                pendingGroups.remove(position);
                pendingInvitesAdapter.notifyDataSetChanged();

                // Display a snackbar to show the action
                Snackbar.make(view, getString(R.string.invite_declined), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pendingGroups.add(group);
                                pendingInvitesAdapter.notifyDataSetChanged();
                                groupMappingsManager.setInviteMapping(ParseUser.getCurrentUser(), group);
                            }
                        })
                        .show();
            }
        });


        // Show the dialog
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}