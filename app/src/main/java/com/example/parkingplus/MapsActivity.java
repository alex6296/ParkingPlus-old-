package com.example.parkingplus;

import android.Manifest;
import android.app.Dialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //debug
    private static final String TAG = "MainActivity";
    //permissions
    private String[] requiredUserPermissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private int locationPermissionRequestCode = 1000;
    private Boolean locationPermissionIsGranted = false;
    //services
    private FusedLocationProviderClient LocationProviderClient;
    //vars
    private GoogleMap mMap;
    private Location currentLocation;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!googleServicesAreCompatible()){
            return;
        }
        if(!locationPermissionIsGranted) {
         requestAccessToUserLocation();
            if(!locationPermissionIsGranted) {
                finish(); // if user denies access close app
            }
        }

        setContentView(R.layout.activity_maps); // sets view
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void requestAccessToUserLocation(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "getLocationUserPermission: user has given permission");
            this.locationPermissionIsGranted = true;
        }
        Log.d(TAG, "getLocationUserPermission: requesting user permission");
        ActivityCompat.requestPermissions(this, requiredUserPermissions, locationPermissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == locationPermissionRequestCode){ // check if the userPermissionsRequestCode matches the one returned
            int permissionGrantedCount = 0;
            for(int i = 0; i < grantResults.length; i++){ // loops all request grands
                   if(grantResults[i] != PackageManager.PERMISSION_GRANTED){ // checks if permission was given
                       // if 1 permission was denied set permission to false and stop
                       locationPermissionIsGranted =false;
                       break;
                   }
                   else{
                       permissionGrantedCount++;
                   }
               }
            if ( permissionGrantedCount ==  grantResults.length){ // if all permission was granted set  locationPermissionIsGranted to true
                locationPermissionIsGranted =true;
            }
        }
    }

    private boolean googleServicesAreCompatible(){
        Log.d(TAG, "googleServiceVersionCheck: checking google services availability ");
        int googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS){
            Log.d(TAG, "googleServiceVersionCheck: googlePlayServicesAvailable = true ");
            return true;
        }
        if (GoogleApiAvailability.getInstance().isUserResolvableError(googlePlayServicesAvailable)){
            Log.d(TAG, "googleServiceVersionCheck: UserResolvableError occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this,googlePlayServicesAvailable,9001);
            dialog.show();
            return googleServicesAreCompatible(); //retry and return the result of the retry
        }
        Log.d(TAG, "googleServiceVersionCheck: Google services version is incompatible");
        Toast.makeText(this,"Google services version is incompatible",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(this.locationPermissionIsGranted){
            retrieveCurrentLocation();
        }


        //TODO create a forloop that renders all the nearby locations

        //LatLng sydney = new LatLng(-34, 151);
       // LatLng sydney2 = new LatLng(-30, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in Sydney2"));
        //moveCameraTo(sydney);
    }

    private void retrieveCurrentLocation(){
        LocationProviderClient = LocationServices.getFusedLocationProviderClient(this); // device location service
        try{
            if(locationPermissionIsGranted){
                Task location = LocationProviderClient.getLastLocation(); //get last known location
                location.addOnCompleteListener(new OnCompleteListener() { //wait for responds
                            @Override
                    public void onComplete(@NonNull Task task) {

                        if (!task.isSuccessful()){ // if the request fails, notify and abort
                            Log.d(TAG,"retrieveCurrentLocation: no location data available");
                            Toast.makeText(MapsActivity.this,"no location data available",Toast.LENGTH_LONG);
                            return;
                        }

                        if (task.getResult() == null) {  // if the result is available empty request a update (when gps is turned of location is set to null)
                            deviceLocationUpdate(LocationProviderClient); // get location update
                            return;
                        }

                        // if a last location exists, add a marker and move the camera there
                        currentLocation = (Location) task.getResult();
                        moveCameraTo(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Test marker"));
                    }
                });
             }
        }catch (SecurityException e){
            Log.e(TAG,"retrieveCurrentLocation:SecurityException"+e.getMessage());
        }
    }

    private LocationRequest getLocationRequest(){
    //creating request settings
    LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); //10 sec
        locationRequest.setFastestInterval(5000); //5 sec
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // since we wanna show the way in the future high accuracy is needed.
    return locationRequest;
    }

    private void validateAndSendLocationRequest(final LocationRequest request, final FusedLocationProviderClient LocationProviderClient){
        //src = https://developer.android.com/training/location/change-location-settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(request); //build a builder and give it the request
        SettingsClient client = LocationServices.getSettingsClient(this); // check if settings are satisfied

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e(TAG,"validateAndSendLocationRequest: request was valid");
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                 sendLocationUpdateRequest(request,LocationProviderClient);

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"validateAndSendLocationRequest: request was invalid");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d(TAG, "validateLocationRequest: UserResolvableError occurred");
                    Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this,GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this),9002);
                    dialog.show();

                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,9002);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    private void sendLocationUpdateRequest(LocationRequest request,FusedLocationProviderClient LocationProviderClient) {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e(TAG,"sendLocationUpdateRequest: location still null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.e(TAG,"sendLocationUpdateRequest: location was = : "+location);
                    // Update UI with location data
                    // ...
                    currentLocation = location;
                }
            };
        };

        LocationProviderClient.requestLocationUpdates(request,
                locationCallback,
                Looper.getMainLooper());
    }

    private void deviceLocationUpdate(FusedLocationProviderClient LocationProviderClient){
        LocationRequest request = getLocationRequest(); // create request
        validateAndSendLocationRequest(request,LocationProviderClient); //validate request

    }

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
