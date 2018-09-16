package com.witness.user.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.witness.user.DiscreteScrollViewOptions;
import com.witness.user.PlayRTCActivity;
import com.witness.user.R;
import com.witness.user.data.Item;
import com.witness.user.data.ItemList;
import com.witness.user.ui.fragment.StartAdapter;
import com.witness.discretescrollview.DSVOrientation;
import com.witness.discretescrollview.DiscreteScrollView;
import com.witness.discretescrollview.InfiniteScrollAdapter;
import com.witness.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;


public class StartActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener  {

    private boolean isCloesActivity;
    private static String LOG_TAG="StartActivity";

    private List<Item> data;
    private ItemList itemList;

    private TextView currentItemName;
    private TextView currentItemPrice;
    private ImageView rateItemButton;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

    private LinearLayout situationLinear;
    private View situationLayout;

    private String type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        currentItemName = (TextView) findViewById(R.id.item_name);
        currentItemPrice = (TextView) findViewById(R.id.item_price);

        itemList = ItemList.get();
        data = itemList.getData();
        itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new StartAdapter(data));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        onItemChanged(data.get(0));

        findViewById(R.id.call_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_btn:
                Log.i("TYPE", currentItemPrice.getText().toString());
                String type=currentItemPrice.getText().toString();

                    popupSituation(type);
                    initSituationPopup(type);

                break;
            default:
                showUnsupportedSnackBar();
                break;
        }
    }

    private void onItemChanged(Item item) {
        currentItemName.setText(item.getName());
        currentItemPrice.setText(item.getPrice());
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        int positionInDataSet = infiniteAdapter.getRealPosition(position);
        onItemChanged(data.get(positionInDataSet));
    }

    private void showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, R.string.msg_unsupported_op, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {

        if (isCloesActivity) {
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Witness");
            alert.setMessage("Witness을 종료하겠습니까?");

            alert.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isCloesActivity = true;
                    onBackPressed();
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

    public void popupSituation(final String type){

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        situationLinear = (LinearLayout) this.findViewById(R.id.situation_layout);

        situationLayout = (View) layoutInflater.inflate(R.layout.situation_popup, situationLinear);

        int width=0;
        int height=0;

        WindowManager windowManager =(WindowManager)getSystemService(Context.WINDOW_SERVICE);


        Display display = windowManager.getDefaultDisplay();


        final PopupWindow popup=new PopupWindow(this);
        popup.setBackgroundDrawable(getDrawable(R.drawable.rounded_bright));

        width = (int) (display.getWidth() * 0.7);
        height = (int) (display.getHeight() * 0.52);

        popup.setContentView(situationLayout);

        popup.setHeight(height);
        popup.setWidth(width);
        popup.setFocusable(true);

        int offset_x=(int) (display.getWidth() * 0.15);
        int offset_y=(int) (display.getHeight() * 0.23);

        popup.showAtLocation(situationLayout, Gravity.NO_GRAVITY, offset_x, offset_y);

    }

    public void initSituationPopup(final String type){
        final ListView situations=(ListView)situationLayout.findViewById(R.id.situation);
        final SituationAdapter adapter=new SituationAdapter(getApplicationContext(), R.layout.chat_structure);
        situations.setAdapter(adapter);
        situations.setClickable(true);
        situations.setOnItemClickListener(adapter);

        switch (type){
            case "112":
                this.type=type;
                adapter.removeAll();
                adapter.addItem("1. 테러 신고");
                adapter.addItem("2. 응급 상황");
                adapter.addItem("3. 범죄 신고");
                adapter.notifyDataSetChanged();
                break;
            case "119":
                this.type=type;
                adapter.removeAll();
                adapter.addItem("1. 환자 발생");
                adapter.addItem("2. 위험 상황");
                adapter.notifyDataSetChanged();
                break;
            case "117":
                this.type=type;
                adapter.removeAll();
                adapter.addItem("1. 상담");
                adapter.addItem("2. 현장 신고");
                adapter.notifyDataSetChanged();
                break;
            default: break;
        }
    }


    class SituationAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{

        private LayoutInflater inflater;
        private ArrayList<Button> data;
        private int layout;

        public SituationAdapter(){}

        public SituationAdapter(Context context, int layout){
            this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout=layout;
            data=new ArrayList<>();
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
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=inflater.inflate(layout,parent,false);
            }

            Button item=data.get(position);


            LinearLayout chatMessageContainer = (LinearLayout)convertView.findViewById(R.id.chat_layout);
            chatMessageContainer.setPadding(10,10,10,10);
            TextView situation=(TextView)convertView.findViewById(R.id.chat_text);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
            layoutParams.setMargins(10,10,10,10);
            layoutParams.setLayoutDirection(Gravity.CENTER);

            situation.setLayoutParams(layoutParams);
            situation.setBackgroundColor(getResources().getColor(R.color.loop_background_color));
            situation.setText(item.getText());
            situation.setElevation(10);
            situation.setPadding(10,0,0,0);
            situation.setTextColor(getResources().getColor(R.color.app_background_color));
            situation.setTextSize(28);


            return convertView;
        }

        public void addItem(String comment){
            Button item=new Button(getApplicationContext());
            item.setText(comment);
            data.add(item);
        }

        public void removeAll(){
            for(int i=0; i<data.size();i++){
                data.remove(i);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(getApplicationContext(), PlayRTCActivity.class);
            Button button=(Button) parent.getItemAtPosition(position);
            intent.putExtra("type",type);
            intent.putExtra("situation", button.getText());
            startActivity(intent);

        }
    }

}
