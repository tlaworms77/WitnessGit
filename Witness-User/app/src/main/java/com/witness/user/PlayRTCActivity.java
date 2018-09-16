package com.witness.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.skt.Tmap.TMapPoint;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.util.PlayRTCRange;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;
import com.witness.user.data.GpsInfo;
import com.witness.user.data.Locations;
import com.witness.user.data.PhoneNum;
import com.witness.user.handler.BaseHandler;
import com.witness.user.handler.ChannelViewListener;
import com.witness.user.handler.DataChannelHandler;
import com.witness.user.ui.fragment.ChatAdapter;
import com.witness.user.view.ChannelView;
import com.witness.user.view.LocalVideoView;
import com.witness.user.view.LogView;
import com.witness.user.view.MapView;
import com.witness.user.view.SoftKeyboard;
import com.witness.user.view.VerticalSeekBar;
import com.witness.user.view.VideoViewGroup;

import java.util.Locale;

public class PlayRTCActivity extends Activity {

    private Point point;
    private Point point_mute;
    private ImageView setting;
    private ImageView mute;
    private  ImageView map;

    private boolean isCloesActivity=false;
    private static final String LOG_TAG = "PlayRTCActivity";

    private BaseHandler baseHandler = null;

    private VideoViewGroup videoLayer = null;

    private DataChannelHandler dataHandler = null;

    private ChannelView channelInfoView = null;

    private TextView txtStatReport = null;

    private LogView logView = null;

    private boolean isResetLogViewArea = false;

    private int Type = 1;

    private ListView chatList;
    private ChatAdapter chatAdapter;

    private String callType;

    /* geo*/
    private GpsInfo info;
    private MapView mapView=null;
    private String loc;
    private String uid;

