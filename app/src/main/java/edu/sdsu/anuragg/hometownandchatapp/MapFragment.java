package edu.sdsu.anuragg.hometownandchatapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener{
    private MapView mapView;
    private GoogleMap googleMap;
    private String selectedCountry,selectedState,selectedCity;
    private String selectedLat, selectedLon;
    private int zoomLevel=6;
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        selectedCountry = getArguments().getString("Country");
        selectedState = getArguments().getString("State");
        if(getArguments().getString("City")!=null) {
            selectedCity = getArguments().getString("City");
        }
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Button setLatLon = (Button) view.findViewById(R.id.setLocationId);
        setLatLon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedLat!=null&&selectedLon!=null) {
                    LayoutInflater lf = getActivity().getLayoutInflater();
                    /*NewUserFragment newUserFragment = new NewUserFragment();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_user_helper, newUserFragment)
                            .commit();*/

                    Log.i("IN LAT LON",selectedLat + " " + selectedLon);
                    View v2 = lf.inflate(R.layout.fragment_new_user,container);
                    EditText mLatitude = (EditText)v2.findViewById(R.id.lattextid);
                    mLatitude.setText(selectedLat);
                    EditText mLongitude = (EditText)v2.findViewById(R.id.lontextid);
                    mLongitude.setText(selectedLon);
                    Toast.makeText(getActivity(),"For Changing Country/State, Please Press Reset Button",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Please Place Marker",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.i("IN activity country",selectedCountry!=null?selectedCountry:"NULL");
        Log.i("IN activity state",selectedState!=null?selectedState:"NULL");
        Log.i("IN activity city",selectedCity!=null?selectedCity:"NULL");

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mapView = (MapView) v.findViewById(R.id.newMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        double latitude = 0.0;
        double longitude = 0.0;
        Geocoder location = new Geocoder(getActivity().getBaseContext());

        try {
            List<Address> state = location.getFromLocationName(selectedCity+","+selectedState+","+selectedCountry,3);
            for (Address stateLocation: state) {
                if (stateLocation.hasLatitude())
                    latitude = stateLocation.getLatitude();
                if (stateLocation.hasLongitude())
                    longitude = stateLocation.getLongitude();
            }
        } catch (Exception error) {
            Log.e("rew", "Address lookup Error", error);
        }

        if(latitude==0 || longitude==0) {
            try {
                List<Address> state = location.getFromLocationName(selectedState + "," + selectedCountry, 3);
                for (Address stateLocation : state) {
                    if (stateLocation.hasLatitude())
                        latitude = stateLocation.getLatitude();
                    if (stateLocation.hasLongitude())
                        longitude = stateLocation.getLongitude();
                }
            } catch (Exception error) {
                Log.e("rew", "Address lookup Error", error);
            }
        }
        Log.d("Inside New Map view", latitude + " " + longitude);
        LatLng stateLatLng = new LatLng(latitude, longitude);
        CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(stateLatLng, zoomLevel);
        googleMap.moveCamera(newLocation);
    }

    public void onMapClick(LatLng location) {
        Log.i("rew", "new Location " + location.latitude + " longitude " + location.longitude);
        LatLng selectedLocation = new LatLng(location.latitude, location.longitude);
        selectedLat = String.valueOf(location.latitude);
        selectedLon = String.valueOf(location.longitude);
        //((UserHelperActivity)getActivity()).getLatLon(selectedLat,selectedLon);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Marker in Position"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));
    }

    //Defining the interface which is implemented by the UserHelper Activity class
    public interface SelectedLocation{
        public void getLatLon(String latitude, String longitude);
    }
}
