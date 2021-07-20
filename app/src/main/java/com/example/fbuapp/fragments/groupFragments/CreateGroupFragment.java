package com.example.fbuapp.fragments.groupFragments;

import android.content.Context;
import android.content.Intent;
import org.json.JSONObject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentCreateGroupBinding;

import com.example.fbuapp.managers.GroupManager;
import com.example.fbuapp.managers.ZoomManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.Location;
import com.example.fbuapp.models.School;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.http.ParseHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import org.json.JSONObject;


public class CreateGroupFragment extends DialogFragment {

    private Map<String, ParseUser> users;
    private List<String> usernames;
    private NachoTextView acTopics;
    private NachoTextView nUsers;
    private NachoTextView nAdditionalTopics;
    private Chip cVirtual;
    private Chip cInPerson;
    private Chip cDays;
    private Chip cTimes;
    private ChipGroup chipGroupTimes;
    private ChipGroup chipGroupDay;
    private TextView tvLocation;
    private TextView tvSchool;
    private ImageView ivProfile;
    private ImageButton ibClose;
    private TextView tvDescription;
    private EditText groupName;
    private TextView tvUsername;
    private ExtendedFloatingActionButton efCreate;
    public static final String TAG = "CreateGroupFragment";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE_SCHOOL = 2;
    Location meetingLocation;
    School school;
    CreateGroupFragment dialogFragment;
    FragmentCreateGroupBinding binding;
    public Group group;
    StringBuffer meetingID;
    StringBuffer password ;

    public CreateGroupFragment() {
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
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to all views
        acTopics = binding.nTopics;
        tvLocation = binding.tvLoc;
        nAdditionalTopics = binding.nAdditionalTopics;
        tvSchool = binding.tvSchool;
        cVirtual = binding.chipVirtual;
        cInPerson = binding.chipInPerson;
        ibClose = binding.ibClose;
        ivProfile = binding.ivProfile;
        tvUsername = binding.tvUserName;
        cDays = binding.chipDays;
        cTimes = binding.chipTimes;
        cDays = binding.chipDays;
        chipGroupTimes = binding.cgMeetingTimes;
        chipGroupDay = binding.cgMeetingDays;
        efCreate = binding.efCreate;
        groupName = binding.tvName;
        tvDescription = binding.tvDescription;
        usernames = new ArrayList<>();
        meetingID = new StringBuffer(getString(R.string.empty_string));
        password = new StringBuffer(getString(R.string.empty_string));
        users = new HashMap<String, ParseUser>();
        MainActivity activity = (MainActivity) getContext();
        dialogFragment = (CreateGroupFragment) activity.getSupportFragmentManager().findFragmentByTag("fragment_create_group");
        specifyDialogParameters(view);
        initializeTopics();
        initializeGroupLocation();
        initializePlaces();
        initializeFindUsers(view);

        // Set a newline chip terminator for additional/untagged topics
        nAdditionalTopics.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);

