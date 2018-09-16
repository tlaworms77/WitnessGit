package com.witness.gov;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.witness.gov.data.GpsInfo;
import com.witness.gov.data.NotifyItem;
import com.witness.gov.handler.BaseHandler;
import com.witness.gov.handler.ChannelViewListener;
import com.witness.gov.handler.DataChannelHandler;
import com.witness.gov.ui.fragment.ChatAdapter;
import com.witness.gov.view.LocalVideoView;
import com.witness.gov.view.MapView;
import com.witness.gov.view.ChannelView;
import com.witness.gov.view.LogView;
import com.witness.gov.view.VerticalSeekBar;
import com.witness.gov.view.VideoViewGroup;
import com.witness.gov.view.SoftKeyboard;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.util.PlayRTCRange;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import java.util.Locale;

public class PlayRTCActivity extends Activity {

    private Point point;
    private Point point_mute;
    private ImageView setting;
    private ImageView mute;
    private  ImageView map;

    private boolean isCloesActivity=false;
    private static final String LOG_TAG = "PlayRTCActivity";
    /*
     * PlayRTC-Handler Class
     * PlayRTC 메소드 , PlayRTC객체의 이벤트 처리
     */
    private BaseHandler playrtcHandler = null;

    /*
     * PlayRTCVideoView를 위한 부모 뷰 그룹
     */
    private VideoViewGroup videoLayer = null;
    /*
     * PlayRTCData를 위한 Handler Class
     *
     * @see DataChannelHandler
     */
    private DataChannelHandler dataHandler = null;
    /*
     * 채널 팝업 뷰
     * 채널 서비스에 채널을 생성하거나 입장할 채널을 선택하는 UI
     *
     * @see ChannelView
     */
    private ChannelView channelInfoView = null;
    /*
     * PlayRTC P2P Status report 출력 TextView
     */
    private TextView txtStatReport = null;

    private LogView logView = null;

    private MapView mapView=null;
    /*
     * 영상 뷰를 사용하지 않는 경우 로그 뷰를 화면 중앙에 1회 위치 시키기 위한 변수
     * onWindowFocusChanged에서 로그뷰 Layout을 조정 하므로 필요함.
     */
    private boolean isResetLogViewArea = false;

    private int playrtcType = 1;

    private ListView chatList;
    private ChatAdapter chatAdapter;

