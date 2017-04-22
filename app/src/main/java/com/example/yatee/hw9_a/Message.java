package com.example.yatee.hw9_a;

/**
 * Created by yatee on 4/20/2017.
 */

public class Message {
    private long id;
    private String name;
    private String message;
    private String userId;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }

    private String dateTime;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
