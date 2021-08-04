package com.example.fbuapp.fragments.profileFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentEditSchoolBinding;
import com.example.fbuapp.databinding.FragmentEditUsernameBinding;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.managers.SchoolManager;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.fbuapp.CreateAccountActivity.KEY_SCHOOL;
import static com.example.fbuapp.managers.LocationManager.KEY_ID;
import static com.example.fbuapp.managers.LocationManager.KEY_LOCATION;
import static com.example.fbuapp.managers.LocationManager.KEY_NAME;
import static com.example.fbuapp.models.Location.KEY_ADDRESS;
import static com.zipow.videobox.confapp.ConfMgr.getApplicationContext;

public class EditLocationFragment extends Fragment {

    FragmentEditSchoolBinding binding;

    Boolean location;
    ImageButton ibBack;
    TextView tvDone;
    TextView tvLocation;

    Location loc;
    School school;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String TAG = "EditSchoolFragment";


    public EditLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().getBoolean("location")) {
            location = true;
        } else {
            location = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditSchoolBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocationManager locationManager = new LocationManager();
        SchoolManager schoolManager = new SchoolManager();
        loc = new Location();
        school = new School();
        ibBack = binding.ibBack;
        tvDone = binding.tvDone;
        tvLocation = binding.tvLocation;

        // Initialize Places API
        Places.initialize(getContext(), getString(R.string.api_key));
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getContext());

        if (location) {
            locationManager.getUserLocation(tvLocation);
        } else {
            schoolManager.getSchoolName(((School) ParseUser.getCurrentUser().get(School.KEY_SCHOOL)).getObjectId(), tvLocation);
        }

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToProfile();
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlaceFinder(AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location) {
                    ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
                    query.whereEqualTo(KEY_ID, ((Location) ParseUser.getCurrentUser().get(KEY_LOCATION)).getObjectId());
                    query.include(KEY_NAME);
                    query.include(KEY_LOCATION);
                    query.include(KEY_ADDRESS);
                    query.getFirstInBackground(new GetCallback<Location>() {
                        @Override
                        public void done(Location l, ParseException e) {
                            if (l.getName().equals(tvLocation.getText().toString())) {
                                returnToProfile();
                            } else {
                                loc.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        ParseUser.getCurrentUser().put(KEY_LOCATION, loc);
                                        ParseUser.getCurrentUser().saveInBackground();
                                        returnToProfile();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    querySchool(tvLocation.getText().toString());
                    ParseQuery<School> query = ParseQuery.getQuery(School.class);
                    query.include(KEY_NAME);
                    query.include(KEY_LOCATION);
                    query.whereEqualTo(KEY_ID, ((School) ParseUser.getCurrentUser().get(KEY_SCHOOL)).getObjectId());
                    // Specify what other data we would like to get back
                    query.getFirstInBackground(new GetCallback<School>() {
                        @Override
                        public void done(School s, ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "Error fetching location");
                                return;
                            }
                            if (s.getName().equals(school.getName())) {
                                returnToProfile();
                            } else {
                                ParseUser.getCurrentUser().put(KEY_SCHOOL, school);
                                ParseUser.getCurrentUser().saveInBackground();
                                returnToProfile();
                            }
                        }
                    });
                }
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

    public void launchPlaceFinder(int autocompleteRequestCode) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getContext());
        startActivityForResult(intent, autocompleteRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                tvLocation.setText(place.getName());
                if (location) {
                    loc.setName(place.getName());
                    loc.setLocation(place.getLatLng());
                    loc.setAddress(place.getAddress());
                } else {
                    school.setLocation(place.getLatLng());
                    school.setName(place.getName());
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

    private void querySchool(String schoolName) {
        ParseQuery<School> query = ParseQuery.getQuery(School.class);
        // Specify what other data we would like to get back
        query.whereEqualTo(School.KEY_NAME, schoolName);
        query.getFirstInBackground(new GetCallback<School>() {
            @Override
            public void done(School object, ParseException e) {
                if (e == null) {
                    school = object;
                } else {
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        // Add new school to database
                        school.saveInBackground();
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }

}