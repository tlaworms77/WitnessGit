package com.witness.user.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.witness.user.R;
import com.witness.user.ui.NoticeActivity;
import com.witness.user.ui.StartActivity;

import com.witness.circleindicator.CircleIndicator;


public class NoticeFragment extends Fragment {

    private Button next;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.view_notice, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        pref=getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final NoticeViewPager viewpager = (NoticeViewPager) view.findViewById(R.id.noticeViewPager);
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        next = (Button) view.findViewById(R.id.nextBtn);
        viewpager.setAdapter(new NoticePagerAdapter(2));
        viewpager.setPagingEnabled(false);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(next.getText().equals("시작")){
                    editor=pref.edit();
                    editor.putString("firstCheck", "true");
                    editor.apply();

                    Intent intent = new Intent(getContext().getApplicationContext(), StartActivity.class);
                    getContext().getApplicationContext().startActivity(intent);
                    ((NoticeActivity)getActivity()).finish();
                }

                viewpager.setCurrentItem(viewpager.getCurrentItem()+1);

                if(viewpager.getCurrentItem()==1){
                    next.setText("시작");
                }
            }
        });

        indicator.setViewPager(viewpager);

    }


}