    /* geo*/
    private GpsInfo info;
    private NotifyItem item;
    private String loc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playrtc);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        Intent intent = getIntent();
        item=(NotifyItem)intent.getSerializableExtra("item");
        loc=intent.getStringExtra("location");

        /*
         * Witness Sample Type
         * - 1. 112
         * - 2. 119
         * - 3. 114
         */

        playrtcType = 1;
        String channelRing = "false";
        String videoCodec = "vp8";
        String audioCodec = "isac";


        // UI 인스턴스 변수 처리
        initUIControls();

        playrtcHandler = new BaseHandler(this);
        try {
            //  PlayRTC 인스턴스를 생성.
            playrtcHandler.createPlayRTC(playrtcType, channelRing, videoCodec, audioCodec);
        } catch (UnsupportedPlatformVersionException e) {
            // Android SDK 버전 체크 Exception
            e.printStackTrace();
        } catch (RequiredParameterMissingException e) {
            // 필수 Parameter 체크 Exception
            e.printStackTrace();
        }

        // P2P 데이터 통신을 위한 객체 생성
        this.dataHandler = new DataChannelHandler(this);

        // 채널 생성/입장 팝업 뷰 초기화 설정
        this.channelInfoView.init(this, playrtcHandler.getPlayRTC(), new ChannelViewListener(this));

        // PlayRTC 채널 서비스에서 채멀 목록을 조회하여 리스트에 출력한다.


        this.channelInfoView.showChannelList(item);//----------------------------------------------------------------------------------------------잠시 잠금
        // 채널 생성 또는 채널 입장하기 위한 팝업 레이어 출력
        //this.channelInfoView.show(600);

    }

    /*
     * PlayRTCHandler 인스턴스를 반환한다.
     * @return PlayRTCHandler
     */
    public BaseHandler getPlayRTCHandler() {
        return playrtcHandler;
    }

    public VideoViewGroup getVideoLayer() {
        return videoLayer;
    }

    /*
     * 로컬 영상 PlayRTCVideoView 인스턴스를 반환한다.
     * @return PlayRTCVideoView
     */
    public PlayRTCVideoView getLocalVideoView() {
        return videoLayer.getLocalView();
    }

    /*
     * 상대방 영상 PlayRTCVideoView 인스턴스를 반환한다.
     * @return PlayRTCVideoView
     */
    public PlayRTCVideoView getRemoteVideoView() {
        return videoLayer.getRemoteView();
    }

    /*
     * PlayRTCDataChannelHandler 인스턴스를 반환한다.
     * @return PlayRTCDataChannelHandler
     */
    public DataChannelHandler getRtcDataHandler() {
        return dataHandler;
    }

    /*
     * PlayRTCChannelView 인스턴스를 반환한다.
     * @return PlayRTCChannelView
     */
    public ChannelView getChannelInfoPopup() {
        return channelInfoView;
    }

    public String getLocation(){
        return loc;
    }

    public NotifyItem getItem(){
        return item;
    }

    /*
     * PlayRTCLogView의  하단에 로그 문자열을 추가 한다.
     * @param message String
     */
    public void appnedLogMessage(String message) {
        if (logView != null) {
            logView.appnedLogMessage(message);
        }
    }

    /*
     * PlayRTCLogView의 최 하단에 로그 문자열을 추가 한다. <br>
     * 주로 진행 상태 메세지를 표시 하기 위해 최 하단의 진행 상태 메세지만 갱신한다.
     * @param message String
     */
    public void progressLogMessage(String message) {
        if (logView != null) {
            logView.progressLogMessage(message);
        }
    }

    /*
     * PlayRTC P2P 상태 문자열을 출력한다.
     * @param resport
     */
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

        // ViewGroup의 사이즈 재조정, 높이 기준으로 4(폭):3(높이)으로 재 조정
        // 4:3 = width:height ,  width = ( 4 * height) / 3
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

        /* 채널 팝업 뷰 */
        channelInfoView = new ChannelView(this);//= (PlayRTCChannelView) findViewById(R.id.channel_info);

        /*video 스트림 출력을 위한 PlayRTCVideoView의 부모 ViewGroup */
        videoLayer = (VideoViewGroup) findViewById(R.id.videoarea);
        videoLayer.sendt(this);
        /*video 스트림 출력을 위한 PlayRTCVideoView의 부모videoLayer.sendt(this);videoLayer.sendt(this); ViewGsroup */
        //videoLayer = (PlayRTCVideoViewGroup) findViewById(R.id.videoarea);

        /* 로그 출력 TextView */
        logView = (LogView) this.findViewById(R.id.logtext);

        /* PlayRTC P2P Status report 출력 TextView */
        txtStatReport = (TextView) this.findViewById(R.id.txt_stat_report);
        String text = "Local\n ICE:none\n Frame:0x0x0\n Bandwidth[0bps]\n RTT[0]\n eModel[-]\n VFLost[0]\n AFLost[0]\n\nRemote\n ICE:none\n Frame:0x0x0\n Bandwidth[0bps]\n VFLost[0]\n AFLost[0]";
        txtStatReport.setText(text);

        //Popup 설정

        //LayoutInflater - xml 파일을 view로 구현하여 activity위에 실행[xml의 resource를 view 형태로 반환]
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        settingLinear = (LinearLayout) this.findViewById(R.id.btn_sub);

        settingLayout = (View) layoutInflater.inflate(R.layout.setting_popup, settingLinear);

        muteLinear = (LinearLayout) this.findViewById(R.id.mute_sub);

        muteLayout = (View) layoutInflater.inflate(R.layout.mute_popup, muteLinear);

        /*listView 설정*/
        initListViewControls();

        /*Map 설정*/
        initMapControls();

        /*KeyBoard 상태에 따른 visibility 설정*/
        initBtn_mainForKeyBoard();

        /*Popup 버튼*/
        initPopupUIControls();

        /* 카메라 전/후방 전환 버튼 */
        initSwitchVideoCameraFunctionUIControls();

        /* 후방 카메라 사용 시 플래쉬 On/Off 전환 버튼 */
        initSwitchVideoCameraFlashFunctionUIControls();

        /* DataChannel 기능 버튼 */
        initDataChannelFunctionUIControls();

        /* Peer 채널 퇴장/종료 버튼 */
        // initChannelCloseFunctionUIControls();

        /* 미디어 스트림 Mute 버튼 */
        initMediaMuteFunctionUIControls();

        /* 로컬뷰 미러 모드 전환 버튼 */
        initVideoViewMirrorFunctionUIControls();

        /* 카메라 영상 추가 회전 각 버튼 */
        initCameraDegreeFunctionUIControls();

        /* 카메라 영상 Zoom 기능 버튼 */
        //initCameraZoomFunctionUIControls();

        /* 카메라 노출 보정 기능 버튼 */
        initCameraExposureFunctionUIControls();

        /* Video View ShowSnapshot 기능 버튼 */
        //initVideoViewShowSnapshotFunctionUIControls();


    }

    /*listView 설정*/
    public void initListViewControls(){

        //chatList 기본 설정

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


    /*Map 설정*/
    public void initMapControls(){
        mapView=(MapView)findViewById(R.id.map_view);
        mapView.setActivity(this);
        mapView.start();

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database.getReference("locations")
                .child(loc)
                .child(item.getUid())
                .child("point")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String []publicUser=dataSnapshot.child("public").getValue().toString().split(":");
                        String []user=dataSnapshot.child("user").getValue().toString().split(":");

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

    /*Popup 설정 버튼*/
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


    }

    /*KeyBoard 상태에 따른 visibility 설정*/
    public void initBtn_mainForKeyBoard(){
        //Keyboard 상태 변화

        final RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.playrtc_layout);
        InputMethodManager controlManager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(relativeLayout, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplication(),"키보드 내려감",Toast.LENGTH_SHORT).show();
                        ((LinearLayout)relativeLayout.findViewById(R.id.btn_main)).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplication(),"키보드 올라감",Toast.LENGTH_SHORT).show();
                        ((LinearLayout)relativeLayout.findViewById(R.id.btn_main)).setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    /* 카메라 전/후방 전환 버튼 */
    private void initSwitchVideoCameraFunctionUIControls() {
        ImageView cameraBtn = (ImageView) this.findViewById(R.id.viewChange);

        /* 카메라 전환 */
        if (playrtcType < 3) {
            cameraBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playrtcHandler.switchVideoCamera();
                }
            });
        } else {
            // 영상 전송을 사용하지 않으므로 화면에서 숨긴다.
            cameraBtn.setVisibility(View.INVISIBLE);
        }
    }

    /* 후방 카메라 사용 시 플래쉬 On/Off 전환 버튼 */
    private void initSwitchVideoCameraFlashFunctionUIControls() {
        final LinearLayout flashBtn = (LinearLayout) settingLayout.findViewById(R.id.flash);
        /* 후방 카메라 플래쉬 On/Off, 후방 카메라 사용 시 작동  */

            flashBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playrtcHandler == null) {
                        return;
                    }
                    playrtcHandler.switchBackCameraFlash(flashBtn);
                }
            });



    }


    /* DataChannel 기능 버튼 */



    private void initDataChannelFunctionUIControls() {
        /* DataChannel Text 전송 버튼 */
        ImageButton btnText = (ImageButton) this.findViewById(R.id.enter);
        /* DataChannel 파일 전송 버튼 */
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


    /* 미디어 스트림 Mute 버튼 */
    private void initMediaMuteFunctionUIControls() {
        /* Local Audio Mute 버튼 */
        final Switch btnMuteLAudio = (Switch) muteLayout.findViewById(R.id.local_amute);
        /* Local Audio Mute 처리시 로컬 음성 스트림은 상대방에게 전달이 되지 않는다. */
        btnMuteLAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (playrtcHandler != null) {
                    playrtcHandler.setRemoteAudioMute(isChecked);
                }
            }
        });

        /* Remote Audio Mute 버튼 */
        Switch btnMuteRAudio = (Switch) muteLayout.findViewById(R.id.remote_amute);
        /* Remote Video Mute 처리시 상대방 영상 스트림은 수신되나 소리는 출력되지 않는다. */
        btnMuteRAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (playrtcHandler != null) {
                    playrtcHandler.setRemoteAudioMute(isChecked);
                }
            }
        });

    }


    /* 로컬뷰 미러 모드 전환 버튼 */
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

    /* 카메라 영상 추가 회전 각 버튼 v2.2.9 */
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

    /**
     * 카메라 영상 회전 기능. v2.2.9
     * @param degree int 0 , 90, 180, 270
     */
    private void setCameraRotation(int degree) {

        if(playrtcHandler != null) {
            playrtcHandler.setCameraRotation(degree);
        }

    }

    /* 카메라 노출 보정 기능 버튼 v2.3.0 */
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
                    exposureRange = playrtcHandler.getCameraExposureCompensationRange();
                    int min = exposureRange.getMinValue();
                    int max = exposureRange.getMaxValue();
                    int exposureLevel = playrtcHandler.getCameraExposureCompensation();
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
                playrtcHandler.setCameraExposureCompensation(exposureLevel);
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

        if (playrtcType == 3 || playrtcType == 4) {
            if (hasFocus && isResetLogViewArea == false) {
                resetLogViewArea();
            }
            return;
        }
        /*
        // Layout XML을 사용하지 않고 소스 코드에서 직접 샹성하는 경우
        if (hasFocus && videoLayer.isCreatedVideoView() == false) {

            // 4. 영상 스트림 출력을 위한 PlayRTCVideoView 동적 생성
            videoLayer.createVideoView();

        }
        */
        // Layout XML에 VideoView를 기술한 경우. v2.2.6
        if (hasFocus && videoLayer.isInitVideoView() == false) {

            // 4. 영상 스트림 출력을 위한 PlayRTCVideoView 초기화
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

        //WindowManager - 화면상의 크기 구하기
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

        //offset_x : 화면상의 x의 위치
        //offset_y : 화면상의 y의 위치
        int offset_x=point.x+100;
        Log.i("offset_x", offset_x+"");
        int offset_y=point.y+160;
        Log.i("offset_y", offset_y+"");

        //showAtLocation - 위치설정 [Parent parent, Gavity, int offset_x, int offset_y]
        if(position==1) {

        popup.showAtLocation(settingLayout, Gravity.NO_GRAVITY, offset_x, offset_y);
        }else if(position==2) {

            popup.showAtLocation(muteLayout, Gravity.NO_GRAVITY, offset_x, offset_y);
        }
    }

    @Override
    public void onBackPressed() {

        // Activity를 종료하도록 isCloesActivity가 true로 지정되어 있다면 종료 처리
        if (isCloesActivity) {
            // BackPress 처리 -> onDestroy 호출
            Log.e(LOG_TAG, "super.onBackPressed()===============================");
            setResult(RESULT_OK, new Intent());
            super.onBackPressed();
        } else {
            // 만약 채널에 입장한 상태이면 먼저 채널을 종료한다.
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Witness");
            alert.setMessage("[ "+loc+" ]를 종료하겠습니까?");

            alert.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 채널에 입장한 상태라면 채널을 먼저 종료한다.
                    // 종료 이벤트에서 isCloesActivity를 true로 설정하고 onBackPressed()를 호출하여
                    // Activity를 종료 처리
                    Log.e(LOG_TAG, "isChannelConnected():" +playrtcHandler.isChannelConnected());
                    if(playrtcHandler.getPlayRTC()!=null) Log.e(LOG_TAG, "getPlayRTC() is not null");
                    if (playrtcHandler.isChannelConnected() == true) {
                        isCloesActivity = false;
                        // PlayRTC 플랫폼 채널을 종료한다.
                        Log.e(LOG_TAG, "isChannelConnected()");
                        playrtcHandler.disconnectChannel();

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
        // 미디어 스트리밍 처리 pause
        if (playrtcHandler != null) playrtcHandler.onActivityPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 미디어 스트리밍 처리 resume
        if (playrtcHandler != null) playrtcHandler.onActivityResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(LOG_TAG, "onDestroy===============================");

        // PlayRTC 인스턴스 해제
        if (playrtcHandler != null) {
            playrtcHandler.close();
            playrtcHandler = null;
        }
        // v2.2.6
        if (videoLayer != null) {
            videoLayer.releaseView();
        }
        this.finish();
        super.onDestroy();
    }

    public void setChat(String msg, int position){
        Log.e(LOG_TAG,"setChat======================="+ msg);

        chatAdapter.add(msg, position);
        chatAdapter.notifyDataSetChanged();
    }
}
