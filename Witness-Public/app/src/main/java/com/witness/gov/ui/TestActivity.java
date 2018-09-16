/*
package com.witness.gov.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.witness.gov.R;
import com.witness.gov.data.GpsInfo;

public class TestActivity extends Activity {

    private TMapView mapView;
    private Location cacheLocation = null;
    private boolean isInitialized = false;
    private static final String LOG_TAG = "TestActivity";

    private GpsInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mapView=(TMapView)findViewById(R.id.map_view);
        mapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                runOnUiThread(new Runnable() {
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
        mapView.setSKTMapApiKey("e71b83fa-5e23-47a3-9048-273c039cba34");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        info=new GpsInfo(this);
        info.getGPSinfo();

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database.getReference("locations")
                .child("wow")
                .child("-Test")
                .child("point")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String []publicUser=dataSnapshot.child("public").getValue().toString().split(":");
                        String []user=dataSnapshot.child("user").getValue().toString().split(":");

                        if(publicUser!=null&&user!=null) {
                            Log.i(LOG_TAG, "publicUser - "+publicUser[0]+" "+publicUser[1]);
                            Log.i(LOG_TAG, "user - "+user[0]+" "+user[1]);
                            TMapPoint start = new TMapPoint(Double.parseDouble(publicUser[0]), Double.parseDouble(publicUser[1]));
                            TMapPoint end = new TMapPoint(Double.parseDouble(user[0]), Double.parseDouble(user[1]));
                            setMyLocation(start.getLatitude(), start.getLongitude());
                            moveMap(start.getLatitude(), start.getLongitude());
                            searchRoute(start, end);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setupMap() {
        isInitialized = true;
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        //        mapView.setSightVisible(true);
        //        mapView.setCompassMode(true);
        //        mapView.setTrafficInfo(true);
        //        mapView.setTrackingMode(true);
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }
    }

    private void moveMap(double lat, double lng) {
        mapView.setCenterPoint(lng, lat);
    }

    private void setMyLocation(double lat, double lng) {
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map)).getBitmap();
        mapView.setIcon(icon);
        mapView.setLocationPoint(lng, lat);
        mapView.setIconVisibility(true);
    }

    private void searchRoute(final TMapPoint start, TMapPoint end) {
        Log.i(LOG_TAG, "searchRoute===");
        TMapData data = new TMapData();

        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {

                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      path.setLineWidth(5);
                                      path.setLineColor(Color.RED);
                                      mapView.addTMapPath(path);
                                      Bitmap s = ((BitmapDrawable) ContextCompat.getDrawable(TestActivity.this, android.R.drawable.ic_input_delete)).getBitmap();
                                      Bitmap e = ((BitmapDrawable) ContextCompat.getDrawable(TestActivity.this, android.R.drawable.ic_input_get)).getBitmap();
                                      mapView.setTMapPathIcon(s, e);
                                  }
                              }
                );

            }
        });
    }

    public void addMarker(TMapPOIItem poi) {
        TMapMarkerItem item = new TMapMarkerItem();
        item.setTMapPoint(poi.getPOIPoint());
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(poi.getPOIName());
        item.setCalloutSubTitle(poi.getPOIContent());
        Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        item.setCalloutLeftImage(left);
        Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        mapView.addMarkerItem(poi.getPOIID(), item);
    }
}
*/
