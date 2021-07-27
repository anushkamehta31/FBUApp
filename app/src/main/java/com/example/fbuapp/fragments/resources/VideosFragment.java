package com.example.fbuapp.fragments.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GridAdapter;
import com.example.fbuapp.adapters.VideoAdapter;
import com.example.fbuapp.databinding.FragmentImagesBinding;
import com.example.fbuapp.databinding.FragmentVideosBinding;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Image;
import com.example.fbuapp.models.Video;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {


    public Group group;
    public List<Video> videos;
    RecyclerView rvVideos;
    FragmentVideosBinding binding;
    public VideoAdapter adapter;
    ImageButton btnAddVideo;
    private SupportMapFragment mapFragment;

    public VideosFragment() {
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
        binding = FragmentVideosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvVideos = binding.rvVideos;
        btnAddVideo = binding.btnUploadVideo;

        videos = new ArrayList<>();
        adapter = new VideoAdapter(videos, getContext());
        rvVideos.setAdapter(adapter);
        rvVideos.setLayoutManager(new LinearLayoutManager(getContext()));


        // Query the images
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.queryVideos(group, videos, adapter);

        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: compose dialog where a user can put in a link to a video
            }
        });

    }

}