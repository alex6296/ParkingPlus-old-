package com.example.parkingplus;


import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationInfo extends Fragment {
    private Button goBackBtn;
    private RecyclerView locationsList;
    private Button test;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Location> locationDataSet;

    public  void setLocationData(List<Location> databaseData) {
        locationDataSet = databaseData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.locationinf, container, false);

        goBackBtn = (Button) view.findViewById(R.id.goBackBtn);
        locationsList = (RecyclerView) view.findViewById(R.id.locationsList);
        locationsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        locationsList.setLayoutManager(layoutManager);

        mAdapter = new LocationAdapter(locationDataSet);
        locationsList.setAdapter(mAdapter);



        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ((mainActivity)getActivity()).setViewPager(0);



            }
        });
        return view;
    }

    private static final String TAG = "LocationInfo";




}