        // Set a click listener on the closer button
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
            }
        });

        // Add user's profile image
        ParseFile image = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl()).fitCenter()
                    .transform(new RoundedCornersTransformation(30,10)).centerCrop().into(ivProfile);
        } else {
            Glide.with(getContext()).load(R.drawable.nopfp).fitCenter()
                    .transform(new RoundedCornersTransformation(40,30)).centerCrop().into(ivProfile);
        }

        // Display current user's username
        tvUsername.setText("@"+ParseUser.getCurrentUser().getUsername());

        // Use time picker if user wants to select times
        cTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
        
        // Use date picker if the user wants to select a meeting day
        cDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Set on click listener for closing the create group fragment
        efCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateGroup();
            }
        });

    }

    private void initiateGroup() {
        // Check that all fields are filled out
        if (!cVirtual.isChecked() && !cInPerson.isChecked()) {
            Toast.makeText(getContext(),
                    "Group type not chosen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (groupName.getText().toString().equals("") || school == null || (cInPerson.isChecked() && meetingLocation == null)) {
            Toast.makeText(getContext(),
                    "Missing fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (chipGroupDay.getChildCount() == 0 || chipGroupTimes.getChildCount() == 0) {
            Toast.makeText(getContext(),
                    "Missing date or time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (acTopics.getAllChips().size() < 3) {
            Toast.makeText(getContext(),
                    "Missing topics", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> topics = new ArrayList<>();
        for (com.hootsuite.nachos.chip.Chip chip : acTopics.getAllChips()) {
            topics.add(chip.getText().toString());
        }
        for (com.hootsuite.nachos.chip.Chip chip : nAdditionalTopics.getAllChips()) {
            topics.add(chip.getText().toString());
        }
        if (cVirtual.isChecked()) {
            try {
                // If the group is virtual, first generate the room and then create the group
                ZoomManager zoomManager = new ZoomManager();
                zoomManager.generateZoomRoom(getContext(), cVirtual.isChecked(), groupName.getText().toString(), school, meetingLocation,
                        tvDescription.getText().toString(), ((Chip) chipGroupDay.getChildAt(0)).getText().toString(),
                        ((Chip) chipGroupTimes.getChildAt(0)).getText().toString(), topics, users, nUsers, getTargetFragment());
                // generateZoomRoom();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            group = new Group();
            // If the group is in person, simply create the group
            try {
                GroupManager groupManager = new GroupManager();
                groupManager.createGroup(group, cVirtual.isChecked(), groupName.getText().toString(), school, meetingLocation,
                        tvDescription.getText().toString(), ((Chip) chipGroupDay.getChildAt(0)).getText().toString(),
                        ((Chip) chipGroupTimes.getChildAt(0)).getText().toString(), topics, users, nUsers, getTargetFragment(), getContext());
                // createGroup();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDatePicker() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a meeting date");
        MaterialDatePicker materialDatePicker = builder.build();
        materialDatePicker.show(getChildFragmentManager(), TAG);
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // user has selected a date
                // format the date and set the text of the input box to be the selected date
                // Get the offset from our timezone and UTC.
                TimeZone timeZoneUTC = TimeZone.getDefault();
                // It will be negative, so that's the -1
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                // Create a date format, then a date object with our offset
                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = new Date(selection + offsetFromUTC);
                // Get day of week
                String dayOfWeek = simpledateformat.format(date);
                // Create a new chip and add it to the chip group
                Chip chip = new Chip(getContext());
                chip.setText(dayOfWeek);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chipGroupDay.removeView(chip);
                    }
                });
                // We only want to allow the user to select on time
                chipGroupDay.removeAllViews();
                chipGroupDay.addView(chip);
            }
        });
    }

    /** Method that opens the Materials Time picker and allows user to choose a time */
    private void openTimePicker() {
        int clockFormat = TimeFormat.CLOCK_12H;
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat).setHour(12).setMinute(0).setTitleText("Select Meeting Times").build();
        picker.show(getChildFragmentManager(), TAG);
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = picker.getHour();
                String am_pm = (hour < 12) ? "AM" : "PM";
                if (hour > 12) hour -= 12;
                int minute = picker.getMinute();
                String chipText = (minute < 10) ? hour + ":0" + minute + " " + am_pm : hour + ":" + minute + " " + am_pm;
                // Create a new chip and add it to the chip group
                Chip chip = new Chip(getContext());
                chip.setText(chipText);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chipGroupTimes.removeView(chip);
                    }
                });
                // We only want to allow the user to select on time
                chipGroupTimes.removeAllViews();
                chipGroupTimes.addView(chip);

            }
        });
    }


    /** Creates a query to find all users and allows current user to search for users to add to the group */
    private void initializeFindUsers(View view) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        List<ParseUser> groupUsers = new ArrayList<>();
        query.include("objectID");
        query.include("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> allUsers, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for (ParseUser parseUser : allUsers) {
                    ParseUser user = (ParseUser) parseUser;
                    String username = user.getUsername();
                    users.put(username, user);
                    usernames.add(username);
                }
                // Create add users textview
                nUsers = view.findViewById(R.id.nUsers);
                ArrayAdapter<String> usersAdapter =
                        new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, usernames);
                nUsers.setAdapter(usersAdapter);
            }
        });
    }

    /** Determines whether a location field should be displayed depending on whether or not the
    group is in person */
    private void initializeGroupLocation() {
        // Create the tvLocation reference variable and set it to Gone initially
        tvLocation.setVisibility(View.GONE);
        // If the In-Person tag is checked, we want to display the Location View
        cInPerson.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvLocation.setVisibility(View.VISIBLE);
            }
        });
        // If the In-Person tag is checked, we want to display the Location View
        cVirtual.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvLocation.setVisibility(View.GONE);
            }
        });
    }

    /**  Instantiates dialog params */
    private void specifyDialogParameters(View view) {
        view.setNestedScrollingEnabled(true);

        // Specify parameters for the window
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.98);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.98);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setLayout(width, height);
    }

    /** Retrieves a list of study topics for users to choose from */
    private void initializeTopics() {
        // First text view for predefined topics
        String[] topics = getResources().getStringArray(R.array.topics_array);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, topics);
        acTopics.setAdapter(adapter);
    }

    /** Initializes places API for user to search for places */
    private void initializePlaces() {
        Places.initialize(getContext(), getString(R.string.api_key));
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
    }

    /** Calls the Places API so that user can search */
    private void launchPlaceFinder(int autocompleteRequestCode) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getContext());
        startActivityForResult(intent, autocompleteRequestCode);
    }

    /** Binds results of user search to data that can be used later on */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE ||
                requestCode == AUTOCOMPLETE_REQUEST_CODE_SCHOOL) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                    tvLocation.setText(place.getName());
                    meetingLocation = new Location();
                    meetingLocation.setName(place.getName());
                    meetingLocation.setLocation(place.getLatLng());
                    meetingLocation.setAddress(place.getAddress());
                } else {
                    school = new School();
                    school.setName(place.getName());
                    school.setLocation(place.getLatLng());
                    tvSchool.setText(place.getName());
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
    }

}