package com.example.fbuapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.managers.ZoomManager;
import com.example.fbuapp.models.Group;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;

public class UpcomingMeetingsAdapter extends RecyclerView.Adapter<UpcomingMeetingsAdapter.ViewHolder> {

    // List of all of the groups the user is part of
    List<Group> groups;
    Context mContext;
    public static final String TAG = "UpcomingMeetingsAdapter";

    public UpcomingMeetingsAdapter(List<Group> groups, Context mContext) {
        this.groups = groups;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Pass in a context and inflates item_upcoming_meeting.xml
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_upcoming_meeting, parent, false);
        // Wrap return value inside of a viewholder
        return new UpcomingMeetingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layout;
        LinearLayout.LayoutParams params;


        private View rootView;
        private TextView tvUpcomingMeeting;
        private TextView tvGroupName;
        private TextView tvMeetingTime;
        private ProgressBar progressBar;
        private Chip chipOngoing;
        private Chip chipExpired;
        private ImageButton ibMap;
        private MaterialButton btnJoinMeeting;

        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tvUpcomingMeeting = itemView.findViewById(R.id.tvTimeUntil);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            progressBar = itemView.findViewById(R.id.progressBar);
            chipOngoing = itemView.findViewById(R.id.chipOnGoing);
            chipExpired = itemView.findViewById(R.id.chipExpired);
            ibMap = itemView.findViewById(R.id.ibMap);
            btnJoinMeeting = itemView.findViewById(R.id.btnJoinMeeting);
            tvMeetingTime = itemView.findViewById(R.id.tvTime);

            layout =(RelativeLayout) itemView.findViewById(R.layout.item_upcoming_meeting);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void bind(Group group) {
            Log.i(TAG, group.getName());
            rootView.setVisibility(GONE);
            tvGroupName.setText(group.getName());
            tvMeetingTime.setText("Today @ " + group.getMeetingTime());
            ZoomManager zoomManager = new ZoomManager();
            zoomManager.initializeSdk(mContext);
            long timestamp = group.getTimeStamp();
            long currentTime = System.currentTimeMillis() / 1000L;

            if (currentTime > (timestamp + 3900)) {
                while (currentTime > timestamp) {
                    timestamp += 604800;
                }
            }

            // If meeting ended within the past 5 minutes, display expired for 5 minutes and then make view invisible
            if (currentTime > timestamp && (currentTime - timestamp) <= 300) {
                rootView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
                tvUpcomingMeeting.setVisibility(GONE);
                chipOngoing.setVisibility(GONE);
                chipExpired.setVisibility(View.VISIBLE);
                ibMap.setVisibility(GONE);
                btnJoinMeeting.setVisibility(GONE);
            }


            // If we are within five minutes of the meeting, display the countdown time from now till meeting
            if (currentTime < timestamp && (timestamp - currentTime) <= 300) {
                double secondsRemaining = timestamp - currentTime;
                double minutesRemaining = Math.ceil(secondsRemaining / 60);
                rootView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvUpcomingMeeting.setVisibility(View.VISIBLE);
                int progress = 5 - ((int) Math.round(minutesRemaining/5));
                progressBar.setProgress(progress);
                tvUpcomingMeeting.setText(minutesRemaining + " min.");
                ibMap.setVisibility(GONE);
                btnJoinMeeting.setVisibility(GONE);
            }

            // If the meeting is currently going on, within 60 minutes (or duration) of meeting display ongoing chip
            if (currentTime > timestamp && currentTime < timestamp + 3600) {
                rootView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
                tvUpcomingMeeting.setVisibility(GONE);
                chipOngoing.setVisibility(View.VISIBLE);
                chipExpired.setVisibility(GONE);

                if (group.isVirtual()) {
                    btnJoinMeeting.setVisibility(View.VISIBLE);
                    btnJoinMeeting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            zoomManager.joinMeeting(mContext, group.getMeetingID(), group.getPassword());
                        }
                    });
                } else {
                    ibMap.setVisibility(View.VISIBLE);
                    ibMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: Directions API
                        }
                    });
                }
            }
        }

    }


}
