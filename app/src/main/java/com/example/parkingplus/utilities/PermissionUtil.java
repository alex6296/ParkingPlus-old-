package com.example.parkingplus.utilities;

import android.Manifest;
import android.app.Dialog;
import android.content.ServiceConnection;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.parkingplus.ListFragment;
import com.example.parkingplus.MapFragment;
import com.example.parkingplus.data.FireBaseService;
import com.example.parkingplus.mainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

public class PermissionUtil {


    //debugging
    private static final String TAG = "MainActivity";
    //permissions
    private  boolean userPermissionGranted = false;
    //activity
    mainActivity Activity;

    public PermissionUtil(mainActivity mainActivity) {
        Activity = mainActivity;
    }

    public boolean wasAllPermissionGranted(){
        return userPermissionGranted;
    }


    // permissions
    public void permissionAndGoogleServiceChecks(){
        //Checking if google services is working
        if(!googleServicesWorks()){
            Log.d(TAG, "googleServiceVersionCheck:failed ");
            Activity.finish();
        }
        //Checking if permissions are granted
        requestPermissions();
        if (!userPermissionGranted){
            Log.d(TAG, "userPermissions: failed ");
            Activity.finish();
        }
    }

    private boolean googleServicesWorks(){
        Log.d(TAG, "googleServiceVersionCheck: checking validity service version ");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Activity);

        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "googleServiceVersionCheck: required services working ");
            return true;
        }
        if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "googleServiceVersionCheck: UserResolvableError occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Activity,available,9001);
            dialog.show();
        } else{
            Log.d(TAG, "googleServiceVersionCheck: Google services version is incompatible");
            Toast.makeText(Activity,"Google services version is incompatible",Toast.LENGTH_LONG).show();
        }
        return false;

    }

    private void requestPermissions() {

        Dexter.withActivity(Activity)
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
            @Override public void onPermissionRationaleShouldBeShown(java.util.List permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();


    }
}
