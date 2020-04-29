package com.example.database;

import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;


public class FireBaseService {

    List<Location> locations = new ArrayList<Location>();

    public FireBaseService(){
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

    public List<Location> getLocations(){
        return locations;
    }
}
