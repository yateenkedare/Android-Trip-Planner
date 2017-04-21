package com.example.yatee.hw9_a;

import java.util.ArrayList;

public class User {
    private String fName,lName,photoURL,gender, id;
    private ArrayList<String> friends;
    private ArrayList<String> myTrips, requestsReceived, requests;

    public User() {
    }

    public ArrayList<String> getRequestsReceived() {
        return requestsReceived;
    }

    public void setRequestsReceived(ArrayList<String> requestsReceived) {
        this.requestsReceived = requestsReceived;
    }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<String> requests) {
        this.requests = requests;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //************************************************************
    public ArrayList<String> getMyTrips() {
        return myTrips;
    }

    public void setMyTrips(ArrayList<String> myTrips) {
        this.myTrips = myTrips;
    }
    //************************************************************

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
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
