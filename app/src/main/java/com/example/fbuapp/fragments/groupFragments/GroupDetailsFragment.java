package com.example.fbuapp.fragments.groupFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.models.Group;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class GroupDetailsFragment extends Fragment {

    private Group group;
    public static final String TAG = "GroupDetailsFragment";
    Button btnType;
    ImageView ivImage;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;


    public GroupDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO: Switch to view binding
        return inflater.inflate(R.layout.fragment_group_details, container, false);
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
        btnType = view.findViewById(R.id.btnVirtual);
        ivImage = view.findViewById(R.id.ivImage);

        // Set up navigation drawer for resources
        drawerLayout = view.findViewById(R.id.drawer);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
        toggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.bringToFront();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        toggle.syncState();
        navigationView = view.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.images:
                        break;
                    case R.id.notes:
                        break;
                    case R.id.videos:
                        break;
                    case R.id.agenda:
                        break;
                    case R.id.links:
                        break;
                    case R.id.chat:
                        break;
                    case R.id.settings:
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });


        if (!group.isVirtual()) btnType.setText(R.string.in_person);
        if (group.getImage() != null) {
            Glide.with(getContext()).load(group.getImage().getUrl()).into(ivImage);
        }


        view.findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinMeeting(getContext(), group.getMeetingID(), group.getPassword());
            }
        });

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