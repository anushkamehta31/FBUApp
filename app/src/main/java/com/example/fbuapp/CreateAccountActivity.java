package com.example.fbuapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbuapp.databinding.ActivityCreateAccountBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String TAG = "CreateAccountActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE_SCHOOL = 2;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvLocation;
    private TextView tvSchool;
    private Button btnSignUp;
    private String locAddress;
    private String schoolAddress;
    LatLng userLoc;
    LatLng userSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCreateAccountBinding binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());

        // layout of activity is stored in root property
        View view = binding.getRoot();
        setContentView(view);

        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        tvLocation = binding.tvLocation;
        tvSchool = binding.tvSchool;
        btnSignUp = binding.btnSignUp;
        userLoc = new LatLng(0,0);
        userSchool = new LatLng(0,0);
        locAddress = "";
        schoolAddress = "";

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.api_key));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });

        tvSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_SCHOOL);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Create ParseUser
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                if (locAddress.equals("") || schoolAddress.equals("")) {
                    // TODO: Change to snackbar
                    Toast.makeText(getApplicationContext(),  "missingFields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Create location object
                // saveUser(location, user); // Sign up user in background
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE || requestCode == AUTOCOMPLETE_REQUEST_CODE_SCHOOL) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                    tvLocation.setText(place.getName());
                    userLoc = place.getLatLng();
                    locAddress = place.getAddress();
                } else {
                    tvSchool.setText(place.getName());
                    userSchool = place.getLatLng();
                    schoolAddress = place.getAddress();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}