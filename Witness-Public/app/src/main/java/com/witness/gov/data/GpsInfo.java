package com.witness.gov.data;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.witness.gov.PlayRTCActivity;
import com.witness.gov.ui.LoadingActivity;
//import com.witness.gov.ui.TestActivity;

public class GpsInfo implements LocationListener {

    /*field*/
    double lat;//위도
    double lon;//경도
    boolean isGpsEnabled=false;//GPS 사용여부
    boolean isIntEnabled=false;//internet 사용여부
    boolean getLocation=false;

    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;

    private Location location;
    protected LocationManager manager;//location 관리자

    private PlayRTCActivity activity;
    //private TestActivity activity;
    private static String LOG_TAG="GpsInfo";

    /*constructor*/
    public GpsInfo(){

    }

    public GpsInfo(PlayRTCActivity activity){
        this.activity=activity;
    }
    /*public GpsInfo(TestActivity activity){
        this.activity=activity;
    }*/

    /*method*/
    @TargetApi(23)
    public void checkPermission(){
        if(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //Permission denied
            Log.i("checkPermission", "Denied");
            activity.requestPermissions(LoadingActivity.MANDATORY_PERMISSIONS,100);


        }
    }
    public void getGPSinfo(){

        //version check
        if ( Build.VERSION.SDK_INT >= 23)
        {
            checkPermission();
        }

        try {

            getLocation=true;

            manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            isIntEnabled=manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            isGpsEnabled=manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(!isIntEnabled&&!isGpsEnabled){

            }else {

                if (isIntEnabled) {

                    manager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (manager != null) {
                        location = manager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.i("check", lat+""+lon);

                        }
                    }
                }

                if (isGpsEnabled) {
                    if (location == null) {
                        manager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (manager != null) {
                            location = manager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();


                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        String loc=lat+":"+lon;
        saveLocation(loc);

    }


    //위도값
    public double getLat(){
        if(location!=null){
            lat=location.getLatitude();
        }
        return lat;
    }

    //경도값
    public double getLon(){
        if(location!=null){
            lon=location.getLongitude();
        }
        return lon;
    }

    public Location getLocation(){
        if(location!=null){
            return location;
        }
        return null;
    }

    //사용중지
    public void stopGPS(){
        if(manager!=null){
            manager.removeUpdates(GpsInfo.this);
        }
    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();

        String loc=lat+":"+lon;
        saveLocation(loc);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void saveLocation(String loc){

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database.getReference("locations")
                .child(activity.getLocation())
                .child(activity.getItem().getUid())
                //.child("wow")
                //.child("-Test")
                .child("point")
                .child("public")
                .setValue(loc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(LOG_TAG, "OnScucces GpsInfo");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(LOG_TAG, "onFailure");
                        TryAgain();

                    }
                });
    }

    public void TryAgain() {

        new AlertDialog.Builder(activity)
                .setTitle("위치 파악 실패")
                .setMessage("다시 시도하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String loc=lat+":"+lon;
                        saveLocation(loc);
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();

    }


}
