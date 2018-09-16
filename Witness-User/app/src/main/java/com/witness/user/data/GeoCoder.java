package com.witness.user.data;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class GeoCoder {

    private Geocoder geocoder;
    private Activity activity;
    private List<Address> listAdd;
    private String[] list;
    private int size;
    private ListIterator iterator;

    public GeoCoder(){

    }

    public GeoCoder(Activity activity) {
        this.activity = activity;
        geocoder = new Geocoder(activity);
    }

    /*method*/
    public void settingGeo(Location info) {

        try {
            if (geocoder != null) {
                listAdd = geocoder.getFromLocation(info.getLatitude(), info.getLongitude(), 5);

            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        boolean isRemove=false;
        for(int i=0; i< listAdd.size();i++){
            String com1=listAdd.get(i).getAddressLine(0);
            for(int j=0; j<listAdd.size();j++){
                String com2=listAdd.get(j).getAddressLine(0);
                if(i!=j){

                    if(com1.equals(com2)){
                        listAdd.remove(j);
                        j--;
                    }else{
                        String[] ex1=com1.split(" ");
                        String[] ex2=com2.split(" ");

                        int num=(ex1.length>ex2.length)? ex2.length: ex1.length;

                        for(int k=0;k<num;k++){
                            if(!ex1[k].equals(ex2[k])) break;

                            if(k==num-1){
                                if(num==ex2.length){
                                    listAdd.remove(j);
                                    j--;
                                }else{
                                    isRemove=true;
                                }
                            }
                        }
                    }

                }

                if(isRemove){
                    listAdd.remove(i);
                    i--;
                    isRemove=false;
                    break;
                }
            }
        }

    }

    public String getList(){
            String list="";
        if(listAdd!=null){
            for(int i=0; i<listAdd.size();i++){
                list+=listAdd.get(i).getAddressLine(0);
                if(i!=listAdd.size()-1){
                    list+="/";
                }
            }
        }
        return list;
    }


    public String getFromLocation(int position){
        return listAdd.get(position).getAddressLine(0);
    }


}
