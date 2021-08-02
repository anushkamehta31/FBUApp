package com.example.fbuapp.fragments.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.airbnb.lottie.L;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.LinksAdapter;
import com.example.fbuapp.databinding.FragmentImagesBinding;
import com.example.fbuapp.databinding.FragmentLinksBinding;
import com.example.fbuapp.fragments.groupFragments.CreateGroupFragment;
import com.example.fbuapp.fragments.groupFragments.GroupsFragment;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Image;
import com.example.fbuapp.models.Link;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class LinksFragment extends Fragment {

    public Group group;
    public List<Link> groupLinks;
    FragmentLinksBinding binding;

    // Get references to views
    RecyclerView rvLinks;
    ImageButton btnAddLink;

    // Reference to adapter
    LinksAdapter adapter;

    // Resource manager to perform queries
    ResourceManager resourceManager;


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
        binding = FragmentLinksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate resource manager
        resourceManager = new ResourceManager();

        // Instantiate empty list
        groupLinks = new ArrayList<>();

        // instantiate view items
        rvLinks = binding.rvLinks;
        btnAddLink = binding.btnAddLink;

        // instantiate adapter
        adapter = new LinksAdapter(getContext(), groupLinks);
        rvLinks.setAdapter(adapter);
        rvLinks.setLayoutManager(new LinearLayoutManager(getContext()));

        // Query current group links
        resourceManager.queryLinks(group, groupLinks, adapter);

        btnAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddLink();
            }
        });
    }

    private void goAddLink() {
        MainActivity activity = (MainActivity) getContext();
        FragmentManager ft = activity.getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable("itemGroup", group);
        AddLinkFragment fragment = new AddLinkFragment();
        fragment.setArguments(bundle);
        // Launch create group dialog fragment and set the target fragment for later use when
        // sending results
        fragment.setTargetFragment(LinksFragment.this, 300);
        fragment.show(ft, "fragment_add_link");
    }
}