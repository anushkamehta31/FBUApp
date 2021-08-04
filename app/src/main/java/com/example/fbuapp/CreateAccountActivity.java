package com.example.fbuapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbuapp.databinding.ActivityCreateAccountBinding;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

// TODO: Convert this to a fragment and remove the repeated methods
public class CreateAccountActivity extends AppCompatActivity {

    public static final String TAG = "CreateAccountActivity";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_SCHOOL = "school";
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
    LatLng userSchoolLocation;
    School userSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCreateAccountBinding binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());

        // layout of activity is stored in root property
        View view = binding.getRoot();
        setContentView(view);

        // Change status bar color
        if (android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        tvLocation = binding.tvLocation;
        tvSchool = binding.tvSchool;
        btnSignUp = binding.btnSignUp;
        userLoc = new LatLng(0,0);
        userSchoolLocation = new LatLng(0,0);
        schoolAddress = getString(R.string.empty_string);
        locAddress = getString(R.string.empty_string);
        userSchool = new School();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.api_key));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlaceFinder(AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        tvSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlaceFinder(AUTOCOMPLETE_REQUEST_CODE_SCHOOL);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create ParseUser
                ParseUser user = initializeUser();

                if (locAddress.equals(getString(R.string.empty_string)) || schoolAddress.equals(getString(R.string.empty_string))) {
                    // TODO: Change to snackbar
                    Toast.makeText(getApplicationContext(),  getString(R.string.missing_field), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user location object and assign fields to user
                Location userLocation = new Location();
                userLocation.setLocation(userLoc);
                userLocation.setName(tvLocation.getText().toString());
                userLocation.setAddress(locAddress);
                Location.saveLocation(userLocation);

                // Check if school already exists in database and if it does not add it to the database
                querySchool(tvSchool.getText().toString());

                // Sign up user in background if everything no fields are missing
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Create the user profile
                        saveUserProfile(user, userLocation, userSchool);
                        // Log in the user
                        loginUser(user.getUsername(), etPassword.getText().toString());
                    }
                });
            }
        });

    }

    private void querySchool(String schoolName) {
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        // Specify what other data we would like to get back
        query.whereEqualTo(School.KEY_NAME, schoolName);
        query.getFirstInBackground(new GetCallback<School>() {
            @Override
            public void done(School object, ParseException e) {
                if (e == null) {
                    userSchool = object;
                } else {
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        // Add new school to database
                        userSchool = new School();
                        userSchool.setLocation(userSchoolLocation);
                        userSchool.setName(tvSchool.getText().toString());
                        School.saveSchool(userSchool);
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }

    private ParseUser initializeUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }


    public void launchPlaceFinder(int autocompleteRequestCode) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getApplicationContext());
        startActivityForResult(intent, autocompleteRequestCode);
    }

    private void saveUserProfile(ParseUser user, Location userLocation, School userSchool) {
        user.put(KEY_LOCATION, userLocation);
        user.put(KEY_SCHOOL, userSchool);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(CreateAccountActivity.this,
                            "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE ||
                requestCode == AUTOCOMPLETE_REQUEST_CODE_SCHOOL) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                    tvLocation.setText(place.getName());
                    userLoc = place.getLatLng();
                    locAddress = place.getAddress();
                } else {
                    tvSchool.setText(place.getName());
                    userSchoolLocation = place.getLatLng();
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

    public void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        // Navigate to main activity if user has properly signed in
        ParseUser.logInInBackground(username, password, new LogInCallback() { // Use background login to prevent bad user experience
            @Override
            public void done(ParseUser user, ParseException e) { // If the request succeeded, e will be null
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    // TODO: Change to snackbar
                    Toast.makeText(CreateAccountActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Navigate to main activity if the user has signed in properly
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}