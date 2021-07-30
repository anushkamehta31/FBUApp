package com.example.fbuapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbuapp.R;
import com.example.fbuapp.databinding.FragmentDirectionsBinding;
import com.example.fbuapp.databinding.FragmentGroupDetailsBinding;
import com.example.fbuapp.models.Group;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DirectionsFragment extends Fragment implements OnMapReadyCallback {


    FragmentDirectionsBinding binding;
    List<Group> groups;
    Group group;

    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    public DirectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getParcelableArrayList("group_list") != null) {
                groups = getArguments().getParcelableArrayList("group_list");
            } else {
                group = group = (Group) getArguments().getParcelable("itemGroup");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDirectionsBinding.inflate(inflater, container, false);
        initGoogleMap(savedInstanceState);
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
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
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
}