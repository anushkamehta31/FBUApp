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
import com.example.fbuapp.databinding.FragmentCreateGroupBinding;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

public class AddLinkFragment extends DialogFragment {

    FragmentAddLinkBinding binding;
    EditText etLinkName;
    EditText etLinkBody;
    MaterialButton btnGo;
    ResourceManager resourceManager;
    public Group group;

    public AddLinkFragment() {
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
        binding = FragmentAddLinkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to all views
        etLinkName = binding.etLinkName;
        etLinkBody = binding.etLinkBody;
        btnGo = binding.btnGo;

        // Instantiate resource manager
        resourceManager = new ResourceManager();

        // Set on click listener for closing the create group link
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLink();
            }
        });
    }

    private void createLink() {
        // If the user forgot a field, make a toast to notify them
        if (etLinkBody.getText().toString().equals("") || (etLinkName.getText().toString().equals(""))){
            Toast.makeText(getContext(),
                    "Missing fields", Toast.LENGTH_SHORT).show();
            return;
        }

        resourceManager.saveLink(etLinkName.getText().toString(), etLinkBody.getText().toString(), group, getContext(), this);
    }
}