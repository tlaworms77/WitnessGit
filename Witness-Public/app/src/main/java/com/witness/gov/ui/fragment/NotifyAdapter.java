package com.witness.gov.ui.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witness.gov.R;
import com.witness.gov.data.NotifyItem;

import java.util.ArrayList;

public class NotifyAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<NotifyItem> data;
    private int layout;

    public NotifyAdapter(Context context, int layout){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout=layout;
        data=new ArrayList<>();
    }

    public NotifyAdapter(Context context, int layout, ArrayList<NotifyItem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=inflater.from(parent.getContext()).inflate(layout,parent,false);
        }

        NotifyItem item=data.get(position);

        Log.i("userTell==", item.getUserTell());


        LinearLayout notify_layout=(LinearLayout)convertView.findViewById(R.id.notify_layout);
        notify_layout.setPadding(10,10,10,10);
        notify_layout.setElevation(10);
        TextView date=(TextView)convertView.findViewById(R.id.date);
        date.setText(item.getDate().split(" ")[1]);
        TextView situation=(TextView)convertView.findViewById(R.id.situation);
        situation.setText(item.getSituation());
        TextView userTell=(TextView)convertView.findViewById(R.id.userTell);
        userTell.setText(item.getUserTell());
        TextView startLocation=(TextView)convertView.findViewById(R.id.startLocation);
        startLocation.setText(item.getStartLocation());

        return convertView;
    }

    public void addItem(NotifyItem item){
        if(item!=null) {
            Log.i("NotifyAdapter", "addItem========");
            data.add(item);
        }
    }

    public void removeItem(int position){
        data.remove(position);
    }

    public void removeItem(String uid){
        for(int i=0; i<data.size();i++){
            NotifyItem  item=data.get(i);
            if(item.getUid().equals(uid)){
                data.remove(i);
            }
        }
    }

}
