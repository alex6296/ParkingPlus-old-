package com.example.database;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;


public class FireBaseService extends Service {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    List<Location> locations = new ArrayList<Location>();
    private final IBinder mBinder = new FireBaseBinder();

    myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String value = dataSnapshot.getValue(String.class);
            Log.d(TAG, "Value is: " + value);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }


    public class FireBaseBinder extends Binder {
        public FireBaseService getService(){
            return FireBaseService.this;
        }
    }

    @Override
    public void onCreate() {
        Location l1 = new Location(LocationManager.GPS_PROVIDER);
        l1.setLatitude(37.4219617);
        l1.setLongitude(-122.092);

        Location l2 = new Location(LocationManager.GPS_PROVIDER);
        l2.setLatitude(37.3275338);
        l2.setLongitude(-122.101);

        Location l3 = new Location(LocationManager.GPS_PROVIDER);
        l3.setLatitude(37.4719520);
        l3.setLongitude(-122.121);

        locations.add(l1);
        locations.add(l2);
        locations.add(l3);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FireBaseService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "FireBaseService stopped", Toast.LENGTH_SHORT).show();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public List<Location> getLocations(){
        return locations;
    }
}
