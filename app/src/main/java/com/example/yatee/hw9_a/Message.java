package com.example.yatee.hw9_a;

/**
 * Created by yatee on 4/20/2017.
 */

public class Message {
    String time,text, imageURL, sender;
    public Message() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "time='" + time + '\'' +
                ", text='" + text + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
