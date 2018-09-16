package com.witness.user.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import com.witness.user.R;
import com.witness.user.ui.fragment.NoticeFragment;

public class NoticeActivity extends Activity {

    private Button next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        next = (Button)findViewById(R.id.nextBtn);


        Fragment fragment =Fragment.instantiate(this, NoticeFragment.class.getName());
        FragmentTransaction fragmentTransaction = (FragmentTransaction)getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, fragment);
        fragmentTransaction.commit();



    }

}
