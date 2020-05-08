package com.example.parkingplus;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LocationInfo extends Fragment {
    private Button goBackBtn;
    private Button test;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.locationinf, container, false);

        goBackBtn = (Button) view.findViewById(R.id.goBackBtn);



        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapsActivity)getActivity()).setViewPager(0);



            }
        });
        return view;
    }

    private static final String TAG = "LocationInfo";


}
