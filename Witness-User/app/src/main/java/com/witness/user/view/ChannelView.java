package com.witness.user.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.connector.servicehelper.PlayRTCServiceHelperListener;
import com.witness.user.PlayRTCActivity;
import com.witness.user.data.ChannelData;
import com.witness.user.data.GeoCoder;
import com.witness.user.data.GpsInfo;
import com.witness.user.data.Locations;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/*
 * 채널을 생성하거나 만들어진 채널 목록을 조회하여 채널에 입장하는 UI를 제공하는 RelativeLayout 확장 Class
 * 내부적으로 채널 목록 리스트의 채널 입장 버튼을 눌렀을 때 해당 채널 정보를 전달 받기 위한 IChannelListAdapter구현.
 * 채널 목록을 조회하기 위해 PlayRTC의 getChannelList메소드를 사용하며, 응답 결과를 받기 위해
 * PlayRTCServiceHelperListener 구현체가 필요하다.
 * 채널 생성/입장 버튼 선택 시 선택 정보를 전달하기 위해 PlayRTCChannelViewListener Interface를 정의.
 *
 * 채널 생성 버튼 선택 시
 * - void onClickCreateChannel(String channelName, String userId, String userName)
 * 채널 입장 버튼 선택 시
 * - void onClickConnectChannel(String channelId, String userId, String userName)
 *
 *
 * @see com.sktelecom.playrtc.connector.servicehelper.PlayRTCServiceHelperListener
 * @see ChannelListAdapter.IChannelListAdapter
 */
public class ChannelView extends RelativeLayout implements ChannelListAdapter.IChannelListAdapter {
    private static final String LOG_TAG = "CHANNEL_INFO";

    /**
     * 채널 생성 탭 Layout
     */
    private LinearLayout tabCreate = null;

    /**
     * 채널 입장 탭 Layout
     */
    private LinearLayout tabConnect = null;

    /**
     * 창 닫기 버튼
     */
    private Button tabClose = null;

    /**
     * 채널 생성 화면 영역
     */
    private LinearLayout createContents = null;

    /**
     * 채널 생성 화면 - 채널 이름 입력
     */
    private EditText txtCrChannelName = null;

    /**
     * 채널 생성 화면 - 사용자 아이디(Application 사용자) 입력
     */
    private EditText txtCrUserId = null;

    /**
     * 채널 생성 화면 - 사용자 이름 입력
     */
    private EditText txtCrUserName = null;

    /**
     * 채널 생성 화면 입력 컨트롤 초기화
     */
    private Button btnCrClear = null;

    /**
     * 채널 생성 버튼
     */
    private Button btnCrCreate = null;

    /**
     * 채널 생성 후 발급 빋은 채널 아이디 출력
     */
    private TextView labelChannelId = null;

    /**
     * 채널 입장 화면 영역
     */
    private LinearLayout connectContents = null;

    /**
     * 채널 입장 화면 - 채널 목록 출력 리스트
     */
    private ListView chList = null;

    /**
     * 채널 입장 화면 - 사용자 아이디(Application 사용자) 입력
     */
    private EditText txtCnUserId = null;

    /**
     * 채널 입장 화면 - 사용자 이름 입력
     */
    private EditText txtCnUserName = null;

    /**
     * 채널 입장 화면 입력 컨트롤 초기화
     */
    private Button btnCnClear = null;

    /**
     * 채널 목록 리스트 조회 버튼
     */
    private Button btnCnList = null;

    private PlayRTC playRTC = null;

    private ChannelListAdapter listAdapter = null;

    // 채널 생성/입장 버튼 선택 시 선택 정보를 전달하기 위한 PlayRTCChannelViewListener Interface 구현 개체.
    private ChannelViewListener listener = null;

    private String channelId = "";

    /**
     * 채널 생성/입장 버튼 선택 시 선택 정보를 전달하기 위해 PlayRTCChannelViewListener Interface를 정의.
     * <pre>
     * 채널 생성 버튼 선택 시
     * - void onClickCreateChannel(String channelName, String userId, String userName)
     * 채널 입장 버튼 선택 시
     * - void onClickConnectChannel(String channelId, String userId, String userName)
     * </pre>
     */

    private static boolean isConnected=true;

    private PlayRTCActivity activity=null;




    public interface ChannelViewListener {
        /**
         * 채널 생성 버튼 선택 시 채널 생성 관련 정보를 전달
         *
         * @param channelName String, 생성할 채널의 별칭을 지정
         * @param userId      String, 채널을 생성하는 사용자의 Application에서 사용하는 아이디 지정
         * @param userName    userName, 채널을 생성하는 사용자의 이름을 지정
         */
        public abstract void onClickCreateChannel(String channelName, String userId, String userName);

