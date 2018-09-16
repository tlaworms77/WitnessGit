package com.witness.gov.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.witness.gov.R;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter {

    public class ListContents{
        String msg;
        int type;
        ListContents(String _msg,int _type)
        {
            this.msg = _msg;
            this.type = _type;
        }
    }

    private Activity activity;
    private ArrayList m_List;
    public ChatAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);
        m_List = new ArrayList();
    }

    // 외부에서 아이템 추가 요청 시 사용
    public void add(String _msg,int _type) {
        m_List.add(new ListContents(_msg,_type));
        super.add(new ListContents(_msg, _type));
        Log.e("CHeck",m_List.size()+"");
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int _position) {
        m_List.remove(_position);
    }

    @Override
    public int getCount() {
        return m_List.size();
    }

    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        View row = convertView;

        if (row == null) {

            // inflator를 생성하여, chatting_message.xml을 읽어서 View객체로 생성한다.

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.chat_structure, parent, false);

        }

        // Array List에 들어 있는 채팅 문자열을 읽어

        ListContents msg = (ListContents) m_List.get(position);

        // Inflater를 이용해서 생성한 View에, ChatMessage를 삽입한다.

        TextView msgText = (TextView) row.findViewById(R.id.chat_text);
        LinearLayout chatMessageContainer = (LinearLayout)row.findViewById(R.id.chat_layout);

        msgText.setText(msg.msg);

        if(msg.type==0){
            msgText.setTextColor(context.getResources().getColor(R.color.loop_background_color));
            msgText.setBackground(context.getResources().getDrawable(R.drawable.remotechat));
            chatMessageContainer.setGravity(Gravity.LEFT);
        }else if(msg.type==1){
            msgText.setTextColor(context.getResources().getColor(R.color.app_background_color));
            msgText.setBackground(context.getResources().getDrawable(R.drawable.localchat));
            chatMessageContainer.setGravity(Gravity.RIGHT);
        }



        return row;

    }

}
