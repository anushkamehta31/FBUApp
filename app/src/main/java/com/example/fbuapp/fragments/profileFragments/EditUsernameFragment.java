package com.example.fbuapp.fragments.profileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentEditUsernameBinding;
import com.example.fbuapp.databinding.FragmentProfileBinding;
import com.example.fbuapp.fragments.searchFragments.SearchGroupFragment;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class EditUsernameFragment extends Fragment {

    FragmentEditUsernameBinding binding;
    ImageButton ibBack;
    TextView tvDone;
    EditText etUsername;

    public EditUsernameFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditUsernameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ibBack = binding.ibBack;
        tvDone = binding.tvDone;
        etUsername = binding.etUsername;

        etUsername.setText(ParseUser.getCurrentUser().getUsername());

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToProfile();
            }
        });


        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.getCurrentUser().setUsername(etUsername.getText().toString());
                ParseUser.getCurrentUser().saveInBackground();
                returnToProfile();
            }
        });

    }

    private void returnToProfile() {
        MainActivity activity = (MainActivity) getContext();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ProfileFragment fragment = new ProfileFragment();
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }


}