package com.witness.gov.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.witness.gov.PlayRTCActivity;
import com.witness.gov.R;

public class MapView extends TMapView{

    private boolean isInitialized = false;
    private Location cacheLocation = null;
    private PlayRTCActivity activity;

    public MapView(Context context, double centerLon, double centerLat, int zoomLevel, int tileType) {
        super(context, centerLon, centerLat, zoomLevel,tileType);
    }

    public MapView(Context context, double centerLon, double centerLat, int zoomLevel) {
        super(context, centerLon, centerLat, zoomLevel);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public MapView(Context context, int tileType) {
        super(context, tileType);
    }

    public MapView(Context context){
        super(context);
    }

    public void setActivity(PlayRTCActivity activity){
        this.activity=activity;
    }

    public void start(){

        setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupMap();
                    }
                });
            }

            @Override
            public void SKTMapApikeyFailed(String s) {

            }
        });
        setSKTMapApiKey(getResources().getString(R.string.key));
        setLanguage(TMapView.LANGUAGE_KOREAN);

    }

    private void setupMap() {
        isInitialized = true;
        setMapType(TMapView.MAPTYPE_STANDARD);
        //        mapView.setSightVisible(true);
        //        mapView.setCompassMode(true);
        //        mapView.setTrafficInfo(true);
        //        mapView.setTrackingMode(true);
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }

    }

    public void moveMap(double lat, double lng) {
        setCenterPoint(lng, lat);
    }

    public void setMyLocation(double lat, double lng) {
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(activity, android.R.drawable.ic_dialog_map)).getBitmap();
        setIcon(icon);
        setLocationPoint(lng, lat);
        setIconVisibility(true);
    }

    public void searchRoute(TMapPoint start, TMapPoint end){
        TMapData data = new TMapData();
        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path.setLineWidth(5);
                        path.setLineColor(Color.RED);
                        addTMapPath(path);
                        Bitmap s = ((BitmapDrawable) ContextCompat.getDrawable(activity, android.R.drawable.ic_input_delete)).getBitmap();
                        Bitmap e = ((BitmapDrawable) ContextCompat.getDrawable(activity, android.R.drawable.ic_input_get)).getBitmap();
                        setTMapPathIcon(s, e);

                    }
                });
            }
        });
    }

    public boolean getInitialized(){
        return isInitialized;
    }

    public void setCacheLocation(Location cacheLocation){
        this.cacheLocation=cacheLocation;
    }

    public void show(){
        Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.log_show);
        animation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation anim) {

            }

            @Override
            public void onAnimationRepeat(Animation anim) {

            }

            @Override
            public void onAnimationStart(Animation anim) {
                MapView.this.setVisibility(View.VISIBLE);
            }

        });
        this.startAnimation(animation);
        MapView.this.bringToFront();

    }

    public void hide(){
        Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.log_hide);
        animation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation anim) {
                MapView.this.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation anim) {

            }

            @Override
            public void onAnimationStart(Animation anim) {

            }

        });
        MapView.this.startAnimation(animation);
    }

}
