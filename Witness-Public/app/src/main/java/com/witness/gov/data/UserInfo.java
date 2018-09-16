package com.witness.gov.data;

import com.google.firebase.database.DataSnapshot;


public class UserInfo{

    //field
    String uid;
    String email;
    String pw;
    String userName;
    String field;
    String location;
    String state;

    //constructor
    public UserInfo(){}
    public UserInfo(DataSnapshot user){
        this.uid=user.getKey();
        this.email=user.child("email").getValue().toString();
        this.pw=user.child("pw").getValue().toString();
        this.userName=user.child("userName").getValue().toString();
        this.field=user.child("field").getValue().toString();
        this.location=user.child("location").getValue().toString();
        this.state=user.child("state").getValue().toString();
    }
    public UserInfo(String email, String pw, String userName,String field, String location, String state){
        this.email=email;
        this.pw=pw;
        this.userName=userName;
        this.field=field;
        this.location=location;
        this.state=state;
    }

    //method
    public void setUid(String uid){
        this.uid=uid;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setPw(String pw){
        this.pw=pw;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public void setField(String field){
        this.field=field;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public void setState(String state){
        this.state=state;
    }
    public String getUid(){
        return uid;
    }
    public String getEmail(){
        return email;
    }
    public String getPw(){
        return pw;
    }
    public String getUserName(){
        return userName;
    }
    public String getField(){
        return field;
    }
    public String getLocation(){
        return location;
    }
    public String getState(){
        return state;
    }

    @Override
    public String toString() {
        return email+" / "+pw+" / "+userName+" / "+field+" / "+location+" / "+state;
    }
}
