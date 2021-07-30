package com.example.fbuapp.fragments.groupFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.GroupMemberAdapter;
import com.example.fbuapp.databinding.FragmentGroupDetailsBinding;
import com.example.fbuapp.databinding.FragmentImagesBinding;
import com.example.fbuapp.fragments.resources.AgendaFragment;
import com.example.fbuapp.fragments.resources.ChatFragment;
import com.example.fbuapp.fragments.resources.FilesFragment;
import com.example.fbuapp.fragments.resources.ImagesFragment;
import com.example.fbuapp.fragments.resources.LinksFragment;
import com.example.fbuapp.fragments.resources.NotesFragment;
import com.example.fbuapp.fragments.resources.SettingsFragment;
import com.example.fbuapp.fragments.resources.VideosFragment;
import com.example.fbuapp.managers.GroupMappingsManager;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.managers.SchoolManager;
import com.example.fbuapp.managers.ZoomManager;
import com.example.fbuapp.models.Group;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class GroupDetailsFragment extends Fragment {

    private Group group;
    public static final String TAG = "GroupDetailsFragment";
    public TextView tvLocation;
    public ImageButton ibMap;
    TextView tvDay;
    TextView tvDescription;
    TextView tvTitle;
    TextView tvTime;
    TextView tvSchool;
    ImageView ivImage;
    MaterialButton btnJoinMeeting;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    ChipGroup chipGroup;
    RecyclerView rvMembers;
    FragmentGroupDetailsBinding binding;
    public GroupMemberAdapter adapter;
    public List<ParseUser> groupMembers;
    ZoomManager zoomManager;


    public GroupDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGroupDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve arguments passed
        group = (Group) getArguments().getParcelable("itemGroup");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Call this method to initialize the SDK
        initializeSdk(getContext());

        zoomManager = new ZoomManager();
        ivImage = binding.ivImage;
        btnJoinMeeting = binding.btnJoinMeeting;
        ibMap = binding.ibMap;
        tvDescription = binding.tvDescription;
        tvLocation = binding.tvLocation;
        tvTitle = binding.tvName;
        tvSchool = binding.tvSchoolName;
        // Set up navigation drawer for resources
        drawerLayout = binding.drawer;
        chipGroup = binding.chipGroupTopics;
        tvDay = binding.tvDay;
        tvTime = binding.tvTime;
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        rvMembers = binding.rvMembers;
        drawerLayout.bringToFront();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        toggle.syncState();
        navigationView = binding.navigationView;

        zoomManager.initializeSdk(getContext());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putParcelable("itemGroup", group);
                switch(id){
                    case R.id.images:
                        fragment = new ImagesFragment();
                        break;
                    case R.id.notes:
                        fragment = new NotesFragment();
                        break;
                    case R.id.videos:
                        fragment = new VideosFragment();
                        break;
                    case R.id.agenda:
                        fragment = new AgendaFragment();
                        break;
                    case R.id.links:
                        fragment = new LinksFragment();
                        break;
                    case R.id.files:
                        fragment = new FilesFragment();
                        break;
                    case R.id.chat:
                        fragment = new ChatFragment();
                        break;
                    case R.id.settings:
                        fragment = new SettingsFragment();
                        break;
                    default:
                        return true;
                }
                loadFragment(fragment, bundle);
                return true;
            }
        });

        tvDescription.setText(group.getDescription());
        tvTitle.setText(group.getName());

        if (group.isVirtual()) {
            tvLocation.setText("Zoom Meeting");
            btnJoinMeeting.setVisibility(View.VISIBLE);
        } else {
            LocationManager locationManager = new LocationManager();
            locationManager.getSchoolFromGroup(tvLocation, group.getLocation().getObjectId());
            ibMap.setVisibility(View.VISIBLE);
            // TODO: set an onclick listener that opens map and gives directions if the user selects it
        }

        SchoolManager schoolManager = new SchoolManager();
        schoolManager.getSchoolName(group.getSchool().getObjectId(), tvSchool);

        tvDay.setText(group.getMeetingDays());
        tvTime.setText(group.getMeetingTime());


        if (group.getImage() != null) {
            Glide.with(getContext()).load(group.getImage().getUrl()).into(ivImage);
        }


        btnJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomManager.joinMeeting(getContext(), group.getMeetingID(), group.getPassword());
            }
        });

        ArrayList<String> topics = group.getTopics();
        for (String topic : topics) {
            Chip chip = new Chip(getContext());
            chip.setText(topic);
            chipGroup.addView(chip);
        }

        // Get group members
        rvMembers.bringToFront();
        GroupMappingsManager groupManager = new GroupMappingsManager();
        groupMembers = new ArrayList<>();
        adapter = new GroupMemberAdapter(getContext(), groupMembers);
        rvMembers.setAdapter(adapter);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        groupManager.getGroupMembers(adapter, groupMembers, group);
    }

    private void loadFragment(Fragment fragment, Bundle bundle) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        ft.replace(R.id.frame, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        ft.addToBackStack(null);
    }

    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // Fr the purpose of this demo app, we are storing the credentials in the client app itself.
        //  However, you should not use hard-coded values for your key/secret in your app in production.
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = getString(R.string.zoom_key);
        params.appSecret = getString(R.string.zoom_secret_key);
        params.domain = getString(R.string.zoom);
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) { }

            @Override
            public void onZoomAuthIdentityExpired() { }
        };
        sdk.initialize(context, listener, params);
    }

    //1. Write the joinMeeting function.
    private void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = ParseUser.getCurrentUser().getUsername();
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }


}