        /**
         * 채널 입장 버튼 선택 시 채널 생성 관련 정보를 전달
         *
         * @param channelId String, 입장 할 채널의 아이디를 지정
         * @param userId    String, 채널에 입장하는 사용자의 Application에서 사용하는 아이디 지정
         * @param userName, 채널에 입장하는 사용자의 이름을 지정
         */
        public abstract void onClickConnectChannel(String channelId, String userId, String userName);
    }

    /**
     * 생성자
     *
     * @param context Context
     */
    public ChannelView(Context context) {
        super(context);

    }
    /**
     * 생성자
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public ChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 생성자
     *
     * @param context  Context
     * @param attrs    AttributeSet
     * @param defStyle int
     */
    public ChannelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * PlayRTCChannelView를 초기화한다.
     *
     * @param activity PlayRTCActivity
     * @param playRTC  PlayRTC
     * @param l        PlayRTCChannelViewListener,  채널 생성/입장 버튼 선택 시 선택 정보를 전달하기 위한 PlayRTCChannelViewListener Interface 구현 개체.
     * @see PlayRTCActivity
     * @see ChannelView.ChannelViewListener
     * @see ChannelListAdapter
     */
    public void init(PlayRTCActivity activity, PlayRTC playRTC, ChannelViewListener l) {
        this.playRTC = playRTC;
        this.listener = l;
        this.listAdapter = new ChannelListAdapter(activity, this);
        this.activity=activity;
        // initLayout();
    }

    /**
     * IChannelListAdapter Interface<br>
     * 채널 목록 리스트의 채널 입장 버튼을 눌렀을 때 해당 채널 정보를 전달 받기 위한 IChannelListAdapter 구현
     *
     * @param data ChannelData, 채널 정보
     * @see ChannelData
     */
    @Override
    public void onSelectListItem(ChannelData data) {
        Log.d("LIST", "onSelectListItem channelId=" + data.channelId);
        if (TextUtils.isEmpty(data.channelId) == false) {
            String userId = this.txtCnUserId.getText().toString();
            String userName = this.txtCnUserName.getText().toString();
            // 채널 입장 버튼 선택 시 채널 생성 관련 정보를 전달
            this.listener.onClickConnectChannel(data.channelId, userId, userName);
        }
    }


    /**
     * 채널아이디를 반환한다. <br>
     * 채널 생성 또는 채널입장 시 획득한 채널의 아이디
     *
     * @return String
     */
    public String getChannelId() {
        return this.channelId;
    }

    /**
     * 외부에서 채널아이디를 전달 받아 채널 생성 탭의 출력영역에 표시한다.<br>
     * PlayRTC의 createChannel의 결과로 채널아이디를 발급받아 아이디가 전달 된다.
     *
     * @param channelId String, 채널아이디
     */
    public void setChannelId(final String channelId) {
        this.channelId = channelId;
        this.labelChannelId.post(new Runnable() {
            public void run() {
                labelChannelId.setText(channelId);
            }
        });
    }

    /**
     * PlayRTC의 getChannelList메소드를 호출하여 채널 목록을 조회하고 리스트에 출력한다.<br>
     * 채널 목록을 전달 받기 위해 PlayRTCServiceHelperListener 구현 개체가 필요
     *
     * @see com.sktelecom.playrtc.connector.servicehelper.PlayRTCServiceHelperListener
     */

    private List<ChannelData> list;


    //permission value
    private GeoCoder coder;

    //type
    private String type=null;

    //database 대신 value transport
    SharedPreferences pref;

    //channel number
    private int cnt;

    public void setting(final GpsInfo info){

        coder=new GeoCoder(activity);
        coder.settingGeo(info.getLocation());

        //Locatons - Address
        activity.getLocations().setLocation(coder.getFromLocation(0));

        pref= activity.getSharedPreferences("pref", MODE_PRIVATE);

        if(pref!=null){
            type=pref.getString("type","");
        }

    }

    public void startChannel(final GpsInfo info){

        setting(info);

        String channelName = type;
        String userId = info.getLat() + " " + info.getLon() + "/" + coder.getList();

        String userName = pref.getString("num","");
        if (ChannelView.this.listener != null) {
            ChannelView.this.listener.onClickCreateChannel(channelName, userId, userName);
        }
    }

}

