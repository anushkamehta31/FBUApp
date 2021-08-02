package com.example.fbuapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>{

    // Need a context to inflate a view
    Context context;
    List<ParseUser> groupMembers;

    public GroupMemberAdapter(Context context, List<ParseUser> groupMembers) {
        this.context = context;
        this.groupMembers = groupMembers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Pass in a context and inflates item_member.xml
        View memberView = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        // Wrap return value inside of a viewholder
        return new ViewHolder(memberView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Get the movie at the position in the list
        ParseUser user = groupMembers.get(position);
        // Bind the movie data into the viewholder
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    // ViewHolder is a representation of a row/item in the recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Member variables for each view in the view holder
        TextView username;
        ImageView ivProfile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get references to each component (we can't use view binding for MovieAdapter class)
            ivProfile = itemView.findViewById(R.id.ivProfileImage);
            username = itemView.findViewById(R.id.tvUsername);
        }

        public void bind(ParseUser user) {
            username.setText(user.getUsername());
            String imageURL;
            int placeholder;

            ParseFile image = user.getParseFile("profileImage");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).fitCenter().centerCrop().into(ivProfile);
            } else {
                Glide.with(context).load(R.drawable.nopfp).fitCenter().centerCrop().into(ivProfile);
            }

        }
    }
}
