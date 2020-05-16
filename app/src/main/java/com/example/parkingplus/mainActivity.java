package com.example.parkingplus;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    private FragmentAdapter mSectionsStatePagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitialChecks();
        setContentView(R.layout.main);

        Fragment map = new MapFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout, map);
        transaction.commit();

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

    // permissions
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
}
