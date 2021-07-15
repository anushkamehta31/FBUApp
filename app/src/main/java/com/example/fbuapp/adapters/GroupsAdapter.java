package com.example.fbuapp.adapters;

import android.content.Context;
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
import com.example.fbuapp.fragments.groupFragments.ViewGroupFragment;
import com.example.fbuapp.models.Group;
import com.parse.ParseFile;

import java.util.List;

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
        private ImageView ivImage;
        private TextView tvName;
        private View vPalette;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            rootView = itemView;
            ivImage = (ImageView)itemView.findViewById(R.id.ivImage);
            tvName = (TextView)itemView.findViewById(R.id.tvName);

            // OnClick listener to view the post in detail
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity activity = (MainActivity) mContext;
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ViewGroupFragment fragment = ViewGroupFragment.newInstance(mGroups.get(getAdapterPosition()));
                    ft.replace(R.id.flContainer, fragment);
                    ft.commit();
                }
            });
        }

        public void bind(Group group) {
            rootView.setTag(group);
            tvName.setText(group.getName());
            ParseFile image = group.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).centerCrop().into(ivImage);
            }
        }
    }
}
