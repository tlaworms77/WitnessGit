package com.witness.gov.data;

import java.io.Serializable;

public class NotifyItem implements Serializable{
    private String uid;
    private String channelId;
    private String date;
    private String userTell;
    private String startLocation;
    private String situation;

    public NotifyItem(){}
    public NotifyItem(String uid, String channelId, String date, String startLocation, String userTell, String situation){
        this.uid=uid;
        this.channelId=channelId;
        this.date=date;
        this.userTell=userTell;
        this.startLocation=startLocation;
        this.situation=situation;
    }

    public void setUid(String uid){
        this.uid=uid;
    }
    public void setChannelId(String channelId){
        this.channelId=channelId;
    }
    public void setDate(String date){
        this.date=date;
    }
    public void setUserTell(String userTell){
        this.userTell=userTell;
    }
    public void setStartLocation(String startLocation){
        this.startLocation=startLocation;
    }
    public void setSituation(String situation){
        this.situation=situation;
    }
    public String getUid(){
        return uid;
    }
    public String getChannelId(){
        return channelId;
    }
    public String getDate(){
        return date;
    }
    public String getUserTell(){
        return userTell;
    }
    public String getStartLocation(){
        return startLocation;
    }
    public String getSituation(){
        return situation;
    }
}
