package com.example.fbuapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.managers.SchoolManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.google.android.material.button.MaterialButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SwipeAdapter extends PagerAdapter {

    private List<Group> groups;
    private LayoutInflater layoutInflater;
    private Context context;
    public static final String TAG = "SwipeAdapter";
    public static final String KEY_LOCATION = "location";

    public SwipeAdapter(List<Group> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    public void myRemove(int position){
        groups.remove(position);
        SwipeAdapter.super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @SuppressLint({"SetTextI18n", "CutPasteId", "ResourceAsColor"})
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_join, container, false);

        Group group = groups.get(position);
        ImageView imageView;
        TextView tvName, tvDescription, tvDistance, tvMembers, tvLocation, tvSchool;
        MaterialButton btnJoin;

        imageView = view.findViewById(R.id.ivImage);
        tvName = view.findViewById(R.id.tvNameItem);
        tvDescription = view.findViewById(R.id.tvDescriptionItem);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvMembers = view.findViewById(R.id.tvMembers);
        tvLocation = view.findViewById(R.id.tvLocationItem);
        btnJoin = view.findViewById(R.id.btnJoinMeeting);
        tvSchool = view.findViewById(R.id.tvSchoolName);

        tvSchool.setText(group.getSchool().getName());

        ParseFile image = group.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).into(imageView);
        }

        tvName.setText(group.getName());
        tvDescription.setText(group.getDescription());
        int members = GroupManager.getMemberCount(group);
        tvMembers.setText(GroupManager.getMemberCount(group) + " " + context.getResources().getQuantityString(R.plurals.memberTest, members));

        if (group.isVirtual()) {
            tvDistance.setText(R.string.vg);
            tvLocation.setText(R.string.zoom_meeting);
        } else {
            ParseQuery<Location> query = new ParseQuery<Location>(Location.class);
            query.include("name");
            query.whereEqualTo("objectId", group.getLocation().getObjectId());
            query.getFirstInBackground(new GetCallback<Location>() {
                @Override
                public void done(Location location, ParseException e) {
                    String locName = location.getName();
                    tvLocation.setVisibility(View.VISIBLE);
                    tvLocation.setText(locName);
                    ParseQuery<Location> queryLoc = ParseQuery.getQuery(Location.class);
                    queryLoc.include(KEY_LOCATION);
                    queryLoc.whereEqualTo("objectId", ((Location) ParseUser.getCurrentUser().get(KEY_LOCATION)).getObjectId());
                    queryLoc.getFirstInBackground(new GetCallback<Location>() {
                        @Override
                        public void done(Location userLocation, ParseException e) {
                            double distance = userLocation.getLocation().distanceInMilesTo(location.getLocation());
                            tvDistance.setText(String.format("%.1f", distance) + " " + context.getString(R.string.miles_from_user));
                        }
                    });
                }
            });
        }

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJoin.setText("Joined");
                GroupMappingsManager groupManager = new GroupMappingsManager();
                groupManager.setUserMapping(ParseUser.getCurrentUser(), group);
            }
        });

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
