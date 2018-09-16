package com.witness.user.ui.fragment;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BasePagerAdapter extends PagerAdapter {

    private int count;

    public BasePagerAdapter() {
        this(5);
    }

    public BasePagerAdapter(int count) {
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        TextView textView = new TextView(view.getContext());
        textView.setText(String.valueOf(position + 1));
        textView.setBackgroundColor(0xFFFFFF);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.GREEN);
        textView.setTextSize(48);
        view.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return textView;

    }

    public void addItem() {
        count++;
        notifyDataSetChanged();
    }

    public void removeItem() {
        count--;
        count = count < 0 ? 0 : count;

        notifyDataSetChanged();
    }


}