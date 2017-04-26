package edu.sdsu.anuragg.hometownandchatapp;

import android.app.ProgressDialog;
import android.content.Intent;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import java.util.List;

import android.support.annotation.NonNull;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.sdsu.anuragg.hometownandchatapp.database.UserDatabaseHelper;

public class LoggedUserActivity extends AppCompatActivity {

    String loginName;
    private BottomNavigationView bottomNavigation;
    Spinner countrySpinner, stateSpinner, yearSpinner;
    static String selectedCountry, selectedState, selectedYear, city;
    public List<String> countries;
    public List<String> states;
    public List<String> years = new ArrayList<>();
    private ProgressDialog progressDialog;
    ArrayAdapter<String> countryAdapter,stateAdapter,yearAdapter;
    DataAccessLayer dataAccessLayer;
    public static ArrayList<UserDataModel> userList;
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    FindUserMapFragment findUserMapFragment;
    UserChatFragment userChatFragment;
    UserChatListFragment userChatListFragment;
    LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    DatabaseReference databaseUsers;
    DatabaseReference databaseChatrooms;
    ChatUserMapFragment chatUserMapFragment;

    UserDatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        databaseHelper = new UserDatabaseHelper(this);
        if(userChatListFragment!=null){
            getSupportFragmentManager().beginTransaction().remove(userChatListFragment).commitAllowingStateLoss();}
        if(chatUserMapFragment!=null){
            getSupportFragmentManager().beginTransaction().remove(chatUserMapFragment).commitAllowingStateLoss();}

        Intent intent = getIntent();
        loginName = intent.getStringExtra("Username");
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseChatrooms = FirebaseDatabase.getInstance().getReference("chatrooms");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        bottomNavigation = (BottomNavigationView)findViewById(R.id.navigation);

        dataAccessLayer = new DataAccessLayer();

        Log.i("IN Useractivity state",selectedState!=null?selectedState:"NULL");
        Log.i("IN Useractivity city",city!=null?city:"NULL");
        countries = new ArrayList<>();
        states = new ArrayList<>();

        years.add("Select Year");
        for(int i=1970;i<=2017;i++){
            years.add(Integer.toString(i));
        }

        countries = dataAccessLayer.getCountriesList(this);
        countries.add(0,"Select Country");

        states.add("Select State");

        countrySpinner = (Spinner) this.findViewById(R.id.countrylist_spinner);
        stateSpinner = (Spinner) this.findViewById(R.id.statelist_spinner);
        yearSpinner = (Spinner) this.findViewById(R.id.year_spinner);

// Create an ArrayAdapter using the string array and a default spinner layout
        countryAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,countries);
        stateAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,states);
        yearAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,years);

