package com.witness.user.data;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneNum {


    private Activity activity=null;

    private String phone=null;


    public PhoneNum(){

    }

    public PhoneNum(Activity activity){
       this.activity=activity;
    }


    public void setting(){
        if ( Build.VERSION.SDK_INT >= 23)
        {
            String[]permission={
                    "android.permission.READ_PHONE_STATE"
            };
            if(activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(permission, 100);

            }
        }


        String myNumber = null;

        TelephonyManager mgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            myNumber = mgr.getLine1Number();
            myNumber = myNumber.replace("+82", "0");

        }catch(Exception e){
            e.printStackTrace();
        }
        phone=myNumber;
    }

    public String getPhone(){
       return phone;
    }

}
