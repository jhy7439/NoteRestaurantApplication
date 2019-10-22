package com.nova.noterestaurantapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nova.noterestaurantapplication.Item.MainItem;
import com.nova.noterestaurantapplication.R;

import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<MainItem> mainItemlist;
    private int layout;

    public MainAdapter(Context context, int layout , ArrayList<MainItem> list){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mainItemlist = list;
        this.layout = layout;
    }

    //리스트 안 Item의 개수를 센다
    @Override
    public int getCount() {
        return mainItemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mainItemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);
        }

        MainItem mainItem = mainItemlist.get(position);




        ImageView foodPhoto = (ImageView) convertView.findViewById(R.id.main_food_iv);
        foodPhoto.setImageResource(mainItem.getFoodPhoto());

        TextView title =(TextView) convertView.findViewById(R.id.main_title_tv);
        title.setText(mainItem.getTitle());

        TextView subTitle = (TextView) convertView.findViewById(R.id.main_subTitle_tv);
        subTitle.setText(mainItem.getSubTitle());

        TextView content = (TextView) convertView.findViewById(R.id.main_content_tv);
        content.setText(mainItem.getContent());

//        TextView phone = (TextView) convertView.findViewById(R.id.main_phone_tv);
//        phone.setText(mainItem.getPhone());
//
//        TextView address = (TextView) convertView.findViewById(R.id.main_address_tv);
//        address.setText(mainItem.getAddress());


        return convertView;
    }
}