    /*Locations 저장*/
    Locations locations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playrtc);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        locations=new Locations();
        Intent intent = getIntent();
        callType=intent.getStringExtra("type");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("type", intent.getStringExtra("type"));

        PhoneNum num=new PhoneNum(this);
        num.setting();
        locations.setUserTell(num.getPhone());
        //loc="TestSample";


        locations.setSituation(intent.getStringExtra("situation"));

        editor.putString("num", num.getPhone());
        editor.apply();

        String channelRing = "true";
        String videoCodec = "vp8";
        String audioCodec = "isac";


        initUIControls();

        baseHandler = new BaseHandler(this);
        try {

            baseHandler.createPlayRTC(Type, channelRing, videoCodec, audioCodec);
        } catch (UnsupportedPlatformVersionException e) {

            e.printStackTrace();
        } catch (RequiredParameterMissingException e) {

            e.printStackTrace();
        }


        this.dataHandler = new DataChannelHandler(this);
        this.channelInfoView.init(this, baseHandler.getPlayRTC(), new ChannelViewListener(this));

        info=new GpsInfo(this);
        info.getGPSinfo();
        this.channelInfoView.startChannel(info);

    }

    public Locations getLocations(){
        return locations;
    }

    public GpsInfo getInfo(){
        return info;
    }


    public BaseHandler getBaseHandler() {
        return baseHandler;
    }

    public VideoViewGroup getVideoLayer() {
        return videoLayer;
    }


    public PlayRTCVideoView getLocalVideoView() {
        return videoLayer.getLocalView();
    }


    public PlayRTCVideoView getRemoteVideoView() {
        return videoLayer.getRemoteView();
    }

    public DataChannelHandler getRtcDataHandler() {
        return dataHandler;
    }


    public ChannelView getChannelInfoPopup() {
        return channelInfoView;
    }


    public void appnedLogMessage(String message) {
        if (logView != null) {
            logView.appnedLogMessage(message);
        }
    }


    public void progressLogMessage(String message) {
        if (logView != null) {
            logView.progressLogMessage(message);
        }
    }


    public void printRtcStatReport(final String resport) {
        txtStatReport.post(new Runnable() {
            public void run() {
                txtStatReport.setText(resport);

            }
        });
    }

    private void resetLogViewArea() {
        if(isResetLogViewArea == true) {
            return;
        }

        Point screenDimensions = new Point();
        int height = videoLayer.getHeight();

        float width = (4.0f * height) / 3.0f;


        RelativeLayout.LayoutParams logLayoutparam = new RelativeLayout.LayoutParams((int)width, (int)height);
        logLayoutparam.addRule(RelativeLayout.CENTER_VERTICAL);
        logLayoutparam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        logView.setLayoutParams(logLayoutparam);

        RelativeLayout.LayoutParams videoLayoutparam = new RelativeLayout.LayoutParams((int)width, (int)height);
        videoLayoutparam.addRule(RelativeLayout.CENTER_VERTICAL);
        videoLayoutparam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoLayer.setLayoutParams(videoLayoutparam);

        isResetLogViewArea = true;

    }

    private void initUIControls() {


        channelInfoView = new ChannelView(this);

        videoLayer = (VideoViewGroup) findViewById(R.id.videoarea);
        videoLayer.sendt(this);

        logView = (LogView) this.findViewById(R.id.logtext);

        txtStatReport = (TextView) this.findViewById(R.id.txt_stat_report);
        String text = "Local\n ICE:none\n Frame:0x0x0\n Bandwidth[0bps]\n RTT[0]\n eModel[-]\n VFLost[0]\n AFLost[0]\n\nRemote\n ICE:none\n Frame:0x0x0\n Bandwidth[0bps]\n VFLost[0]\n AFLost[0]";
        txtStatReport.setText(text);

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        settingLinear = (LinearLayout) this.findViewById(R.id.btn_sub);

        settingLayout = (View) layoutInflater.inflate(R.layout.setting_popup, settingLinear);

        muteLinear = (LinearLayout) this.findViewById(R.id.mute_sub);

        muteLayout = (View) layoutInflater.inflate(R.layout.mute_popup, muteLinear);

        initListViewControls();

        initBtn_mainForKeyBoard();

        initPopupUIControls();

        initSwitchVideoCameraFunctionUIControls();

        initSwitchVideoCameraFlashFunctionUIControls();

        initDataChannelFunctionUIControls();

        initMediaMuteFunctionUIControls();

        initVideoViewMirrorFunctionUIControls();

        initCameraDegreeFunctionUIControls();

        initCameraExposureFunctionUIControls();

    }

    public void initListViewControls(){

        chatList=(ListView)findViewById(R.id.chatview);

        chatAdapter=new ChatAdapter(this.getApplicationContext(), R.layout.chat_structure);

        chatList.setAdapter(chatAdapter);

        //When message is added, it makes listview to scroll last message

        chatAdapter.registerDataSetObserver(new DataSetObserver() {

            @Override

            public void onChanged() {

                super.onChanged();

                chatList.setSelection(chatAdapter.getCount()-1);

            }

        });
    }

    public void initMapControls(){

        if(loc ==null || uid==null) return;

        mapView=(MapView)findViewById(R.id.map_view);
        mapView.setActivity(this);
        mapView.start();

        FirebaseDatabase database= FirebaseDatabase.getInstance();
        database.getReference("locations")
                .child(loc)
                .child(uid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                        Log.i("PlayRTC", "onChildChaged : "+ dataSnapshot.toString());
                        String []publicUser=(dataSnapshot.hasChild("public"))? dataSnapshot.child("public").getValue().toString().split(":"): null;
                        String []user=(dataSnapshot.hasChild("user"))? dataSnapshot.child("user").getValue().toString().split(":"):null;

                        if(publicUser!=null&&user!=null) {
                            Log.i(LOG_TAG, "publicUser - "+publicUser[0]+" "+publicUser[1]);
                            Log.i(LOG_TAG, "user - "+user[0]+" "+user[1]);
                            TMapPoint start = new TMapPoint(Double.parseDouble(publicUser[0]), Double.parseDouble(publicUser[1]));
                            TMapPoint end = new TMapPoint(Double.parseDouble(user[0]), Double.parseDouble(user[1]));
                            mapView.setMyLocation(start.getLatitude(), start.getLongitude());
                            mapView.moveMap(start.getLatitude(), start.getLongitude());
                            mapView.searchRoute(start, end);
                        }

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public String getUid(){
        return uid;
    }

    public void setLoc(String loc){
        this.loc=loc;
    }

    public String getLoc(){
        return loc;
    }

    public void initPopupUIControls(){

        setting = (ImageView)findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(point!=null)
                    showPopup(PlayRTCActivity.this, point,1);

            }
        });

        mute=(ImageView)findViewById(R.id.mute);

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(point_mute!=null)
                    showPopup(PlayRTCActivity.this, point_mute,2);
            }
        });

        map=(ImageView)findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mapView.isShown()) {
                    mapView.show();
                }else{
                    mapView.hide();
                }

            }
        });

        setMapClick(false);
    }

    public void setMapClick(boolean setting){
        map.setEnabled(setting);

        if(setting){
            initMapControls();
        }
    }


    public void initBtn_mainForKeyBoard(){

        final RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.playrtc_layout);
        InputMethodManager controlManager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(relativeLayout, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)relativeLayout.findViewById(R.id.btn_main)).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)relativeLayout.findViewById(R.id.btn_main)).setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    private void initSwitchVideoCameraFunctionUIControls() {
        ImageView cameraBtn = (ImageView) this.findViewById(R.id.viewChange);

        if (Type < 3) {
            cameraBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baseHandler.switchVideoCamera();
                }
            });
        } else {
            cameraBtn.setVisibility(View.INVISIBLE);
        }
    }


    private void initSwitchVideoCameraFlashFunctionUIControls() {
        final LinearLayout flashBtn = (LinearLayout) settingLayout.findViewById(R.id.flash);

            flashBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (baseHandler == null) {
                        return;
                    }
                    baseHandler.switchBackCameraFlash(flashBtn);
                }
            });



    }

    private void initDataChannelFunctionUIControls() {
        ImageButton btnText = (ImageButton) this.findViewById(R.id.enter);

        ImageButton btnFile = (ImageButton) this.findViewById(R.id.btn_file);

        final TextView text=(TextView)findViewById(R.id.chat);

            btnText.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!text.getText().equals("")) {
                        dataHandler.sendText(text.getText().toString());
                        text.setText("");
                    }
                }
            });
            btnFile.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataHandler.sendFile();
                }
            });

    }



    private void initMediaMuteFunctionUIControls() {

        final Switch btnMuteLAudio = (Switch) muteLayout.findViewById(R.id.local_amute);

        btnMuteLAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (baseHandler != null) {
                    baseHandler.setRemoteAudioMute(isChecked);
                }
            }
        });


        Switch btnMuteRAudio = (Switch) muteLayout.findViewById(R.id.remote_amute);

        btnMuteRAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (baseHandler != null) {
                    baseHandler.setRemoteAudioMute(isChecked);
                }
            }
        });

    }



    private int flag=0;
    private void initVideoViewMirrorFunctionUIControls() {
        LinearLayout btnMirror = (LinearLayout)settingLayout.findViewById(R.id.mirror);

        btnMirror.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalVideoView view = videoLayer.getLocalView();
                if(flag==0) {
                    flag=1;
                    view.setMirror(true);
                }else{
                    flag=0;
                    view.setMirror(false);
                }
            }
        });

    }


    private int degree;
    private void initCameraDegreeFunctionUIControls() {
        LinearLayout btnCameraDegree = (LinearLayout) settingLayout.findViewById(R.id.turn);

        btnCameraDegree.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameraRotation(degree);

                degree=(degree%270);
                if(degree>0){
                    degree+=90;
                }

            }
        });


    }


    private void setCameraRotation(int degree) {

        if(baseHandler != null) {
            baseHandler.setCameraRotation(degree);
        }

    }


    private VerticalSeekBar exposureRangeBar = null;
    private PlayRTCRange<Integer> exposureRange = null;
    private void initCameraExposureFunctionUIControls() {

        final LinearLayout btnCameraExposure = (LinearLayout)settingLayout.findViewById(R.id.light);
        exposureRangeBar = (VerticalSeekBar) this.findViewById(R.id.seekbar_exposure_compensation);
        btnCameraExposure.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                RelativeLayout layer = (RelativeLayout)findViewById(R.id.btn_exposure_compensation_layer);
                if(layer.isShown()) {
                    layer.setVisibility(View.GONE);
                    btnCameraExposure.setAlpha((float)0.3);
                }
                else {
                    exposureRange = baseHandler.getCameraExposureCompensationRange();
                    int min = exposureRange.getMinValue();
                    int max = exposureRange.getMaxValue();
                    int exposureLevel = baseHandler.getCameraExposureCompensation();
                    exposureRangeBar.setMaximum(max * 2);
                    exposureRangeBar.setProgressAndThumb(exposureLevel + max);
                    layer.setVisibility(View.VISIBLE);
                    btnCameraExposure.setAlpha((float)0.8);
                }
            }
        });
        exposureRangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser == false) {
                    return;
                }
                int max = exposureRange.getMaxValue();
                int exposureLevel = progress - max;
                exposureRangeBar.setProgressAndThumb(progress);
                String sValue = String.format(Locale.getDefault(), "노출: %d", exposureLevel);
                baseHandler.setCameraExposureCompensation(exposureLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

        });



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int []location=new int[2];

        if(setting!=null)
            setting.getLocationOnScreen(location);

        point=new Point();
        point.x=location[0];
        point.y=location[1];

        if(mute!=null)
            mute.getLocationOnScreen(location);

        point_mute=new Point();
        point_mute.x=location[0];
        point_mute.y=location[1];

        if (Type == 3 || Type == 4) {
            if (hasFocus && isResetLogViewArea == false) {
                resetLogViewArea();
            }
            return;
        }

        if (hasFocus && videoLayer.isInitVideoView() == false) {

            videoLayer.initVideoView();

        }
    }


    private View settingLayout=null;
    private View muteLayout=null;
    private LinearLayout settingLinear=null;
    private LinearLayout muteLinear=null;


    public void showPopup(final Activity activity, Point point, int position){

        int width=0;
        int height=0;

        WindowManager windowManager =(WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);


        Display display = windowManager.getDefaultDisplay();


        final PopupWindow popup=new PopupWindow(activity);
        popup.setBackgroundDrawable(getDrawable(R.drawable.rounded));

        if(position==1) {
            width = (int) (display.getWidth() * 0.5);
            height = (int) (display.getHeight() * 0.3);

            popup.setContentView(settingLayout);

        }else if(position==2){
            width = (int) (display.getWidth() * 0.4);
            height = (int) (display.getHeight() * 0.15);

            popup.setContentView(muteLayout);


        }

        popup.setHeight(height);
        popup.setWidth(width);
        popup.setFocusable(true);

        int offset_x=point.x+100;
        Log.i("offset_x", offset_x+"");
        int offset_y=point.y+160;
        Log.i("offset_y", offset_y+"");

        if(position==1) {

        popup.showAtLocation(settingLayout, Gravity.NO_GRAVITY, offset_x, offset_y);
        }else if(position==2) {

            popup.showAtLocation(muteLayout, Gravity.NO_GRAVITY, offset_x, offset_y);
        }
    }

    @Override
    public void onBackPressed() {


        if (isCloesActivity) {

            Log.e(LOG_TAG, "super.onBackPressed()===============================");
            setResult(RESULT_OK, new Intent());
            super.onBackPressed();
        } else {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Witness");
            alert.setMessage("[ "+callType+" ]를 종료하겠습니까?");

            alert.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    Log.e(LOG_TAG, "isChannelConnected():" +baseHandler.isChannelConnected());
                    if(baseHandler.getPlayRTC()!=null) Log.e(LOG_TAG, "getPlayRTC() is not null");
                    if (baseHandler.isChannelConnected() == true) {
                        isCloesActivity = false;
                        // PlayRTC 플랫폼 채널을 종료한다.
                        Log.e(LOG_TAG, "isChannelConnected()");
                        baseHandler.disconnectChannel();

                    }
                    // 채널에 입장한 상태가 아니라면 바로 종료 처리
                    else {
                        isCloesActivity = true;
                        onBackPressed();
                    }
                }
            });
            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    isCloesActivity = false;
                }
            });
            alert.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (baseHandler != null) baseHandler.onActivityPause();
    }

    @Override
    public void onResume() {
        super.onResume();


        if (baseHandler != null) baseHandler.onActivityResume();
    }

    @Override
    protected void onDestroy() {

        if (baseHandler != null) {
            baseHandler.close();
            baseHandler = null;
        }

        if (videoLayer != null) {
            videoLayer.releaseView();
        }
        this.finish();
        super.onDestroy();
    }

    public void setChat(String msg, int position){
        chatAdapter.add(msg, position);
        chatAdapter.notifyDataSetChanged();
    }
}
