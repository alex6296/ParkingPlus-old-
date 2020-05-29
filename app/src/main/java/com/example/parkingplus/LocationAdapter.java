package com.example.parkingplus;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationAdapterViewHolder> {

    //vars
    private List<Location> mLocations;

    public LocationAdapter() {

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class LocationAdapterViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public final TextView mParkingSpotTextView ;

        public LocationAdapterViewHolder(View v) {
            super(v);
            mParkingSpotTextView = v.findViewById(R.id.parkingSpotData);
        }
    }



    // Create new views (invoked by the layout manager)
    @Override
    public LocationAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_list_item, parent, false);

        return new LocationAdapterViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LocationAdapterViewHolder holder, int position) {
        // get parking spot
        Location spot = mLocations.get(position);

        //create display string
        String displayText = "lat:"+spot.getLatitude()+"\nlong: "+spot.getLongitude() +"\n";

        //set display string on view
        holder.mParkingSpotTextView.setText(displayText);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public void setLocationData(List<Location> locations){
        this.mLocations = locations;
        notifyDataSetChanged();
    }
}