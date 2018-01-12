package com.example.raed.chatapp;

/**
 * Created by Raed on 17/10/2017.
 */

public class Message {
    private static final String TAG = "Message";

    private String userName;
    private String message;
    private String photoUrl;

    public Message (){

    }

    public Message(String userName, String message, String photoUrl) {
        this.userName = userName;
        this.message = message;
        this.photoUrl = photoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
