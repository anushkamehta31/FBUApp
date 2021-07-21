package com.example.fbuapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.fbuapp.models.Group;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SwipeAdapter extends PagerAdapter {

    private List<Group> groups;
    private LayoutInflater layoutInflater;
    private Context context;

    public SwipeAdapter(List<Group> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_join, container, false);

        Group group = groups.get(position);
        ImageView imageView;
        TextView tvName, tvDescription, tvDistance, tvMembers;

        imageView = view.findViewById(R.id.ivImage);
        tvName = view.findViewById(R.id.tvNameItem);
        tvDescription = view.findViewById(R.id.tvDescriptionItem);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvMembers = view.findViewById(R.id.tvMembers);

        ParseFile image = group.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).centerCrop().into(imageView);
        }

        tvName.setText(group.getName());
        tvDescription.setText(group.getDescription());
        tvMembers.setText(GroupManager.getMemberCount(group) + " " + context.getString(R.string.members));

        if (group.isVirtual()) {
            tvDistance.setText(R.string.vg);
        } else {
            try {
                tvDistance.setText(String.valueOf(GroupManager.getDistanceToGroup(group)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
