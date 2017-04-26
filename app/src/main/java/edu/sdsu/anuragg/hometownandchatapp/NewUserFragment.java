package edu.sdsu.anuragg.hometownandchatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewUserFragment extends Fragment {
    private EditText mNickname, mPassword, mCity, mLatitude, mLongitude;
    Spinner countrySpinner, stateSpinner, yearSpinner;
    String selectedCountry, selectedState, selectedYear, nickname, password, city, selectedLat, selectedLon;
    public List<String> countries,states;
    public List<String> years = new ArrayList<>();
    ArrayAdapter<String> countryAdapter,stateAdapter,yearAdapter;
    DataAccessLayer dataAccessLayer;

    public NewUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);
        // Inflate the layout for this fragment

        dataAccessLayer = new DataAccessLayer();
        countries = new ArrayList<>();
        states = new ArrayList<>();
        states.add("Select State");

        years.add("Select Year");
        for (int i = 1970; i <= 2017; i++) {
            years.add(Integer.toString(i));
        }

        countries = dataAccessLayer.getCountriesList(getActivity().getBaseContext());
        countries.add(0, "Select Country");


        mNickname = (EditText) view.findViewById(R.id.nicknametextid);
        mPassword = (EditText) view.findViewById(R.id.passwordtextid);
        mCity = (EditText) view.findViewById(R.id.citytextid);
        mLatitude = (EditText) view.findViewById(R.id.lattextid);
        mLongitude = (EditText) view.findViewById(R.id.lontextid);
        mNickname.setSelection(mNickname.getText().length());
        mNickname.requestFocus();
        mPassword.setSelection(mPassword.getText().length());
        mCity.setSelection(mCity.getText().length());
        mLatitude.setSelection(mLatitude.getText().length());
        mLongitude.setSelection(mLongitude.getText().length());


        countrySpinner = (Spinner) view.findViewById(R.id.countrylist_spinner);
        stateSpinner = (Spinner) view.findViewById(R.id.statelist_spinner);
        yearSpinner = (Spinner) view.findViewById(R.id.year_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        countryAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, countries);
        stateAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, states);
        yearAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, years);
// Specify the layout to use when the list of choices appears
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        countrySpinner.setAdapter(countryAdapter);
        stateSpinner.setAdapter(stateAdapter);
        yearSpinner.setAdapter(yearAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedCountry = countries.get(position);
                if(position!=0) {
                    Log.i("selected Country", selectedCountry);
                    states = dataAccessLayer.getStatesList(getActivity().getBaseContext(),selectedCountry);
                    states.add(0,"Select State");
                    stateAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_spinner_item,states);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    stateSpinner.setAdapter(stateAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCountry = null;
            }
        });

        if(selectedCountry == "Select Country"){
            selectedCountry= null;
        }
        if(selectedState == "Select State"){
            selectedState = null;
        }

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedState = states.get(position);
                if(position!=0) {
                    Log.i("selected State", selectedState);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedState = null;
            }
        });

        if(selectedYear == "Select Year"){
            selectedYear = null;
        }

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedYear = years.get(position);
                if(position!=0) {
                    Log.i("selected State", selectedYear);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = null;
            }
        });



        return view;
    }



    public interface UserInformation{
        public void getUserInfo(String nickname, String password, String country, String state, String city, String year);
    }

}
