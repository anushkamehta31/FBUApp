package com.example.fbuapp.fragments.profileFragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbuapp.LoginActivity;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;

import com.example.fbuapp.databinding.FragmentProfileBinding;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.managers.SchoolManager;
import com.example.fbuapp.models.School;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.fbuapp.Constants.KEY_USER_PROFILE;
import static com.example.fbuapp.fragments.resources.ImagesFragment.PICK_PHOTO_CODE;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    // References to all views
    private Button btnLogout;
    private CircleImageView ivProfileImage;
    private TextView tvChangeProfile;
    private TextView tvUsername;
    private TextView tvSchool;
    public TextView tvLocation;
    private SchoolManager schoolManager;
    private LocationManager locationManager;
    private ImageButton ibEditUsername;
    private ImageButton ibEditLocation;
    private ImageButton ibEditSchool;
    File photoFile;
    public String photoFileName = "photo.jpg";

    public static final String TAG = "ProfileFragment";
    public static final String KEY_PROFILE = "profileImage";


    public ProfileFragment() {
        // Required empty public constructor
    }

    // Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here. E.g, view lookups and attaching view listeners
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Instantiate view references
        btnLogout = binding.btnLogout;
        ivProfileImage = binding.ivProfileImage;
        tvChangeProfile = binding.tvChangeProfileImage;
        tvUsername = binding.tvUserName;
        tvSchool = binding.tvSchool;
        tvLocation = binding.tvLocation;
        ibEditLocation = binding.editLocation;
        ibEditSchool = binding.editSchool;
        ibEditUsername = binding.editUsername;

        // Instantiate managers
        schoolManager = new SchoolManager();
        locationManager = new LocationManager();

        // Load user's profile image into the screen
        ParseFile image = ParseUser.getCurrentUser().getParseFile(KEY_USER_PROFILE);
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl()).fitCenter().centerCrop().into(ivProfileImage);
        } else {
            Glide.with(getContext()).load(R.drawable.nopfp).fitCenter().centerCrop().into(ivProfileImage);
        }

        // Load current data into views
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        locationManager.getUserLocation(tvLocation);
        schoolManager.getSchoolName(((School) ParseUser.getCurrentUser().get(School.KEY_SCHOOL)).getObjectId(), tvSchool);

        // Set click listener on logout button to return to log in screen
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

        // Set on click listeners to edit user settings
        tvChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });

        ibEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditLocation(true);
            }
        });

        ibEditSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditLocation(false);
            }
        });

        ibEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditUsername();
            }
        });

    }

    private void goEditUsername() {
        MainActivity activity = (MainActivity) getContext();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        EditUsernameFragment fragment = new EditUsernameFragment();
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
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
            ParseUser.getCurrentUser().put(KEY_PROFILE, new ParseFile(imageFile));
            ParseUser.getCurrentUser().saveInBackground();
            if (imageFile != null) {
                Glide.with(getContext()).load(imageFile).into(ivProfileImage);
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

    public void goEditLocation(boolean location) {
        MainActivity activity = (MainActivity) getContext();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        EditLocationFragment fragment = new EditLocationFragment();
        bundle.putBoolean("location", location);
        fragment.setArguments(bundle);
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }
}