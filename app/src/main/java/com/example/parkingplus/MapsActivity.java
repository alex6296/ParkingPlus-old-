package com.example.parkingplus;
import android.Manifest;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.example.database.FireBaseService;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //debugging
    private static final String TAG = "MainActivity";

    //vars
    private  boolean userPermissionGranted = false;
    //location
    private Location mCurrentLocation; //SET WITH SetCurrentLocation(Location location)
    private Boolean firstLocation = true; //

    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true; // toggles location updates
    private LocationRequest locationRequest = getDefaultLocationRequest();
    //elements
    private GoogleMap mMap;
    //clients
    private FusedLocationProviderClient fusedLocationClient;
    FireBaseService databaseClient;

    private void SetCurrentLocation(Location location){
        mCurrentLocation = location;
        mMap.setMyLocationEnabled(true);

        if (firstLocation == true) {
            moveCameraTo(mCurrentLocation);
            firstLocation = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitialChecks(); // check google service --v, user-permissions etc. //terminates app if fails

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallBackObject();
        startLocationUpdates();

        databaseClient = new FireBaseService();

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    private void InitialChecks(){
        //Checking if google services is working
        if(!googleServicesWorks()){
            Log.d(TAG, "googleServiceVersionCheck:failed ");
            finish();
        }
        //Checking if permissions are granted
        requestPermissions();
        if (!userPermissionGranted){
            Log.d(TAG, "userPermissions: failed ");
            finish();
        }
    }

    private void requestPermissions() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                        // if more permissions need add here
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                if(report.areAllPermissionsGranted()){
                    userPermissionGranted = true;
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();


    }

    private boolean googleServicesWorks(){
        Log.d(TAG, "googleServiceVersionCheck: checking validity service version ");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "googleServiceVersionCheck: required services working ");
            return true;
        }
        if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "googleServiceVersionCheck: UserResolvableError occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this,available,9001);
            dialog.show();
        } else{
            Log.d(TAG, "googleServiceVersionCheck: Google services version is incompatible");
            Toast.makeText(this,"Google services version is incompatible",Toast.LENGTH_LONG).show();
        }
        return false;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Location l : databaseClient.getLocations()){
            mMap.addMarker(new MarkerOptions().position(toLatLng(l)).title("a free parking spot"));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    //location related

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

}
