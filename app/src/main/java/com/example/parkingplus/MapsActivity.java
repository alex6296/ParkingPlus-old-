package com.example.parkingplus;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //debug
    private static final String TAG = "MainActivity";
    //permissions
    private int userPermissionRequestCode = 1;
    private Boolean userPermissionGranted = false;
    //services
    private FusedLocationProviderClient LocationProviderClient;
    //vars
    private GoogleMap mMap;
    private Location currentLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Checking if google services is working
        if(googleServicesWorks()){
            if(!userPermissionGranted) {
             getLocationUserPermission();

            setContentView(R.layout.activity_maps);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            }
        }
    }


    private void getLocationUserPermission(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "getLocationUserPermission: user has given permission ");
            this.userPermissionGranted = true;
        }
        Log.d(TAG, "getLocationUserPermission: requesting user permission ");
        String[] userPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, userPermissions,userPermissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == userPermissionRequestCode){ // matches our request send to get permission
               for(int i = 0; i < grantResults.length; i++){ // loops all request grands
                   if(grantResults[i] != PackageManager.PERMISSION_GRANTED){ // checks if permission was given
                       // if 1 permission was denied set permission to false and stop
                       userPermissionGranted =false;
                       break;
                   }
                   else{ // if all permission was granted set userPermission to true
                       userPermissionGranted =true;
                   }
               }
        }
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
        }
        Log.d(TAG, "googleServiceVersionCheck: Google services version is incompatible");
        Toast.makeText(this,"Google services version is incompatible",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        retrieveCurrentLocation();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void retrieveCurrentLocation(){
        LocationProviderClient = LocationServices.getFusedLocationProviderClient(this); // device location service
    try{
        if(userPermissionGranted){ // if access to location data granted
            Task location = LocationProviderClient.getLastLocation(); //get last known location
            location.addOnCompleteListener(new OnCompleteListener() { //wait for responds
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){ // if responds is successful
                        if (task.getResult() != null) { // if a last location does exists
                            currentLocation = (Location) task.getResult(); // set variable
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 18f));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Test marker"));
                        }else{ // if no last location exists request a new one
                            LocationRequest locationRequest = LocationRequest.create();
                            //todo location updates
                            Task location = LocationProviderClient.requestLocationUpdates(LocationProviderClient,3000,).;
                        }
                    }

                    }
                    else{

                        Log.d(TAG,"getCurrentLocation: no location data available");
                        Toast.makeText(MapsActivity.this,"no location data available",Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }catch (SecurityException e){
        Log.e(TAG,"retrieveCurrentLocation:SecurityException"+e.getMessage());
    }
    }

    private void moveCameraWithAutoZoomTo(LatLng coordinates){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates,18f));
    }

}
