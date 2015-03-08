package com.bouncythings.bubbletask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kevin on 3/8/15.
 */
public class Homelist_Drawer_Adapter extends BaseAdapter {

    private Context c;
    private ArrayList<String> data = new ArrayList<String>();

    public Homelist_Drawer_Adapter(Context c, ArrayList<String> data){
        this.c = c;
        this.data = data;
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public String getItem(int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.homelist_drawer_element, null);
        }

        TextView tvProjectName = (TextView) convertView.findViewById(R.id.homelist_drawer_element_projectname);

        String szProjectName = data.get(position).substring(0,1).toUpperCase() + data.get(position).substring(1);

        tvProjectName.setText(szProjectName);
        return convertView;
    }

}
