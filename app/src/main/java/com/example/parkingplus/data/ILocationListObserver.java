package com.example.parkingplus.data;

import android.location.Location;

import java.util.List;

public interface ILocationListObserver {

    public void updateLocationListData(List<Location> locationList);
}
