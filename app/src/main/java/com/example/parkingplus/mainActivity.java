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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.database.FireBaseService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;

public class mainActivity extends FragmentActivity  {

    //debugging
    private static final String TAG = "MainActivity";
    //permissions
    private  boolean userPermissionGranted = false;
    //db
    private ServiceConnection mDBConnection;
    FireBaseService databaseClient;
    //fragments
    MapFragment mMapFragment;
    LocationInfo mLocationInf;

    private Button navbutton;
    //fragment manager
    FragmentManager fm;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //check permissions
        InitialChecks();
        //start db service
        createConnectToDatabaseService();
        doBindDBService();
        //set view
        setContentView(R.layout.main);
        //create map fragment
        mMapFragment = new MapFragment();
        mLocationInf = new LocationInfo();

        //set fragment
        fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout, mMapFragment);
        transaction.commit();

        navbutton = (Button) findViewById(R.id.navButton);
        navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.frameLayout, mLocationInf);
                transaction.commit();


            }
        });

        mViewPager = (ViewPager) findViewById(R.id.container2);

        setupViewPager(mViewPager);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mDBConnection);
    }

    // database service connection
    private void createConnectToDatabaseService() {

        mDBConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                FireBaseService.FireBaseBinder binder = (FireBaseService.FireBaseBinder) service;
                databaseClient = binder.getService();
                List<Location> databaseData = databaseClient.getLocations();
                mMapFragment.setParkingSpots(databaseData);
                mLocationInf.setLocationData(databaseData);

            }
            @Override
            public void onServiceDisconnected(ComponentName className) {
                databaseClient = null;
            }
        };
    }

    void doBindDBService() {
        if (bindService(new Intent(this, FireBaseService.class),
                mDBConnection, Context.BIND_AUTO_CREATE)) {
        } else {
            Log.e(TAG, "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }

    }

    // permissions
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
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "googleServiceVersionCheck: required services working ");
            return true;
        }
        if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "googleServiceVersionCheck: UserResolvableError occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,available,9001);
            dialog.show();
        } else{
            Log.d(TAG, "googleServiceVersionCheck: Google services version is incompatible");
            Toast.makeText(this,"Google services version is incompatible",Toast.LENGTH_LONG).show();
        }
        return false;

    }
    private void setupViewPager(ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

    }
    public void setViewPager(int fragmentNumber){
       mViewPager.setCurrentItem(fragmentNumber);
    }
}
