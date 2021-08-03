package com.example.fbuapp.fragments.resources;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentAddLinkBinding;
import com.example.fbuapp.databinding.FragmentAddNoteBinding;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

public class AddNoteFragment extends DialogFragment {

    FragmentAddNoteBinding binding;
    EditText etNoteTitle;
    EditText etNoteText;
    MaterialButton btnGo;
    ResourceManager resourceManager;
    public Group group;

    public AddNoteFragment() {
        // Required empty public constructor
    }

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = (Group) getArguments().getParcelable("itemGroup");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get references to all views
        etNoteTitle = binding.etNoteTitle;
        etNoteText = binding.etNoteBody;
        btnGo = binding.btnGo;

        // Instantiate resource manager
        resourceManager = new ResourceManager();

        // Set on click listener for closing the create group link
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote();
            }
        });
    }

    private void createNote() {
        if (etNoteTitle.getText().toString().equals("") || (etNoteText.getText().toString().equals(""))){
            Toast.makeText(getContext(),
                    getContext().getString(R.string.missing_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        resourceManager.saveNote(etNoteTitle.getText().toString(), etNoteText.getText().toString(), group, getContext(), this);

    }
}