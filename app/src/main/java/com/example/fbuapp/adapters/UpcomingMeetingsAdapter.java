package com.example.fbuapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.models.Group;

import org.jetbrains.annotations.NotNull;

import java.util.List;
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

        private View rootView;
        private TextView tvUpcomingMeeting;
        private TextView tvGroupName;

        // TODO: Grab references to every view in row of recycler

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tvUpcomingMeeting = itemView.findViewById(R.id.tvTimeUntil);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            // TODO: Instantiate all of the references to different views
        }

        public void bind(Group group) {
            rootView.setVisibility(GONE);
            tvGroupName.setText(group.getName());
            // Initially, we want to make the object invisible until it is five minutes before the meeting

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    MainActivity activity = (MainActivity) mContext;
                    ((MainActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long timestamp = group.getTimeStamp();
                            long currentTime = System.currentTimeMillis() / 1000L;
                            TextView tvTimeUntil = rootView.findViewById(R.id.tvTimeUntil);

                            // If current time is past five minutes after meeting, update the timestamp and clear the view
                            if (currentTime > timestamp + 3900) {
                                while (currentTime > timestamp) {
                                    timestamp += 604800;
                                }
                                rootView.setVisibility(GONE);
                            }

                            // If meeting ended within the past 5 minutes, display expired for 5 minutes and then make view invisible
                            if (currentTime > timestamp && (currentTime - timestamp) <= 300) {
                                rootView.setVisibility(View.VISIBLE);
                                tvTimeUntil.setText("EXPIRED");
                            }

                            // If we are within five minutes of the meeting, display the countdown time from now till meeting
                            if (currentTime < timestamp && (timestamp - currentTime) <= 300) {
                                // TODO: display countdown timer until timestamp
                                double secondsRemaining = timestamp - currentTime;
                                double minutesRemaining = Math.ceil(secondsRemaining / 60);
                                rootView.setVisibility(View.VISIBLE);
                                tvTimeUntil.setText(minutesRemaining + " min until meeting");
                            }

                            // If the meeting is currently going on, within 60 minutes (or duration) of meeting display ongoing chip
                            if (currentTime > timestamp && currentTime < timestamp + 3600) {
                                // TODO: Display ongoing chip and join/map button to join meeting
                                rootView.setVisibility(View.VISIBLE);
                                tvTimeUntil.setText("ONGOING");
                            }
                        }
                    });

                }
            };

            // Set task to run every 10 seconds and update UI
            timer.scheduleAtFixedRate(timerTask, 0, 10000);

        }
    }


}
