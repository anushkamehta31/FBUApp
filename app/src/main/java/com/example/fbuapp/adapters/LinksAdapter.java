package com.example.fbuapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbuapp.R;
import com.example.fbuapp.models.Link;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.ViewHolder>{

    Context context;
    List<Link> groupLinks;

    public LinksAdapter(Context context, List<Link> groupLinks) {
        this.context = context;
        this.groupLinks = groupLinks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_link, parent, false);
        // Wrap return value inside of a viewholder
        return new LinksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the movie at the position in the list
        Link link = groupLinks.get(position);
        // Bind the movie data into the viewholder
        holder.bind(link);
    }

    @Override
    public int getItemCount() {
        return groupLinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDescription;
        TextView tvUrl;
        TextView tvAddedBy;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvUrl = (TextView) itemView.findViewById(R.id.tvUrl);
            tvAddedBy = (TextView) itemView.findViewById(R.id.tvAddedBy);
        }

        public void bind(Link link) {
            tvDescription.setText(link.getDescription());
            tvUrl.setText(link.getUrl());
            tvAddedBy.setText(context.getString(R.string.added_by)+link.getUser().getUsername());
        }
    }
}
