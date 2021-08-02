package com.example.fbuapp.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.fbuapp.MainActivity;
import com.example.fbuapp.R;
import com.example.fbuapp.adapters.MapPotentialGroupsAdapter;
import com.example.fbuapp.databinding.FragmentDirectionsBinding;
import com.example.fbuapp.fragments.searchFragments.SearchGroupFragment;
import com.example.fbuapp.fragments.groupFragments.GroupDetailsFragment;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.models.Group;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DirectionsFragment extends Fragment implements OnMapReadyCallback {


    FragmentDirectionsBinding binding;
    GoogleMap mGoogleMap;
    List<Group> potentialGroups;
    Group group;
    ImageButton ibClose;
    private GeoApiContext mGeoApiContext;
    public static final String TAG = "DirectionsFragment";

    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    LocationManager locationManager;
    ParseGeoPoint userPosition;
    ParseGeoPoint groupLocation;

    public ViewPager viewPager;
    public MapPotentialGroupsAdapter adapter;
    public List<Group> potentialInPersonGroups;

    public List<Marker> allMarkers;

    public DirectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().getParcelable("itemGroup") != null) {
                group = (Group) getArguments().getParcelable("itemGroup");
            }
            if (getArguments().getParcelableArrayList("potentialGroups") != null) {
                potentialGroups = getArguments().getParcelableArrayList("potentialGroups");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDirectionsBinding.inflate(inflater, container, false);
        initGoogleMap(savedInstanceState);
        locationManager = new LocationManager();
        userPosition = locationManager.getUserLocation();
        if (group != null) {
            groupLocation = locationManager.getGroupLocation(group);
        }

        potentialInPersonGroups = new ArrayList<>();
        allMarkers = new ArrayList<>();
        adapter = new MapPotentialGroupsAdapter(potentialInPersonGroups, getContext());
        viewPager = binding.viewPagerPotential;
        viewPager.setAdapter(adapter);
        viewPager.setPadding(15, 0, 200, 0);

        ibClose = binding.ibClose;
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (potentialGroups != null) {
                    goSearchFragment();
                } else if (group != null) {
                    goDetailsFragment();
                }
            }
        });
        return binding.getRoot();
    }

    private void goDetailsFragment() {
        MainActivity activity = (MainActivity) getContext();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        GroupDetailsFragment detailsFragment = new GroupDetailsFragment();
        bundle.putParcelable("itemGroup", group);
        detailsFragment.setArguments(bundle);
        ft.replace(R.id.flContainer, detailsFragment);
        ft.commit();
    }

    private void goSearchFragment() {
        MainActivity activity = (MainActivity) getContext();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        SearchGroupFragment fragment = new SearchGroupFragment();
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }



    private void initGoogleMap(Bundle savedInstanceState) {

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) binding.mapView;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (group != null) {
            calculateDirections();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(groupLocation.getLatitude(), groupLocation.getLongitude()))
                    .title("Marker"));

            googleMap.addCircle(new CircleOptions().center(new LatLng(userPosition.getLatitude(), userPosition.getLongitude())).radius(10)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.BLUE));

            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(userPosition.getLatitude(), userPosition.getLongitude()), 13);
            googleMap.animateCamera(cameraUpdate);
        }

        if (potentialGroups != null) {
            // Set up the sliding viewpager and the markers
            initSearchMap();
        }

    }

    private void initSearchMap() {
        // Create markers for every group that is in person
        // Add each group that is in person to the view pager
        int i = 0;
        for (Group potentialGroup : potentialGroups) {
            if (!potentialGroup.isVirtual()) {
                ParseGeoPoint curGroupLocation = locationManager.getGroupLocation(potentialGroup);
                Marker groupMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(curGroupLocation.getLatitude(),
                        curGroupLocation.getLongitude())).title(potentialGroup.getName()));
                potentialInPersonGroups.add(potentialGroup);
                adapter.notifyDataSetChanged();
                allMarkers.add(i, groupMarker);
                groupMarker.setTag(i);
                i++;
            }
        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                int position = (int) marker.getTag();
                viewPager.setCurrentItem(position, true);
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Marker marker = allMarkers.get(position);
                CameraUpdate cameraUpdate = CameraUpdateFactory
                        .newLatLngZoom(marker.getPosition(), 13);
                mGoogleMap.animateCamera(cameraUpdate);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Start the camera at the first group position
        Marker marker = allMarkers.get(0);
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(marker.getPosition(), 10);
        mGoogleMap.animateCamera(cameraUpdate);

    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // Method to calculate directions
    private void calculateDirections(){
        Log.d(TAG, "calculateDirections: calculating directions.");



        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                groupLocation.getLatitude(),
                groupLocation.getLongitude()
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);


        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        userPosition.getLatitude(),
                        userPosition.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    // Method to draw the lines between
    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg" + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.filter));
                    polyline.setClickable(true);
                }
            }
        });
    }

}