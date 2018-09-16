package com.witness.user.data;

public class Locations {
        private String channelId;
        private String situation;
        private String state;
        private String userTell;
        private String date;
        private String location;

        public Locations(){
            this.state="호출";
        }
        public Locations(String channelId, String situation, String userTell, String date, String location){
            this.channelId=channelId;
            this.situation=situation;
            this.state="호출";
            this.userTell=userTell;
            this.date=date;
            this.location=location;
        }

        public  void setChannelId(String channelId){
            this.channelId=channelId;
        }
        public void setSituation(String situation){
            this.situation=situation;
        }
        public void setState(String state){
            this.state=state;
        }
        public void setUserTell(String userTell){
            this.userTell=userTell;
        }
        public void setDate(String date){
            this.date=date;
        }
        public void setLocation(String location){
            this.location=location;
        }
        public String getChannelId(){
            return channelId;
        }
        public String getSituation(){
            return situation;
        }
        public String getState(){
            return state;
        }
        public String getUserTell(){
            return userTell;
        }
        public String getDate(){
            return date;
        }
        public String getLocation(){
            return location;
        }

}
