package com.beaconhackathon.slalom.beaconandeggs.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beaconhackathon.slalom.beaconandeggs.R;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

/**
 * Created by ainsleyh on 10/29/2015.
 */
public class PuchasedListViewAdapter extends BaseSwipeAdapter {
    private Context mContext;
    private List<Item> _gc;

    public PuchasedListViewAdapter(Context mContext, List<Item> gc) {
        this.mContext = mContext;
        _gc = gc;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, final ViewGroup parent) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView t = (TextView)convertView.findViewById(R.id.text_data);
        t.setText(_gc.get(position).name);
        TextView t2 = (TextView)convertView.findViewById(R.id.text_data2);
        int quantity = _gc.get(position).quantity;
        if (quantity <= 0)
            quantity = 1;
        t2.setText("x" + quantity);
    }

    @Override
    public int getCount() {
        return _gc.size();
    }

    @Override
    public Object getItem(int position) {
        return _gc.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
