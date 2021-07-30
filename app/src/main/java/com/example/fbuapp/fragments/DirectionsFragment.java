package com.example.fbuapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentDirectionsBinding;
import com.example.fbuapp.databinding.FragmentGroupDetailsBinding;
import com.example.fbuapp.managers.LocationManager;
import com.example.fbuapp.models.Group;
import com.example.fbuapp.models.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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
    List<Group> groups;
    Group group;
    private GeoApiContext mGeoApiContext;
    public static final String TAG = "DirectionsFragment";

    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    LocationManager locationManager;
    ParseGeoPoint userPosition;
    ParseGeoPoint groupLocation;

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
        groupLocation = locationManager.getGroupLocation(group);
        return binding.getRoot();
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
                    .position(new LatLng(userPosition.getLatitude(), userPosition.getLongitude()))
                    .title("Marker"));
        }

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