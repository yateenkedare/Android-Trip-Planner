package com.example.yatee.hw9_a;

import java.util.ArrayList;

/**
 * Created by rajdeeprao on 4/16/17.
 */

public class User {
    String fName,lName,photoURL,gender;
    ArrayList<User> friends;
//*********************************
    ArrayList<String> myTrips;
//*********************************
    public User() {
    }

    //************************************************************
    public ArrayList<String> getMyTrips() {
        return myTrips;
    }

    public void setMyTrips(ArrayList<String> myTrips) {
        this.myTrips = myTrips;
    }
    //************************************************************

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public User(String fName, String lName, String photoURL, String gender) {

        this.fName = fName;
        this.lName = lName;
        this.photoURL = photoURL;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
