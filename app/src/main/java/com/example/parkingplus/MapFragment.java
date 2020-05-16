package com.example.parkingplus;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.database.FireBaseService;


import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    //debugging
    private static final String TAG = "MainActivity";
    //parking spots
    private List<Location> parkingSpots;
    private Location mCurrentLocation; //SET WITH SetCurrentLocation(Location location)
    private Boolean firstLocation = true; //
    //map
    private GoogleMap mMap;
    //location
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true; // toggles location updates
    private LocationRequest locationRequest = getDefaultLocationRequest();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map, container, false);
        // Gets the MapView from the XML layout and creates it
        final SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        myMAPF.getMapAsync(this);
        return  view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        createLocationCallBackObject();
        startLocationUpdates();
    }

    //location related
    private void setParkingSpots(List<Location> locations){
        parkingSpots = locations;
        for (Location l : parkingSpots){
            mMap.addMarker(new MarkerOptions().position(toLatLng(l)).title("a free parking spot"));
        }
    }

    private void SetCurrentLocation(Location location){
        mCurrentLocation = location;
        if (firstLocation == true) {
            moveCameraTo(mCurrentLocation);
            firstLocation = false;
        }
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void createLocationCallBackObject() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "LocationCallBack : onLocationResult: location == null");
                    return;
                }
                    SetCurrentLocation(locationResult.getLastLocation()); // update location
            }
        };
    }

    private LocationRequest getDefaultLocationRequest(){
        //creating request settings
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); //10 sec
        locationRequest.setFastestInterval(5000); //5 sec
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // since we wanna show the way in the future high accuracy is needed.
        return locationRequest;
    }

    private LatLng toLatLng(Location location){
       return new LatLng(location.getLatitude(), location.getLongitude());
    }

    //camera
    private void moveCameraTo(Location coordinates){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()),10f));
    }

}