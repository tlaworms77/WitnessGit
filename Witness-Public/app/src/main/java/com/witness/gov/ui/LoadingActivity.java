package com.witness.gov.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.witness.gov.R;

public class LoadingActivity extends Activity {

    private static final int LAUNCHED_PLAYRTC = 100;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private static final String LOG_TAG = "LoadingActivity";
    public static Activity activity;


    public static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_PHONE_STATE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=LoadingActivity.this;
        setContentView(R.layout.activity_loading);

        // Application permission 23
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            checkPermission(MANDATORY_PERMISSIONS);
        }else {
            startLoading();
        }

    }


    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingActivity.this.startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                LoadingActivity.this.finish();
              //activity.finish();
            }
        }, 1000);

    }

    @TargetApi(23)
    private void checkPermission(String[] permissions) {

        requestPermissions(permissions, MY_PERMISSION_REQUEST_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                int cnt = permissions.length;
                for(int i = 0; i < cnt; i++ ) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {

                        Log.i(LOG_TAG, "Permission[" + permissions[i] + "] = PERMISSION_GRANTED");

                    } else {

                        Log.i(LOG_TAG, "permission[" + permissions[i] + "] always deny");
                    }
                }
                break;
        }
        startLoading();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LAUNCHED_PLAYRTC) {
            if (resultCode == RESULT_OK) {
            }
        }
    }


}