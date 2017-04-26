package edu.sdsu.anuragg.hometownandchatapp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserChatListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    String currentUser, currentUID;
    List<User> users;
    List<String> usernames;
    UserChatFragment userChatFragment;
    ChatUserMapFragment chatUserMapFragment;

    private ProgressDialog progressDialog;
    boolean isMapSelected = false;
    ArrayAdapter<String> arrayAdapter;

    public UserChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Users..");
        progressDialog.show();
        //getAllUsersFromFirebase();
        currentUser = getArguments().getString("currentUser");
        currentUID = getArguments().getString("currentUID");



        AllChatUsers allChatUsers = new AllChatUsers();
        allChatUsers.execute();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(userChatFragment!=null){
        getFragmentManager().beginTransaction().remove(userChatFragment).commitAllowingStateLoss();}
        if(chatUserMapFragment!=null){
            getFragmentManager().beginTransaction().remove(chatUserMapFragment).commitAllowingStateLoss();}
        }

    @Override
    public void onStop(){
        super.onStop();
        if(userChatFragment!=null){
            getFragmentManager().beginTransaction().remove(userChatFragment).commitAllowingStateLoss();}
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_chat_list, container, false);
        Button chatUserListBtn = (Button)view.findViewById(R.id.viewListId);
        Button chatUserMapBtn = (Button) view.findViewById(R.id.viewMapId);
        chatUserListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMapSelected = false;
                if(chatUserMapFragment!=null){
                    getFragmentManager().beginTransaction()
                            .remove(chatUserMapFragment)
                            .commitAllowingStateLoss();
                }
                if(userChatFragment!=null){
                    getFragmentManager().beginTransaction()
                            .remove(userChatFragment)
                            .commitAllowingStateLoss();
                }
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching Users..");
                progressDialog.show();
                AllChatUsers allChatUsers = new AllChatUsers();
                allChatUsers.execute();
            }
        });

        chatUserMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatUserMapFragment!=null){
                    getFragmentManager().beginTransaction()
                            .remove(chatUserMapFragment)
                            .commitAllowingStateLoss();
                }
                isMapSelected = true;
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Fetching Users..");
                progressDialog.show();
                AllChatUsers allChatUsers = new AllChatUsers();
                allChatUsers.execute();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }


    private class AllChatUsers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            users = new ArrayList<>();
            usernames = new ArrayList<String>();
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                    .iterator();

                            while (dataSnapshots.hasNext()) {
                                DataSnapshot dataSnapshotChild = dataSnapshots.next();
                                User user = dataSnapshotChild.getValue(User.class);
                                if (!TextUtils.equals(user.uid,
                                        currentUID)) {
                                    users.add(dataSnapshotChild.getValue(User.class));
                                    usernames.add(dataSnapshotChild.getValue(User.class).userName);
                                }
                            }
                            if(!isMapSelected) {
                                if(chatUserMapFragment!=null){
                                    getFragmentManager().beginTransaction()
                                            .remove(chatUserMapFragment)
                                            .commitAllowingStateLoss();
                                }
                                arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, usernames);
                                setListAdapter(arrayAdapter);
                            }
                            else{
                                if(arrayAdapter!=null){
                                    arrayAdapter.clear();
                                    arrayAdapter.notifyDataSetChanged();
                                }
                                if(users.size()!=0) {
                                    //if (view_option == "map_view") {
                                    Log.d("Map View SIZE", String.valueOf(users.size()));
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("ChatUserList", (Serializable)users);
                                    bundle.putString("currentUser", currentUser);
                                    bundle.putString("currentUserUID", currentUID);
                                    chatUserMapFragment = new ChatUserMapFragment();
                                    chatUserMapFragment.setArguments(bundle);
                                    getFragmentManager().beginTransaction()
                                            .add(R.id.activity_logged_user, chatUserMapFragment)
                                            .commit();
                                }
                             //   Toast.makeText(getActivity().getBaseContext(), "Total Users to PLot "+users.size() , Toast.LENGTH_SHORT).show();
                            }
                            //
                            //arrayAdapter.notifyDataSetChanged();
                            progressDialog.hide();
                            // All users are retrieved except the one who is currently logged
                            // in device.
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Unable to retrieve the users.
                        }
                    });

            return null;
        }

        @Override
        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(this);
    }

    //Calling method in Activity to Fetch the States on the click of Country item in List view
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        String selectedUsername = parent.getItemAtPosition(position).toString();
        Iterator<User> userList = users.iterator();
        String selectedUserUid;


                        /*users = new ArrayList<>();
                        usernames = new ArrayList<String>();*/
        while (userList.hasNext()) {
            User user = userList.next();
            if (TextUtils.equals(user.userName,
                    selectedUsername)) {
                //Toast.makeText(getActivity().getBaseContext(), "Selected User "+selectedUsername+" "+user.uid, Toast.LENGTH_SHORT).show();
                selectedUserUid = user.uid;
               // Toast.makeText(getActivity().getBaseContext(), selectedUsername+" "+currentUser, Toast.LENGTH_SHORT).show();
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
            }
        }
    }
}
