package com.example.parkingplus;
import android.Manifest;
import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.example.database.FireBaseService;


import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

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
    //db
    private ServiceConnection mDBConnection;
    FireBaseService databaseClient;
    private Boolean mShouldUnbind;
    //button
    private Button goToLocation;
    private ViewPager mViewPager;

    //map
    MapView mMapView;


    //lifecycle overrides

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        createLocationCallBackObject();
        startLocationUpdates();
        createConnectToDatabaseService();
        doBindDBService();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // database service connetion

    private void createConnectToDatabaseService() {

        mDBConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                FireBaseService.FireBaseBinder binder = (FireBaseService.FireBaseBinder) service;
                databaseClient = binder.getService();
                setParkingSpots(databaseClient.getLocations());

            }
            @Override
            public void onServiceDisconnected(ComponentName className) {
                databaseClient = null;
            }
        };
    }

    void doBindDBService() {
        if (getActivity().bindService(new Intent(getActivity(), FireBaseService.class),
                mDBConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Log.e(TAG, "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    void doUnbindService() {
        if (mShouldUnbind) {
            getActivity().unbindService(mDBConnection);
            mShouldUnbind = false;
        }
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
        mMap.setMyLocationEnabled(true);

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

    private void moveCameraTo(LatLng coordinates){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates,10f));
    }

    private void moveCameraTo(LatLng coordinates, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates,zoom));
    }

    private void setupViewPager(ViewPager viewPager){
        MapsFragment mapactivity = new MapsFragment();

        FragmentAdapter adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());

        adapter.addFragment(new LocationInfo(),"LocationFragment");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);

    }

}
