package com.example.parkingplus.data;

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

    //vars
    List<Location> locations = new ArrayList<Location>();

    //firebase
    DatabaseReference retriever;
    private final IBinder mFireBaseBinder = new FireBaseBinder();

    private List<ILocationListObserver> subscribers =  new ArrayList<>() ;

    public class FireBaseBinder extends Binder {

        public FireBaseService getService(){
            return FireBaseService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mFireBaseBinder;
    }

    public interface DataStatus{
        void DataIsLoaded(List<Location> locations, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    @Override
    public void onCreate() {
        retriever = FirebaseDatabase.getInstance().getReference("Location");
        Toast.makeText(FireBaseService.this, "Firebase Connection successful", Toast.LENGTH_LONG).show();

    }

    public void addSubscribe(ILocationListObserver subscriber){
        subscribers.add(subscriber);
    }

    public void removeSubscribe(ILocationListObserver subscriber){
        subscribers.remove(subscriber);
    }

    public void updateLocationData(){
         retriever.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear old data
                    locations.clear();

                    List<String> keys = new ArrayList<>();

                    for(DataSnapshot keyNode: dataSnapshot.getChildren()){

                        keys.add(keyNode.getKey());
                        tempLongitude location = keyNode.getValue(tempLongitude.class);

                        Location l1 = new Location(LocationManager.GPS_PROVIDER);
                        l1.setLatitude(location.getLatitude());
                        l1.setLongitude(location.getLongitude());
                        System.out.println(l1);
                        locations.add(l1);
                    }

                for(ILocationListObserver subscriber: subscribers){
                    subscriber.updateLocationListData(locations);
                }
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
}
