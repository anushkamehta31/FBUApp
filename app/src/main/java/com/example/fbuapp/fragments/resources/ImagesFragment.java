package com.example.fbuapp.fragments.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GridAdapter;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ImagesFragment extends Fragment {

    public Group group;
    public List<Image> groupImages;
    GridView gvPosts;
    public GridAdapter adapter;
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
        // btnUpload = view.findViewById(R.id.btnUploadPhoto);
        // btnAddPhoto = view.findViewById(R.id.btnAdd);
        gvPosts.setAdapter(adapter);

        // Query the images
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.queryImages(group, groupImages, adapter);

    }

    private void launchCamera() {
    }


}