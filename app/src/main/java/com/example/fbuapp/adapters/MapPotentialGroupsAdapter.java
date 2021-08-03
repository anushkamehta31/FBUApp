package com.example.fbuapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.managers.SchoolManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.fbuapp.adapters.SwipeAdapter.KEY_LOCATION;

public class MapPotentialGroupsAdapter extends PagerAdapter {

    private List<Group> pendingGroups;
    private LayoutInflater layoutInflater;
    private Context context;
    public static final String TAG = "MapPotentialGroups";

    public MapPotentialGroupsAdapter(List<Group> pendingGroups, Context context) {
        this.pendingGroups = pendingGroups;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pendingGroups.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_potential_group, container, false);

        Group group = pendingGroups.get(position);
        LocationManager locationManager = new LocationManager();
        SchoolManager schoolManager = new SchoolManager();
        ImageView imageView = view.findViewById(R.id.ivImage);
        TextView tvGroupName = view.findViewById(R.id.tvGroupName);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvSchoolName = view.findViewById(R.id.tvSchoolName);
        TextView tvDistance = view.findViewById(R.id.tvDistance);
        MaterialButton btnJoin = view.findViewById(R.id.btnJoin);

        ParseFile image = group.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).centerCrop().into(imageView);
        }

        tvGroupName.setText(group.getName());

        if (group.isVirtual()) {
            tvLocation.setText(context.getString(R.string.zoom_meeting));
        } else {
            locationManager.getSchoolFromGroup(tvLocation, group.getLocation().getObjectId());
        }

        schoolManager.getSchoolName(group.getSchool().getObjectId(), tvSchoolName);

        if (group.isVirtual()) {
            tvDistance.setText(R.string.vg);
        } else {
            Location groupLocation = group.getLocation();
            ParseQuery<Location> queryLoc = ParseQuery.getQuery(Location.class);
            queryLoc.include(KEY_LOCATION);
            queryLoc.whereEqualTo(Location.KEY_OBJECT_ID, ((Location) ParseUser.getCurrentUser().get(KEY_LOCATION)).getObjectId());
            queryLoc.getFirstInBackground(new GetCallback<Location>() {
                @Override
                public void done(Location userLocation, ParseException e) {
                    double distance = userLocation.getLocation().distanceInMilesTo(groupLocation.getLocation());
                    tvDistance.setText(String.format("%.1f", distance) + " mi");
                }
            });
        }

        // Set OnClick Listener to Join the Group
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJoin.setText(context.getString(R.string.joined));
                GroupMappingsManager groupManager = new GroupMappingsManager();
                groupManager.setUserMapping(ParseUser.getCurrentUser(), group);
            }
        });

        container.addView(view, 0);
        return view;
    }

}
