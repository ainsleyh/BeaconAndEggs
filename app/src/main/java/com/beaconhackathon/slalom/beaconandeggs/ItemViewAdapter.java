package com.beaconhackathon.slalom.beaconandeggs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beaconhackathon.slalom.beaconandeggs.Models.Category;
import com.beaconhackathon.slalom.beaconandeggs.Models.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucy on 10/14/15.
 */
public class ItemViewAdapter extends BaseAdapter {
    private List<Category> itemList;
    LayoutInflater inflater;
    Context context;


    public ItemViewAdapter(Context context, List<Category> myList) {
        this.itemList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Category getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_search, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Category currentCategory = getItem(position);

        mViewHolder.name.setText(currentCategory.name);

        return convertView;
    }

    private class MyViewHolder {
        TextView name;

        public MyViewHolder(View item) {
            name = (TextView) item.findViewById(R.id.item);
        }
    }
}