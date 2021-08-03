package com.example.fbuapp.fragments.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.fbuapp.R;
import com.example.fbuapp.adapters.LinksAdapter;
import com.example.fbuapp.adapters.NotesAdapter;
import com.example.fbuapp.databinding.FragmentLinksBinding;
import com.example.fbuapp.databinding.FragmentNotesBinding;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Link;
import com.example.fbuapp.models.Note;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    public Group group;
    public List<Note> groupNotes;
    FragmentNotesBinding binding;

    // Get references to views
    RecyclerView rvNotes;
    ImageButton btnAddNote;

    // Reference to adapter
    NotesAdapter adapter;

    // Resource manager to perform queries
    ResourceManager resourceManager;

    public NotesFragment() {
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
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate resource manager
        resourceManager = new ResourceManager();

        // Instantiate empty list
        groupNotes = new ArrayList<>();

        // instantiate view items
        rvNotes = binding.rvNotes;
        btnAddNote = binding.btnAddNote;

        // instantiate adapter
        adapter = new NotesAdapter(groupNotes, getContext());
        rvNotes.setAdapter(adapter);
        rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        resourceManager.queryNotes(group, groupNotes, adapter);
    }
}