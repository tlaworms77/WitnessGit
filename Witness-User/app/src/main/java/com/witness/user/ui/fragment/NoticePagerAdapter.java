package com.witness.user.ui.fragment;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.witness.user.R;


public class NoticePagerAdapter extends BasePagerAdapter {

    public NoticePagerAdapter(int num){
        super(num);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        ImageView noticeView = new ImageView(view.getContext());

        if(position == 0) {
            noticeView.setBackgroundResource(R.drawable.notice_img1);
        }else if(position == 1){
            noticeView.setBackgroundResource(R.drawable.notice_img2);
        }

        view.addView(noticeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return noticeView;

    }

}
