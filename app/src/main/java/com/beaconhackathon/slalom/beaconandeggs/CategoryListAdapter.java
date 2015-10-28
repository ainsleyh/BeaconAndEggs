package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beaconhackathon.slalom.beaconandeggs.Models.Recipe;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ianb on 10/14/2015.
 */
public class CategoryListAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private LayoutInflater mInflater;
    private int mLayoutResourceId;

    public ArrayList<String> groupElements;
    public ArrayList<HashMap<String,String>> childElements;
    public ArrayList<ArrayList<String>> ingredientLists;


    public CategoryListAdapter(Activity context, int layoutResourceId, ArrayList<Recipe> recipes) {
        super();
        this.mContext = context;
        this.mInflater = mContext.getLayoutInflater();
        this.mLayoutResourceId = layoutResourceId;
        this.childElements = new ArrayList<HashMap<String, String>>();
        this.groupElements = new ArrayList<String>();
        this.ingredientLists = new ArrayList<ArrayList<String>>();
        for(Recipe recipe: recipes){
            add(recipe);
        }

    }

    public void add(Recipe recipe) {
        groupElements.add(recipe.name);
        childElements.add(groupElements.indexOf(recipe.name), getChildMapFromRecipe(recipe));
        ingredientLists.add(groupElements.indexOf(recipe.name),recipe.items);
        notifyDataSetChanged();
    }

    public void remove(Recipe recipe) {
        if(groupElements.contains(recipe.name))
        {
            groupElements.remove(groupElements.indexOf(recipe.name));
            childElements.remove(groupElements.indexOf(recipe.name));
            ingredientLists.add(groupElements.indexOf(recipe.name), recipe.items);

        }
        this.notifyDataSetChanged();
    }

    public void clear()
    {
        groupElements.clear();
        childElements.clear();
        ingredientLists.clear();
        this.notifyDataSetChanged();
    }

    private HashMap<String,String> getChildMapFromRecipe(Recipe recipe){
        HashMap<String,String> childMap = new HashMap<>();
        //ArrayList<String> recipeAttributes = new ArrayList<String>();
        childMap.put("imageURL", recipe.imageURL);
        childMap.put("totalMinutes",""+recipe.totalMinutes);
        childMap.put("sourceURL", recipe.sourceURL);
        childMap.put("sourceDisplayName",recipe.sourceDisplayName);
        childMap.put("rating", "" + recipe.rating);
        return childMap;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childElements.get(childPosition);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final HashMap<String,String> childMap = childElements.get(groupPosition);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recipe_list_child_row, null);
        }
        String imageURL = childMap.get("imageURL");
        ImageView imageView = (ImageView)convertView.findViewById(R.id.recipeListRowImage);
        imageView.setImageResource(R.drawable.logo);
        imageView.setContentDescription("image not available");
        imageView.setTag("");
        if(imageURL != null && !imageView.getTag().equals(imageURL)) {
            new DownloadThumbnailTask(imageView)
                    .execute(childMap.get("imageURL"));
            imageView.setTag(imageURL);
        }
        ((TextView) convertView.findViewById(R.id.recipeListRowTime)).setText(childMap.get("totalMinutes"));
        ((TextView) convertView.findViewById(R.id.recipeListRowSourceName)).setText(childMap.get("sourceDisplayName"));
        convertView.findViewById(R.id.recipeListRowSourceName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(childMap.get("sourceURL")));
                mContext.startActivity(intent);
            }
        });
        ((RatingBar) convertView.findViewById(R.id.recipeListRowRating)).setRating((float) Double.parseDouble(childMap.get("rating")));

        LinearLayout recipeIngredientListView = (LinearLayout) convertView.findViewById(R.id.recipeIngredientListView);
        recipeIngredientListView.removeAllViewsInLayout();
        LayoutInflater inflater=mContext.getLayoutInflater();
        for(String ingredient:ingredientLists.get(groupPosition))
        {

            View rowView=inflater.inflate(R.layout.recipe_ingredient_list_item, (ViewGroup)convertView.findViewById(R.id.recipeIngredientListView),false);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.recipe_ingredient_list_row_text);

            ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.remove_ingredient_row);

            txtTitle.setText(ingredient);
            recipeIngredientListView.addView(rowView,ingredientLists.indexOf(ingredient));
        }

    return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupElements.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.groupElements.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recipe_list_group_row, null);
        }
        ((TextView) convertView).setText(groupElements.get(groupPosition));
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    //class for async download of thumbnails for recipe detail
    private class DownloadThumbnailTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadThumbnailTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setContentDescription("recipe image");
        }
    }


}
