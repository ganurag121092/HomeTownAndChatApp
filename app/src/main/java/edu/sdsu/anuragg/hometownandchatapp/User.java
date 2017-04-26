package edu.sdsu.anuragg.hometownandchatapp;

import java.io.Serializable;

/**
 * Created by AnuragG on 11-Apr-17.
 */

public class User implements Serializable{
    public String uid;
    public String userEmail;
    public String userName;
    public Double latitude;
    public Double longitude;

    public User(){
    }


    public User(Double latitude, String UID, String userName, Double longitude, String userEmail) {
        this.latitude = latitude;
        this.uid = UID;
        this.userName = userName;
        this.longitude = longitude;
        this.userEmail = userEmail;
    }
}
