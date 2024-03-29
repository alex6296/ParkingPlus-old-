package com.example.parkingplus;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.parkingplus.data.ILocationListObserver;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, ILocationListObserver {

    //debugging
    private static final String TAG = "MainActivity";
    //parking spots
    private Location mCurrentLocation; //SET WITH SetCurrentLocation(Location location)
    private List<Location> mLocationList= new ArrayList<>();
    //map
    private GoogleMap mMap;
    //location
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest = getDefaultLocationRequest();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //start location services
        createLocationCallBackObject();
        startLocationUpdates();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Gets the MapView from the XML layout and creates it
        final SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        myMAPF.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if(mCurrentLocation != null){
            moveCameraTo(mCurrentLocation);
        }
        if(mLocationList != null)
            setMarkers();
    }

    //location related
    public void updateLocationListData(List<Location> locationList){
        mLocationList = locationList;
        setMarkers();
    }

    private void setMarkers(){
        for (Location l :mLocationList){
            mMap.addMarker(new MarkerOptions().position(toLatLng(l)).title("a free parking spot"));
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
                mCurrentLocation = locationResult.getLastLocation(); // update location

                moveCameraTo(mCurrentLocation);
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
