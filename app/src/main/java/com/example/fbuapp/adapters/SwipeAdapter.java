package com.example.fbuapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.Location;
import com.google.android.material.button.MaterialButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SwipeAdapter extends PagerAdapter {

    private List<Group> groups;
    private LayoutInflater layoutInflater;
    private Context context;
    public static final String TAG = "SwipeAdapter";

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
        TextView tvName, tvDescription, tvDistance, tvMembers, tvLocation;
        MaterialButton btnJoin;

        imageView = view.findViewById(R.id.ivImage);
        tvName = view.findViewById(R.id.tvNameItem);
        tvDescription = view.findViewById(R.id.tvDescriptionItem);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvMembers = view.findViewById(R.id.tvMembers);
        tvLocation = view.findViewById(R.id.tvLocation);
        btnJoin = view.findViewById(R.id.btnJoin);

        ParseFile image = group.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).centerCrop()
                    .transform(new RoundedCornersTransformation(60,20)).into(imageView);
        }

        tvName.setText(group.getName());
        tvDescription.setText(group.getDescription());
        tvMembers.setText(GroupManager.getMemberCount(group) + " " + context.getString(R.string.members));

        if (group.isVirtual()) {
            tvDistance.setText(R.string.vg);
        } else {
            try {
                ParseQuery<Location> query = new ParseQuery<Location>(Location.class);
                query.include("name");
                query.whereEqualTo("objectId", group.getLocation().getObjectId());
                String locName = query.getFirst().getName();
                tvLocation.setText(locName);
                tvDistance.setText(String.format("%.2f",GroupManager.getDistanceToGroup(group)) + " miles from you");
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
