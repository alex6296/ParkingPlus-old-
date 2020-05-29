package com.example.parkingplus;


import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingplus.data.ILocationListObserver;

import java.util.List;

public class ListFragment extends Fragment implements ILocationListObserver {
    private RecyclerView mCycleView;
    private LocationAdapter mLocationAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private java.util.List<Location> mLocations;

    public void updateLocationListData(List<Location> locationList){
        mLocations = locationList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_list, container, false);

        //get list
        mCycleView = view.findViewById(R.id.locationsList);
        mCycleView.setHasFixedSize(true);

        //define layout
        mLayoutManager = new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL, false);
        mCycleView.setLayoutManager(mLayoutManager);

        //define adapter
        mLocationAdapter = new LocationAdapter();
        mLocationAdapter.setLocationData(mLocations);
        mCycleView.setAdapter(mLocationAdapter);

        return view;
    }

    private static final String TAG = "ListFragment";




}