// Specify the layout to use when the list of choices appears
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        countrySpinner.setAdapter(countryAdapter);
        stateSpinner.setAdapter(stateAdapter);
        yearSpinner.setAdapter(yearAdapter);

        fetchLocalRecords(null,null,null);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getBaseContext(), "Please Press Bottom Navigation For Viewing List/Map", Toast.LENGTH_SHORT).show();
                selectedYear = years.get(position);
                if(position!=0) {
                    Log.i("selected Year", selectedYear);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getBaseContext(), "Please Press Bottom Navigation For Viewing List/Map", Toast.LENGTH_SHORT).show();
                selectedCountry = countries.get(position);
                if(selectedCountry=="Select Country"){
                    states = new ArrayList<String>();
                }
                if(position!=0) {
                    Log.i("selected Country", selectedCountry);
                    states = dataAccessLayer.getStatesList(getBaseContext(), selectedCountry);
                }
                    states.add(0,"Select State");
                    stateAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_item,states);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    stateSpinner.setAdapter(stateAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getBaseContext(), "Please Press Bottom Navigation For Viewing List/Map", Toast.LENGTH_SHORT).show();
                selectedState = states.get(position);
                if(position!=0) {
                    Log.i("selected State", selectedState);
                    if(selectedState.contains(" ")){
                        String[] s = selectedState.split(" ");

                        selectedState = "";
                        for(int i=0;i<s.length;i++){
                            if(i==0){
                                selectedState = s[i];
                            }else {
                                selectedState = selectedState + "%20" + s[i];
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_list:

                        if(userChatListFragment!=null){
                            getSupportFragmentManager().beginTransaction().remove(userChatListFragment).commitAllowingStateLoss();}
                        if(chatUserMapFragment!=null){
                            getSupportFragmentManager().beginTransaction().remove(chatUserMapFragment).commitAllowingStateLoss();}
                        if(userChatFragment!=null){
                            getSupportFragmentManager().beginTransaction().remove(userChatFragment).commitAllowingStateLoss();}

                        countrySpinner.setVisibility(View.VISIBLE);
                        stateSpinner.setVisibility(View.VISIBLE);
                        yearSpinner.setVisibility(View.VISIBLE);
                        if(selectedCountry=="Select Country"){
                            selectedCountry = null;
                        }
                        if(selectedYear=="Select Year"){
                            selectedYear = null;
                        }
                        if(selectedState=="Select State"){
                            selectedState = null;
                        }
                        if(selectedCountry!=null) {
                            if(selectedState!=null) {
                                if (selectedCountry != null && selectedState != null && selectedYear != null) {
                                    //url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                                    fetchLocalRecords(selectedCountry,selectedState,selectedYear);
                                } else if (selectedCountry != null && selectedState != null) {
                                    //url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&page=0&reverse=true";
                                    fetchLocalRecords(selectedCountry,selectedState,null);
                                }
                            }
                            else{
                                if(selectedYear!=null) {
                                    //url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                                    fetchLocalRecords(selectedCountry,null,selectedYear);
                                }
                                else {
                                    fetchLocalRecords(selectedCountry,null,null);
                                }
                            }
                        }
                        else{
                            if(selectedYear!=null){
                                fetchLocalRecords(null,null,selectedYear);
                            }
                            else{
                                fetchLocalRecords(null,null,null);
                            }
                        }
                        return true;
                    case R.id.menu_chat:
                        countrySpinner.setVisibility(View.INVISIBLE);
                        stateSpinner.setVisibility(View.INVISIBLE);
                        yearSpinner.setVisibility(View.INVISIBLE);
                        if(recyclerView!=null)
                        {
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        if(findUserMapFragment!=null){
                        getSupportFragmentManager().beginTransaction().remove(findUserMapFragment).commit();}

                        //userChatFragment = new UserChatFragment();
                        userChatListFragment = new UserChatListFragment();

                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            //Toast.makeText(getBaseContext(), "Current user is " + user.getEmail()+" "+user.getUid(), Toast.LENGTH_LONG).show();

                            Bundle bundle = new Bundle();
                            bundle.putString("currentUser", email.substring(0,email.length()-10));
                            bundle.putString("currentUID",user.getUid());
                            userChatListFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.activity_logged_user,userChatListFragment).commit();
                        }
                        else{
                            Toast.makeText(getBaseContext(), "Current user is NULL", Toast.LENGTH_LONG).show();
                        }
                        return true;
                }
                return true;
            }
        });
    }



    private void fetchLocalRecords(String country, String state, String year){
        if(databaseHelper.isDbEmpty()){
            fetchServerRecords();
        }
        else{
            userList = new ArrayList<>();
            userList = databaseHelper.getLatestUsers(country,state,year,this);
            Log.d("Final User Size", Integer.toString(userList.size()));

            if(recyclerView!=null){
                recyclerView.setVisibility(View.VISIBLE);
            }
            Log.d("User List SIZE", String.valueOf(userList.size()));

            userListAdapter = new UserListAdapter(userList);
            userListAdapter.notifyDataSetChanged();
            mLayoutManager = new LinearLayoutManager(getApplicationContext());

            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(userListAdapter);

            Bundle bundle = new Bundle();
            bundle.putSerializable("userList", userList);
            bundle.putString("selectedCountry",selectedCountry);
            bundle.putString("selectedState",selectedState);
            bundle.putString("selectedCity",city);

            Log.d("Map View SIZE", String.valueOf(userList.size()));
            findUserMapFragment = new FindUserMapFragment();
            findUserMapFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_find_users, findUserMapFragment)
                    .commit();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int listSize = userList.size();
                    if (listSize != 0)
                    {
                        progressDialog = new ProgressDialog(LoggedUserActivity.this);
                    if (listSize == getLastCompleteVisibleItemPosition() + 1) {
                        // Toast.makeText(LoggedUserActivity.this, "Last element reached " + totalItemCount, Toast.LENGTH_LONG).show();
                        int position = totalItemCount;
                        int minId = userList.get(userList.size() - 1).id;
                        int maxId = userList.get(0).id;
                        if (selectedCountry == "Select Country") {
                            selectedCountry = null;
                        }
                        if (selectedYear == "Select Year") {
                            selectedYear = null;
                        }
                        if (selectedState == "Select State") {
                            selectedState = null;
                        }
                        progressDialog.setMessage("Refreshing UserList & Map..");
                        progressDialog.show();
                        userList.addAll(databaseHelper.getMoreLatestUsers(maxId, selectedCountry, selectedState, selectedYear, LoggedUserActivity.this));
                        userList.addAll(databaseHelper.getOldUsers(minId, selectedCountry, selectedState, selectedYear, LoggedUserActivity.this));
                        progressDialog.hide();
                        Log.d("Final User Size", Integer.toString(userList.size()));
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userList", userList);
                        bundle.putString("selectedCountry", selectedCountry);
                        bundle.putString("selectedState", selectedState);
                        bundle.putString("selectedCity", city);

                        Log.d("Map View SIZE", String.valueOf(userList.size()));
                        findUserMapFragment = new FindUserMapFragment();
                        findUserMapFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.activity_find_users, findUserMapFragment)
                                .commit();

                        //int lastId = getLastId(userList.get(userList.size()-1).id);
                        //userList.add(DataAccessLayer.refreshRecords(lastId,selectedCountry,selectedState,selectedYear));
                        userListAdapter = new UserListAdapter(userList);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        userListAdapter.notifyDataSetChanged();
                        mLayoutManager.scrollToPosition(position);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(userListAdapter);
                        progressDialog.hide();
                    }
                }
                }
            });
        }
    }

    private void fetchServerRecords(){
        userList = new ArrayList<>();
        String usersFetchUrl = "http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true";
        Log.d("Final tech URL", usersFetchUrl);
        databaseHelper = new UserDatabaseHelper(this);


        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {

            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        if (response != null) {
                            JSONObject person = (JSONObject) response.get(i);
                            UserDataModel model = new UserDataModel();
                            model.id = person.getInt("id");
                            model.nickname = person.getString("nickname");
                            model.longitude = person.getDouble("longitude");
                            model.latitude = person.getDouble("latitude");
                            model.year = person.getInt("year");
                            model.city = person.getString("city");
                            model.state = person.getString("state");
                            model.country = person.getString("country");
                            model.timestamp = person.getString("time-stamp");
                            databaseHelper.insertUser(model);
                            //userList.add(model);
                            //Log.d("Data User List rew", userList.get(i).toString() + String.valueOf(userList.size()));
                        }
                    }
                    //userList.addAll(databaseHelper.getAllUsers());
                    if(!databaseHelper.isDbEmpty()){

                    }
                    //  }
                    else {
                        // if (view_option=="map_view") {
                        if(findUserMapFragment!=null){
                        getSupportFragmentManager().beginTransaction().remove(findUserMapFragment).commit();}
                        //}else if(view_option == "menu_list"){
                        if(recyclerView!=null) {
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        //}
                        Toast.makeText(getBaseContext(), "Please Select Other Country and State", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }


        };




        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest(usersFetchUrl, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

    private int getLastCompleteVisibleItemPosition() {
        return mLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(userChatListFragment!=null){
            getSupportFragmentManager().beginTransaction().remove(userChatListFragment).commitAllowingStateLoss();}

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logged_user_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.hello);
        menuItem.setTitle("Hello, "+loginName.substring(0,loginName.length()-10));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.hello:
                return true;
            case R.id.signout:
                if(userChatListFragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(userChatListFragment).commitAllowingStateLoss();}
                Intent intent = new Intent(this,HomeOptionsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
