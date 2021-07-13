package com.example.fbuapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fbuapp.LoginActivity;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;

import com.parse.ParseUser;

import static com.parse.Parse.getApplicationContext;

public class ProfileFragment extends Fragment {

    private Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here. E.g, view lookups and attaching view listeners
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                MainActivity activity = (MainActivity) getContext();
                activity.finish();
            }
        });
    }
}