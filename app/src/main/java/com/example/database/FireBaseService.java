package com.example.database;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FireBaseService extends Service {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    DatabaseReference pusher;
    DatabaseReference retriever;
    List<Location> locations = new ArrayList<Location>();
    private final IBinder mBinder = new FireBaseBinder();
    double longitude;
    double lattitude;

    public class FireBaseBinder extends Binder {
        public FireBaseService getService(){
            return FireBaseService.this;
        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(FireBaseService.this, "Firebase Connection successfull", Toast.LENGTH_LONG).show();
        pusher = FirebaseDatabase.getInstance().getReference().child("Location");

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
    public void pushToFirebase(Location location){
        pusher.push().setValue(location);
    }

    public Location retrieveFromFirebase(String idnumber){
        retriever = FirebaseDatabase.getInstance().getReference().child(idnumber);
        retriever.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               lattitude = (double) dataSnapshot.child("lattitude").getValue();
               longitude = (double) dataSnapshot.child("longitude").getValue();
                //possible issue here with the casting of doubles, since firebase seems eager to only collect objects
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Location dLocation = new Location(LocationManager.GPS_PROVIDER);
        dLocation.setLongitude(longitude);
        dLocation.setLatitude(lattitude);
        return dLocation;



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
