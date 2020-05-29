package com.example.parkingplus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.parkingplus.data.FireBaseService;
import com.example.parkingplus.utilities.PermissionUtil;

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
    ListFragment mLocationList;
    //fragment manager
    FragmentManager fm;

    //util
    PermissionUtil permissionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //check permissions

        //start db service
        createConnectToDatabaseService();
        doBindDBService();
        //set view
        setContentView(R.layout.main);
        //create map fragment
        mMapFragment = new MapFragment();
        mLocationList = new ListFragment();

        //set fragment
        fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout, mMapFragment);
        transaction.commit();

        Button navbutton = (Button) findViewById(R.id.navButton);
        navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment active_fragment  = fm.findFragmentById(R.id.frameLayout);

                if(active_fragment.getId() == mMapFragment.getId()){
                    final FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frameLayout, mLocationList);
                    transaction.commit();
                }else{
                    final FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frameLayout, mMapFragment);
                    transaction.commit();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionUtil = new PermissionUtil(this);
        permissionUtil.permissionAndGoogleServiceChecks();
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

                databaseClient.addSubscribe(mMapFragment);
                databaseClient.addSubscribe(mLocationList);
                databaseClient.updateLocationData();
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






}
