package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ianb on 10/14/2015.
 */
public class IngredientListAdapter extends ArrayAdapter<String> {

    private Activity mContext;
//    private int mLayoutResourceId;
    private String[] mIngredients;

    public IngredientListAdapter(Activity context, ArrayList<String> ingredients) {
        super(context, -1, ingredients);
        this.mContext = context;
        this.mIngredients = ingredients.toArray(new String[0]);
//        this.mLayoutResourceId = layoutResourceId;

    }


    @Override
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.ingredient_list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.ingredient_list_row_text);
        ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.remove_ingredient_row);

        txtTitle.setText(mIngredients[position]);

        imageButton.setBackgroundResource(android.R.drawable.ic_delete);

        return rowView;

    };
}