package edu.sdsu.anuragg.hometownandchatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindUserMapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    private String selectedCountry,selectedState,selectedCity;
    private int zoomLevel=5;
    public ArrayList<UserDataModel> userList;

    public FindUserMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        userList = (ArrayList<UserDataModel>) args.getSerializable("userList");
        selectedCountry = getArguments().getString("selectedCountry");
        selectedState = getArguments().getString("selectedState");
        selectedCity = getArguments().getString("selectedCity");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_user_map, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mapView = (MapView) v.findViewById(R.id.findUserMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        double actualLatitude = 0.0,actualLongitude = 0.0, latitude,longitude;
        Geocoder geocoder = new Geocoder(getActivity().getBaseContext());
        int count = 0;
        for(int i = 0;i<userList.size();i++) {
            Log.d("Inside Map view", userList.get(i).latitude.toString() + " " + userList.get(i).longitude.toString());
            latitude = userList.get(i).latitude;
            longitude = userList.get(i).longitude;
            if (latitude == 0 || longitude == 0) {
                List<Address> state;
                try {
                    if(selectedCountry!=null){
                        if(selectedState!=null){
                            state = geocoder.getFromLocationName(userList.get(i).state + "," + userList.get(i).country, 3);
                            for (Address stateLocation : state) {
                                if (stateLocation.hasLatitude())
                                    actualLatitude = stateLocation.getLatitude();
                                if (stateLocation.hasLongitude())
                                    actualLongitude = stateLocation.getLongitude();
                            }
                        }
                        else{
                            state = geocoder.getFromLocationName(userList.get(i).country, 3);
                            for (Address stateLocation : state) {
                                if (stateLocation.hasLatitude())
                                    actualLatitude = stateLocation.getLatitude();
                                if (stateLocation.hasLongitude())
                                    actualLongitude = stateLocation.getLongitude();
                            }
                        }
                    }
                    else{
                        state = geocoder.getFromLocationName(userList.get(i).state + "," + userList.get(i).country, 3);
                        for (Address stateLocation : state) {
                            if (stateLocation.hasLatitude())
                                actualLatitude = stateLocation.getLatitude();
                            if (stateLocation.hasLongitude())
                                actualLongitude = stateLocation.getLongitude();
                        }
                    }
                } catch (Exception error) {
                    Log.e("rew", "Address lookup Error", error);
                }
            }
            else {
                List<Address> state;
                if(selectedCountry!=null){
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 5);
                        if (selectedState != null) {
                            for (Address addr : addressList) {
                                boolean isCountryPresent = false;
                                boolean isStatePresent = false;
                                int index = 0;
                                while (addr.getAddressLine(index) != null) {
                                    String tempAddr = addr.getAddressLine(index);
                                    Log.i("rew", "line " + index + " = " + tempAddr);
                                    if (tempAddr.contains(userList.get(i).country)) {
                                        isCountryPresent = true;
                                    }
                                    if (tempAddr.contains(userList.get(i).state)) {
                                        isStatePresent = true;
                                    }
                                    index++;
                                }

                                if(isCountryPresent||isStatePresent){
                                    actualLatitude = latitude;
                                    actualLongitude = longitude;
                                }
                                else{
                                    state = geocoder.getFromLocationName(userList.get(i).state + "," + userList.get(i).country, 3);
                                    for (Address stateLocation : state) {
                                        if (stateLocation.hasLatitude())
                                            actualLatitude = stateLocation.getLatitude();
                                        if (stateLocation.hasLongitude())
                                            actualLongitude = stateLocation.getLongitude();
                                    }
                                }
                            }
                        }
                        else {
                            for (Address addr : addressList) {
                                boolean isCountryPresent = false;
                                boolean isStatePresent = false;
                                int index = 0;
                                while (addr.getAddressLine(index) != null) {
                                    String tempAddr = addr.getAddressLine(index);
                                    Log.i("rew", "line " + index + " = " + tempAddr);
                                    if (tempAddr.contains(userList.get(i).country)) {
                                        isCountryPresent = true;
                                    }
                                    if (tempAddr.contains(userList.get(i).state)) {
                                        isStatePresent = true;
                                    }
                                    index++;
                                }
                                if(isCountryPresent||isStatePresent){
                                    actualLatitude = latitude;
                                    actualLongitude = longitude;
                                }
                                else{
                                    state = geocoder.getFromLocationName(userList.get(i).state + "," + userList.get(i).country, 3);
                                    for (Address stateLocation : state) {
                                        if (stateLocation.hasLatitude())
                                            actualLatitude = stateLocation.getLatitude();
                                        if (stateLocation.hasLongitude())
                                            actualLongitude = stateLocation.getLongitude();
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception error) {
                        Log.e("rew", "Address lookup Error", error);
                    }
                }
                else{
                    actualLatitude = latitude;
                    actualLongitude = longitude;
                }
            }
            LatLng location = new LatLng(actualLatitude, actualLongitude);
            googleMap.addMarker(new MarkerOptions().position(location).title(userList.get(i).nickname));
            count++;

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        Log.i("Plot Count",Integer.toString(count));

    }

}
