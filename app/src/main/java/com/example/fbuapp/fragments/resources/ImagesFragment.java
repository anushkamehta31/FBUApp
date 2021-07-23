package com.example.fbuapp.fragments.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GridAdapter;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ImagesFragment extends Fragment {

    private Group group;
    protected List<Image> groupImages;
    GridView gvPosts;
    GridAdapter adapter;
    ImageButton btnAddPhoto;
    ImageButton btnUpload;
    public static final String TAG = "ImagesFragment";

    public ImagesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments passed
        group = (Group) getArguments().getParcelable("itemGroup");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gvPosts = view.findViewById(R.id.gvPosts);
        groupImages = new ArrayList<>();
        adapter = new GridAdapter(getContext(), groupImages);
        btnUpload = view.findViewById(R.id.btnUploadPhoto);
        btnAddPhoto = view.findViewById(R.id.btnAdd);
        gvPosts.setAdapter(adapter);
        queryImages();

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow user to choose a photo
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allow the user to choose a photo
            }
        });
    }

    protected void queryImages() {
        // Specify which class to query
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        // Specify what other data we would like to get back
        query.include(Image.KEY_GROUP);
        // Get current users posts only
        query.whereEqualTo(Image.KEY_GROUP, group);
        // Get most recent posts at top
        query.addDescendingOrder(Image.KEY_CREATED_AT);
        // Get all the posts
        query.findInBackground(new FindCallback<Image>() {
            @Override
            public void done(List<Image> images, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                groupImages.addAll(images);
                adapter.notifyDataSetChanged();
            }
        });
    }
}