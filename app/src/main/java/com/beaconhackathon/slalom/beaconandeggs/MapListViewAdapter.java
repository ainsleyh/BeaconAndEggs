package com.beaconhackathon.slalom.beaconandeggs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconhackathon.slalom.beaconandeggs.Models.Item;
import com.beaconhackathon.slalom.beaconandeggs.Models.State;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

/**
 * ListViewAdapter for the Map Locator view
 *
 * Created by ainsleyherndon on 10/25/15.
 */
public class MapListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Item> _items;

    public MapListViewAdapter(Context mContext, List<Item> gc) {
        this.mContext = mContext;
        _items = gc;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeDone;
    }

    @Override
    public View generateView(final int position, final ViewGroup parent) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.swipe_done, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));

        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {

                // mark as checked
                Item selectedItem = _items.get(position);
                selectedItem.state = State.Checked;

                notifyDataSetChanged();
                closeAllItems();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView t = (TextView)convertView.findViewById(R.id.text_data);
        t.setText(_items.get(position).name);
    }

    @Override
    public int getCount() {
        return _items.size();
    }

    @Override
    public Object getItem(int position) {
        return _items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
