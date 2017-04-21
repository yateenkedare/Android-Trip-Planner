package com.example.yatee.hw9_a;

import java.util.ArrayList;

/**
 * Created by yatee on 4/20/2017.
 */

public class Trip {
    String title, location, coverURL, key;
    ArrayList<Message> messages;
    public Trip() {
    }

    public Trip(String title, String location, String coverURL, String key){
        this.title = title;
        this.location = location;
        this.coverURL = coverURL;
        messages = new ArrayList<>();
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", coverURL='" + coverURL + '\'' +
                '}';
    }
}
