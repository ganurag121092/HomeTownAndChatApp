package edu.sdsu.anuragg.hometownandchatapp;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserMapFragment extends Fragment implements OnMapReadyCallback{
    private MapView mapView;
    private GoogleMap googleMap;
    private int zoomLevel=5;
    public ArrayList<User> chatUserList;
    String currentUser,currentUID,selectedUsername,selectedUserUid;
    UserChatFragment userChatFragment;
    public ChatUserMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        currentUser = args.getString("currentUser");
        currentUID = args.getString("currentUserUID");
        chatUserList = (ArrayList<User>) args.getSerializable("ChatUserList");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_user_map, container, false);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(userChatFragment!=null){
            getFragmentManager().beginTransaction().remove(userChatFragment).commitAllowingStateLoss();}
    }

    @Override
    public void onStop(){
        super.onStop();
        if(userChatFragment!=null){
            getFragmentManager().beginTransaction().remove(userChatFragment).commitAllowingStateLoss();}
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mapView = (MapView) v.findViewById(R.id.chat_users_map);
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
        for(int i = 0;i<chatUserList.size();i++) {
            Log.d("Inside Map view", chatUserList.get(i).latitude.toString() + " " + chatUserList.get(i).longitude.toString());
            latitude = chatUserList.get(i).latitude;
            longitude = chatUserList.get(i).longitude;

                    actualLatitude = latitude;
                    actualLongitude = longitude;

            LatLng location = new LatLng(actualLatitude, actualLongitude);

            googleMap.addMarker(new MarkerOptions().position(location).title(chatUserList.get(i).userName+"_"+chatUserList.get(i).uid));
            count++;
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String[] title = marker.getTitle().split("_");
                    selectedUsername = title[0];
                    selectedUserUid = title[1];
                    /*Toast.makeText(getActivity(),
                            "Marker Clicked: " + selectedUsername, Toast.LENGTH_LONG)
                            .show();*/

                    Bundle bundle = new Bundle();
                    bundle.putString("currentUser", currentUser);
                    bundle.putString("selectedUser",selectedUsername);
                    bundle.putString("currentUserUID", currentUID);
                    bundle.putString("selectedUserUID",selectedUserUid);

                    userChatFragment = new UserChatFragment();
                    userChatFragment.setArguments(bundle);
                    if(userChatFragment!=null){
                        getFragmentManager().beginTransaction().remove(userChatFragment).commitAllowingStateLoss();
                    }
                    getFragmentManager().beginTransaction().add(R.id.activity_logged_user,userChatFragment).commit();
                    return false;
                }
            });

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        Log.i("Plot Count",Integer.toString(count));
    }
}
