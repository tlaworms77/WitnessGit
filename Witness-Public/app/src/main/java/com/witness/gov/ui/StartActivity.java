package com.witness.gov.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.witness.gov.PlayRTCActivity;
import com.witness.gov.R;
import com.witness.gov.data.NotifyItem;
import com.witness.gov.ui.fragment.NotifyAdapter;
import com.witness.skyfishjy.RippleBackground;

import java.util.ArrayList;

public class StartActivity extends Activity implements AdapterView.OnItemClickListener, ChildEventListener{

    private static String LOG_TAG="StartActivity";
    private String uid;
    private String userName;
    private String location;
    private ListView notifyList;
    private NotifyAdapter adapter;

    private boolean isClick=false;
    private PopupWindow popup;
    private NotifyItem clickedItem;
    private View layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        saveIntent();

        UISetting();
    }
    public void saveIntent(){
        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");
        userName=intent.getStringExtra("userName");
        location=intent.getStringExtra("location");
        notifyList=(ListView)findViewById(R.id.notification);
    }

    public void UISetting(){

        adapter=new NotifyAdapter(this,R.layout.item_notify);

        notifyList.setAdapter(adapter);
        notifyList.setOnItemClickListener(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("locations")
                .child(location)
                .addChildEventListener(this);


    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {//------------------------------------보류
        if(dataSnapshot.child("state").getValue().toString().equals("호출")) {
            Log.i("CHILDEventListener", dataSnapshot.toString());
            String uid = dataSnapshot.getKey();
            Log.i("uid", uid);
            String channelId = dataSnapshot.child("channelId").getValue().toString();
            Log.i("channelId", channelId);
            String date=dataSnapshot.child("date").getValue().toString();
            Log.i("date", date);
            String startLocation=dataSnapshot.child("location").getValue().toString();
            Log.i("startLocation", startLocation);
            String userTell = dataSnapshot.child("userTell").getValue().toString();
            Log.i("userTell", userTell);
            String situation = dataSnapshot.child("situation").getValue().toString();
            Log.i("situation", situation);
            NotifyItem item = new NotifyItem(uid, channelId, date, startLocation, userTell, situation);
            adapter.addItem(item);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Log.i("onCHildChanged", dataSnapshot.toString());/*
        String state=dataSnapshot.child("state").getValue().toString();*/
        switch (dataSnapshot.child("state").getValue().toString()){
            case "호출" : onChildAdded(dataSnapshot,null); break;
            case  "준비완료" : onChildRemoved(dataSnapshot); break;
            case "출동" : foundDevice(); break;



        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Log.i("onCHildRemoved", dataSnapshot.toString());
        adapter.removeItem(dataSnapshot.getKey());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



        final NotifyItem item=(NotifyItem) parent.getItemAtPosition(position);
        Log.i(LOG_TAG,"onItemClick====");

        final AlertDialog.Builder alert = new AlertDialog.Builder(StartActivity.this,R.style.MyAlertDialogStyle);
        //alert.setIcon(getResources().getDrawable(R.drawable._logo));
        //alert.setTitle(location);

        int ids=1;

        RelativeLayout relativeLayout =new RelativeLayout(this);
        ImageView icon=new ImageView(this);
        icon.setId(ids);
        icon.setBackground(getDrawable(R.drawable._logo));
        icon.setMaxHeight(10);
        icon.setMaxWidth(10);

        ids=2;
        RelativeLayout.LayoutParams layoutParams =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayout.addView(icon);
        TextView title =  new TextView(this);
        title.setText(location);
        title.setId(ids);
        title.setTextSize(32);
        title.setBackgroundColor(getColor(R.color.indigo));
        title.setTextColor(Color.WHITE);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, 1);
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,1);
        layoutParams.bottomMargin=20;
        //layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        title.setLayoutParams(layoutParams);
        relativeLayout.addView(title);
        alert.setCustomTitle(relativeLayout);



        alert.setMessage(item.getDate().split(" ")[1]+"에 신고가 들어온\n [ "+item.getSituation()+" ]을/를 선택하시겠습니까?");

        alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClick=true;


                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference("locations")
                        .child(location)
                        .child(item.getUid())
                        .child("state")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                                if (isClick&&dataSnapshot.getValue().toString().equals("호출")) {
                                    database.getReference("locations")
                                            .child(location)
                                            .child(item.getUid())
                                            .child("state")
                                            .setValue("준비완료")
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    clickedItem=item;
                                                    nextPage();
                                                    isClick=false;
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

        });

        alert.show();

    }

    public void nextPage() {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        layout = (View) layoutInflater.inflate(R.layout.wait_popup, rippleBackground);

        int width = 0;
        int height = 0;

        //WindowManager - 화면상의 크기 구하기
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        popup = new PopupWindow(this);
        /*popup.setBackgroundDrawable(getDrawable(R.drawable.rounded));*/

        popup.setContentView(layout);
        popup.setHeight(height);
        popup.setWidth(width);
        popup.setFocusable(true);

        popup.showAtLocation(layout, Gravity.NO_GRAVITY, 0, 0);


        rippleBackground = (RippleBackground) layout.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
    }

    private void foundDevice(){

        ImageView foundDevice=(ImageView)layout.findViewById(R.id.foundDevice);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(getApplicationContext(), PlayRTCActivity.class);
                intent.putExtra("item", clickedItem);
                intent.putExtra("location", location);
                startActivityForResult(intent, 100);
                popup.dismiss();
            }
        }, 1000);
    }

}
