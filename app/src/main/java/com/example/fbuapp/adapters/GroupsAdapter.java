package com.example.fbuapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.fragments.groupFragments.GroupDetailsFragment;
import com.example.fbuapp.fragments.groupFragments.ViewGroupFragment;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.managers.SchoolManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.example.fbuapp.adapters.SwipeAdapter.KEY_LOCATION;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private Context mContext;
    private List<Group> mGroups;

    public GroupsAdapter(Context context, List<Group> groups) {
        this.mContext = context;
        this.mGroups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.ViewHolder holder, int position) {
        Group group = mGroups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View rootView;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            rootView = itemView;

            // OnClick listener to view the post in detail
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity activity = (MainActivity) mContext;
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    GroupDetailsFragment detailsFragment = new GroupDetailsFragment();
                    bundle.putParcelable("itemGroup", mGroups.get(getAdapterPosition()));
                    detailsFragment.setArguments(bundle);
                    ft.replace(R.id.flContainer, detailsFragment);
                    ft.commit();
                }
            });
        }

        public void bind(Group group) {
            LocationManager locationManager = new LocationManager();
            SchoolManager schoolManager = new SchoolManager();
            ImageView imageView = rootView.findViewById(R.id.ivBackgroundImage);
            TextView tvGroupName = rootView.findViewById(R.id.tvGroupName);
            TextView tvLocation = rootView.findViewById(R.id.tvLocation);
            TextView tvSchoolName = rootView.findViewById(R.id.tvSchoolName);
            TextView tvDistance = rootView.findViewById(R.id.tvDistance);

            ParseFile image = group.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).centerCrop().into(imageView);
            }

            tvGroupName.setText(group.getName());

            if (group.isVirtual()) {
                tvLocation.setText("Zoom Meeting");
            } else {
                locationManager.getSchoolFromGroup(tvLocation, group.getLocation().getObjectId());
            }

            schoolManager.getSchoolName(group.getSchool().getObjectId(), tvSchoolName);

            if (group.isVirtual()) {
                tvDistance.setText(R.string.vg);
            } else {
                ParseQuery<Location> query = new ParseQuery<Location>(Location.class);
                query.include("name");
                query.whereEqualTo("objectId", group.getLocation().getObjectId());
                query.getFirstInBackground(new GetCallback<Location>() {
                    @Override
                    public void done(Location location, ParseException e) {
                        ParseQuery<Location> queryLoc = ParseQuery.getQuery(Location.class);
                        queryLoc.include(KEY_LOCATION);
                        queryLoc.whereEqualTo("objectId", ((Location) ParseUser.getCurrentUser().get(KEY_LOCATION)).getObjectId());
                        queryLoc.getFirstInBackground(new GetCallback<Location>() {
                            @Override
                            public void done(Location userLocation, ParseException e) {
                                double distance = userLocation.getLocation().distanceInMilesTo(location.getLocation());
                                tvDistance.setText(String.format("%.1f", distance) + " mi");
                            }
                        });
                    }
                });
            }

        }
    }
}
