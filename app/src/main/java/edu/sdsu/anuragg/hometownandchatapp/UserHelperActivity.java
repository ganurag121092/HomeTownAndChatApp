package edu.sdsu.anuragg.hometownandchatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.sdsu.anuragg.hometownandchatapp.database.UserDatabaseHelper;

import static android.content.ContentValues.TAG;

public class UserHelperActivity extends AppCompatActivity implements MapFragment.SelectedLocation{
    private EditText mNickname, mPassword, mCity, mLatitude, mLongitude;
    Spinner countrySpinner, stateSpinner, yearSpinner;
    String selectedCountry, selectedState, selectedYear, nickname, password, city, selectedLat, selectedLon;
    DataAccessLayer dataAccessLayer;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    DatabaseReference databaseUsers;
    static UserDataModel userData;
    UserDatabaseHelper udb;
    static FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_helper);
        udb = new UserDatabaseHelper(this);
        NewUserFragment newUserFragment = new NewUserFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_user_helper, newUserFragment)
                .commit();
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    addUserToDatabase(userData,firebaseUser);
                    Log.d("Firebase User", firebaseAuth.getCurrentUser().toString());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                  //  Toast.makeText(UserHelperActivity.this,"Login User Null",Toast.LENGTH_LONG).show();
                }
               /* // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]*/
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void saveClicked(View v){
        mNickname = (EditText) this.findViewById(R.id.nicknametextid);
        mPassword = (EditText) this.findViewById(R.id.passwordtextid);
        mCity = (EditText) this.findViewById(R.id.citytextid);
        mLatitude = (EditText) this.findViewById(R.id.lattextid);
        mLongitude = (EditText) this.findViewById(R.id.lontextid);
        countrySpinner = (Spinner) this.findViewById(R.id.countrylist_spinner);
        stateSpinner = (Spinner) this.findViewById(R.id.statelist_spinner);
        yearSpinner = (Spinner) this.findViewById(R.id.year_spinner);

        boolean isValid = true;
        dataAccessLayer = new DataAccessLayer();
        nickname = mNickname.getText().toString();
        password = mPassword.getText().toString();
        city = mCity.getText().toString();
        selectedCountry = countrySpinner.getSelectedItem().toString();
        selectedState = stateSpinner.getSelectedItem().toString();
        selectedYear = yearSpinner.getSelectedItem().toString();
        selectedLat = mLatitude.getText().toString();
        selectedLon = mLongitude.getText().toString();

        Log.i("Final Name", nickname);
        Log.i("Final Password", password);
        Log.i("Final Country", selectedCountry);
        Log.i("Final State", selectedState);
        Log.i("Final City", city);
        Log.i("Final LATITUDE", mLatitude.getText().toString()+" "+mLongitude.getText().toString());
        if(selectedYear!=null)
            Log.i("Final Yerar", selectedYear);
        Log.i("Final Coordinates", selectedLat+ " " + selectedLon);
        if(TextUtils.isEmpty(nickname)){
            mNickname.setError("Nickname Required");
        }
        if(dataAccessLayer.isUserExists(mNickname.getText().toString(), getBaseContext())){
            //Toast.makeText(getBaseContext(), "Please Enter new Nickname", Toast.LENGTH_LONG).show();
            mNickname.setError("Nickname Already Exists");
            isValid = false;
        }
        if(password.length()<6){
            mPassword.setError("Must be more than 5 characters");
            isValid = false;
        }
        if(TextUtils.isEmpty(password)){
            //Toast.makeText(getBaseContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
            mPassword.setError("Password Required");
            isValid = false;
        }

        if(selectedCountry=="Select Country"){
            Toast.makeText(getBaseContext(), "Please Select Country", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(selectedState=="Select State"){
            Toast.makeText(getBaseContext(), "Please Select State", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if(TextUtils.isEmpty(city)){
            mCity.setError("City Name Required");
            //Toast.makeText(getBaseContext(), "Please Enter City", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if(selectedYear=="Select Year"){
            Toast.makeText(getBaseContext(), "Please Select School Year", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if(TextUtils.isEmpty(selectedLat)|| TextUtils.isEmpty((selectedLon))){
            Toast.makeText(getBaseContext(), "Please Select Location", Toast.LENGTH_LONG).show();
            isValid = false;
        }

//        Toast.makeText(getBaseContext(), "isValid - "+Boolean.toString(isValid), Toast.LENGTH_LONG).show();
        progressDialog.setMessage("Registering User..");
        progressDialog.show();

        if (isValid) {
            Log.i("Data Validity","All enter data Valid");
            userData = new UserDataModel();
            userData.nickname = mNickname.getText().toString();
            userData.password = mPassword.getText().toString();
            userData.country = selectedCountry;
            userData.state = selectedState;
            userData.city = city;
            userData.year = Integer.parseInt(selectedYear);
            userData.latitude = Double.parseDouble(mLatitude.getText().toString());
            userData.longitude = Double.parseDouble(mLongitude.getText().toString());
            createAccount(userData);
            dataAccessLayer.postUserData(userData,getBaseContext());
          //  addUserToDatabase(userData);
            clearFields();
            Toast.makeText(getBaseContext(), "User Data Saved", Toast.LENGTH_LONG).show();
            progressDialog.hide();
            FirebaseAuth.getInstance().signOut();
            /*Intent intent= new Intent(UserHelperActivity.this,HomeOptionsActivity.class);
            startActivity(intent);*/
        }
        progressDialog.hide();
    }
    public void addUserToDatabase(UserDataModel userDataModel, FirebaseUser fbUser) {
        //Log.i("FINAL I m here",userDataModel.nickname + "  "+ firebaseUser.getEmail());
        User user = new User(userDataModel.latitude,fbUser.getUid(),userDataModel.nickname,userDataModel.longitude, fbUser.getEmail());
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(userDataModel.nickname)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent= new Intent(UserHelperActivity.this,HomeOptionsActivity.class);
                            startActivity(intent);
                            //Toast.makeText(UserHelperActivity.this,"User Added in REALtimeDB",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UserHelperActivity.this,"Failed to Added in REALtimeDB",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void createAccount(UserDataModel userDataModel){
        final String email = userDataModel.nickname.toLowerCase() + "@gmail.com";
        final String password = userDataModel.password;
        Log.i("Email and Password", email + " " + userDataModel.password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(UserHelperActivity.this,"Login Unsuccessful",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void resetDataFields(View v){
        Log.i("Reset cliked", "RESET clicked");
        mNickname = (EditText) this.findViewById(R.id.nicknametextid);
        mPassword = (EditText) this.findViewById(R.id.passwordtextid);
        mCity = (EditText) this.findViewById(R.id.citytextid);
        mLatitude = (EditText) this.findViewById(R.id.lattextid);
        mLongitude = (EditText) this.findViewById(R.id.lontextid);
        mNickname.setText("");
        mPassword.setText("");
        mCity.setText("");
        mLatitude.setText("");
        mLongitude.setText("");
        mNickname.setSelection(mNickname.getText().length());
        mNickname.requestFocus();
        mPassword.setSelection(mPassword.getText().length());
        mCity.setSelection(mCity.getText().length());
        mLatitude.setSelection(mLatitude.getText().length());
        mLongitude.setSelection(mLongitude.getText().length());
        clearFields();
    }

    public void clearFields(){
        NewUserFragment newUserFragment = new NewUserFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_user_helper, newUserFragment)
                .commit();
    }

    public void getCoordinates(View v) {
        mNickname = (EditText) this.findViewById(R.id.nicknametextid);
        mPassword = (EditText) this.findViewById(R.id.passwordtextid);
        mCity = (EditText) this.findViewById(R.id.citytextid);
        countrySpinner = (Spinner) this.findViewById(R.id.countrylist_spinner);
        stateSpinner = (Spinner) this.findViewById(R.id.statelist_spinner);
        yearSpinner = (Spinner) this.findViewById(R.id.year_spinner);

        nickname = mNickname.getText().toString();
        password = mPassword.getText().toString();
        selectedCountry = countrySpinner.getSelectedItem().toString();
        selectedState = stateSpinner.getSelectedItem().toString();
        selectedYear = yearSpinner.getSelectedItem().toString();
        city = mCity.getText().toString();
        boolean isValid = true;
        if(TextUtils.isEmpty(nickname)){
            isValid = false;
        }
        if(TextUtils.isEmpty(password)){
            isValid = false;
        }
        if (TextUtils.isEmpty(city)) {
            isValid = false;
        }
        if (selectedYear == "Select Year") {
            isValid = false;
        }
        if (isValid) {
            //Log.i("Country Value", selectedCountry);
            if (selectedCountry != "Select Country") {
                Log.i("Country Value", selectedCountry);
                if (selectedState != "Select State") {

                    MapFragment mapFragment = new MapFragment();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.activity_user_helper, mapFragment)
                            .commit();
                    Bundle bundle = new Bundle();
                    bundle.putString("Country", selectedCountry);
                    bundle.putString("State", selectedState);
                    if (city != null) {
                        bundle.putString("City", city);
                    }
                    mapFragment.setArguments(bundle);
                } else {
                    Toast.makeText(getBaseContext(), "Please Select State", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "Please Select Country", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getBaseContext(), "Name, Password, City or Year cannot be Empty", Toast.LENGTH_LONG).show();
        }
    }


    public void getLatLon(String latitude, String longitude){

    }
}
