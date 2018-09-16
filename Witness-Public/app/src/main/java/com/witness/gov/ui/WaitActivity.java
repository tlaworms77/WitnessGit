package com.witness.gov.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.witness.gov.R;
import com.witness.skyfishjy.RippleBackground;
import com.witness.gov.PlayRTCActivity;
import com.witness.gov.data.NotifyItem;

import java.util.ArrayList;

public class WaitActivity extends Activity {

    private static String LOG_TAG="WaitActivity";
    private ImageView foundDevice;
    private TextView state;
    private NotifyItem item;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_popup);

        Intent intent=getIntent();

        item=(NotifyItem) intent.getSerializableExtra("notifyItem");
        location=intent.getStringExtra("location");

        state=(TextView)findViewById(R.id.state);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        final Handler handler=new Handler();

        foundDevice=(ImageView)findViewById(R.id.foundDevice);
        ImageView button=(ImageView)findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.i("Locaton", location);
        Log.i("UID", item.getUid());

    }

    private void foundDevice(){
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

        Intent intent=new Intent(this, PlayRTCActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("location", location);
        startActivityForResult(intent, 100);
}

}
