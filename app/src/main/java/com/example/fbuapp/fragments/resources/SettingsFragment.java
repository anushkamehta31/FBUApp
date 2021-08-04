package com.example.fbuapp.fragments.resources;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentNotesBinding;
import com.example.fbuapp.databinding.FragmentSettingsBinding;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.managers.ResourceManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.GroupMappings;
import com.example.fbuapp.models.School;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.hootsuite.nachos.NachoTextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static com.example.fbuapp.fragments.resources.ImagesFragment.PICK_PHOTO_CODE;

public class SettingsFragment extends Fragment {

    public Group group;
    FragmentSettingsBinding binding;

    // Get references to views
    ExtendedFloatingActionButton btnSave;
    NachoTextView nUsers;
    Chip chipTimes;
    ChipGroup cgMeetingTimes;
    Chip chipDays;
    ChipGroup cgMeetingDays;
    TextView tvLocation;
    EditText etDescription;
    EditText etName;
    TextView tvEditGroupPhoto;
    ImageView ivGroupImage;

    // Resource manager to perform queries
    ResourceManager resourceManager;

    // Get references to each current setting of the group
    File photoFile;
    public String photoFileName = "photo.jpg";
    Calendar combinedCal;
    private Map<String, ParseUser> users;
    private List<String> usernames;

    // Tag for class
    public static final String TAG = "SettingsFragment";

    public SettingsFragment() {
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate resource manager
        resourceManager = new ResourceManager();
        combinedCal = Calendar.getInstance();

        // Instantiate views
        btnSave = binding.efSave;
        nUsers = binding.nUsers;
        chipTimes = binding.chipTimes;
        chipDays = binding.chipDays;
        cgMeetingDays = binding.cgMeetingDays;
        cgMeetingTimes = binding.cgMeetingTimes;
        tvLocation = binding.tvLoc;
        etDescription = binding.etDescription;
        etName = binding.etName;
        tvEditGroupPhoto = binding.tvEditGroupPhoto;
        ivGroupImage = binding.ivGroupImage;

        // Load current group settings into views
        if (group.getImage() != null) {
            Glide.with(getContext()).load(group.getImage().getUrl()).into(ivGroupImage);
        }
        etName.setText(group.getName());
        etDescription.setText(group.getDescription());


        // Instantiate views to change settings
        tvEditGroupPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });

        // Use time picker if user wants to select times
        chipTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });

        // Use date picker if the user wants to select a meeting day
        chipDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        combinedCal.setTimeInMillis(group.getTimeStamp()*1000L);
        usernames = new ArrayList<>();
        users = new HashMap<String, ParseUser>();
        initializeFindUsers(view);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGroupSettings();
                saveGroupMappings();
            }
        });
    }

    private void saveGroupSettings() {
        group.setName(etName.getText().toString());
        group.setDescription(etDescription.getText().toString());
        long timestamp = (combinedCal.getTimeInMillis() / 1000L);
        group.setTimeStamp(timestamp);
        if (cgMeetingDays.getChildCount() != 0) {
            group.setMeetingDay(((Chip) cgMeetingDays.getChildAt(0)).getText().toString());
        }

        if (cgMeetingTimes.getChildCount() != 0) {
            group.setMeetingTime(((Chip) cgMeetingTimes.getChildAt(0)).getText().toString());
        }
        group.saveInBackground();
    }

    private void saveGroupMappings() {
        // Create mappings and save
        GroupMappingsManager mappingsManager = new GroupMappingsManager();
        try {
            mappingsManager.setSettingsMappings(users, nUsers, group);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
    }

    public void launchGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileproviderx", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getRealPathFromURIForGallery(selectedImageUri);
            File imageFile = new File(selectedImagePath);
            group.setImage(new ParseFile(imageFile));
            if (imageFile != null) {
                Glide.with(getContext()).load(imageFile).into(ivGroupImage);
            }
        }
    }

    public String getRealPathFromURIForGallery(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null,
                null, null);
        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        assert false;
        cursor.close();
        return uri.getPath();
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
                combinedCal.setTime(new Date(selection + offsetFromUTC));
                // Set the timezone to the default timezone the system is running on
                combinedCal.setTimeZone(TimeZone.getDefault());
                // Get day of week
                String dayOfWeek = simpledateformat.format(date);
                // Create a new chip and add it to the chip group
                Chip chip = new Chip(getContext());
                chip.setText(dayOfWeek);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cgMeetingDays.removeView(chip);
                    }
                });
                // We only want to allow the user to select on time
                cgMeetingDays.removeAllViews();
                cgMeetingDays.addView(chip);
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
                combinedCal.set(Calendar.HOUR_OF_DAY, hour);
                combinedCal.set(Calendar.MINUTE, picker.getMinute());
                combinedCal.set(Calendar.SECOND, 0);
                combinedCal.set(Calendar.MILLISECOND, 0);
                long timestamp = combinedCal.getTimeInMillis() / 1000L;
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
                        cgMeetingTimes.removeView(chip);
                    }
                });
                // We only want to allow the user to select on time
                cgMeetingTimes.removeAllViews();
                cgMeetingTimes.addView(chip);

            }
        });
    }

    private void initializeFindUsers(View view) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        List<ParseUser> groupUsers = new ArrayList<>();
        query.include("objectId");
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

                    // Make sure no mapping exists between the user and the group already
                    ParseQuery<GroupMappings> mapQuery = ParseQuery.getQuery(GroupMappings.class);
                    mapQuery.include("objectId");
                    mapQuery.include(GroupMappings.KEY_GROUP_ID);
                    mapQuery.include(GroupMappings.KEY_USER_ID);
                    mapQuery.whereEqualTo(GroupMappings.KEY_USER_ID, user);
                    mapQuery.whereEqualTo(GroupMappings.KEY_GROUP_ID, group);
                    mapQuery.getFirstInBackground(new GetCallback<GroupMappings>() {
                        @Override
                        public void done(GroupMappings mapping, ParseException e) {
                            if (e == null) {
                                return;
                            } else {
                                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                    // Add new user to list
                                    users.put(username, user);
                                    usernames.add(username);
                                } else {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
                    });
                }

                // Create add users textview
                nUsers = view.findViewById(R.id.nUsers);
                ArrayAdapter<String> usersAdapter =
                        new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, usernames);
                nUsers.setAdapter(usersAdapter);
            }
        });
    }

}