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

import com.google.android.gms.maps.model.LatLng;
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

    public class FireBaseBinder extends Binder {
        public FireBaseService getService(){
            return FireBaseService.this;
        }
    }

    public interface DataStatus{
        void DataIsLoaded(List<Location> locations, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    @Override
    public void onCreate() {
        Toast.makeText(FireBaseService.this, "Firebase Connection successfull", Toast.LENGTH_LONG).show();
        pusher = FirebaseDatabase.getInstance().getReference().child("Location");
        retrieveFromFirebase();


    }
    public void pushToFirebase(Location location){
        pusher.push().setValue(location);
    }

    public void retrieveFromFirebase(){
        retriever = FirebaseDatabase.getInstance().getReference("Location");
        pusher.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    locations.clear();
                    List<String> keys = new ArrayList<>();
                    for(DataSnapshot keyNode: dataSnapshot.getChildren()){
                        keys.add(keyNode.getKey());
                        tempLongitude location = new tempLongitude();
                        location = keyNode.getValue(tempLongitude.class);
                        Location l1 = new Location(LocationManager.GPS_PROVIDER);
                        l1.setLatitude(location.getLattitude());
                        l1.setLongitude(location.getLongitude());
                        System.out.println(l1);
                        locations.add(l1);

                }

               //lattitude = (double) dataSnapshot.child("lattitude").getValue();
               //longitude = (double) dataSnapshot.child("longitude").getValue();
                //possible issue here with the casting of doubles, since firebase seems eager to only collect objects
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




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
