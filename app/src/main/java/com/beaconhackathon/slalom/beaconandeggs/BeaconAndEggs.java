package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.SafeDrawOverlay;
import com.mapbox.mapboxsdk.overlay.TilesOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.MapTile;
import com.mapbox.mapboxsdk.tileprovider.MapTileLayerBase;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.safecanvas.ISafeCanvas;
import com.mapbox.mapboxsdk.views.safecanvas.SafeDashPathEffect;
import com.mapbox.mapboxsdk.views.safecanvas.SafePaint;
import com.mapbox.mapboxsdk.views.util.Projection;

import java.util.ArrayList;

public class BeaconAndEggs extends Activity {

    private MapView mv;
    private Handler myHandler;
    private ItemizedIconOverlay itemizedIconOverlayGT;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_and_eggs);

        mv = (MapView) findViewById(R.id.mapview);
        mv.setMinZoomLevel(mv.getTileProvider().getMinimumZoomLevel());
        mv.setMaxZoomLevel(mv.getTileProvider().getMaximumZoomLevel());
        mv.setCenter(new LatLng(47.616035, -122.309646));
        mv.setZoom(12);

        mv.setUserLocationEnabled(true);

        ArrayList<Marker> Markers = new ArrayList<Marker>();
        marker = new Marker(mv, "Trader Joes", "description", new LatLng(47.616035, -122.309646));
        marker.setMarker(getApplicationContext().getResources()
                .getDrawable(R.drawable.floorplan2));
        Markers.add(marker);
        itemizedIconOverlayGT = new ItemizedIconOverlay(this, Markers,
                new ItemizedIconOverlay.OnItemGestureListener<Marker>() {

                    @Override
                    public boolean onItemSingleTapUp(int index, Marker item) {

                        return false;
                    }

                    @Override
                    public boolean onItemLongPress(int index, Marker item) {
                        return false;
                    }
                });
        /*Resources res = getApplicationContext().getResources();
        Drawable myImage = res.getDrawable(R.drawable.floorplan, getApplicationContext().getTheme());
        marker.setImage(myImage);
        marker.
        mv.addMarker(marker);*/

        //FloorPlanOverlay overlay = new FloorPlanOverlay();
        //mv.addOverlay(overlay);

        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run(){
                mv.setZoom(15);
            }
        }, 3000);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run(){
                mv.setZoom(18);
            }
        }, 6000);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run(){

                // marker
                mv.addMarker(marker);
                mv.addItemizedOverlay(itemizedIconOverlayGT);
            }
        }, 8000);
    }

    /*<String> imageUrl, <LatLngBounds> bounds, <ImageOverlay options> options? )*/


    /*var imageUrl = 'http://www.lib.utexas.edu/maps/historical/newark_nj_1922.jpg',
imageBounds = [[40.712216, -74.22655], [40.773941, -74.12544]];

L.imageOverlay(imageUrl, imageBounds).addTo(map);*/
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_and_eggs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FloorPlanOverlay extends SafeDrawOverlay {


        public FloorPlanOverlay() {
            super();
            this.setUseSafeCanvas(true);
        }

        /*@Override
        protected void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
            SafePaint circlePaint = new SafePaint();
            SafePaint textPaint = new SafePaint();
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(Color.BLACK);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(100);
            circlePaint.setPathEffect(new SafeDashPathEffect(new float[]{10, 20}, 0,0));
            circlePaint.setStrokeWidth(2);

            int width = canvas.getWidth();
            int height = canvas.getHeight();
            RectF scrollableAreaLimit = mapView.getScrollableAreaLimit();
            float height1 = scrollableAreaLimit.height();
            float width1 = scrollableAreaLimit.width();
            int padding = 200;
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, width - padding, circlePaint);

            canvas.drawText("Hello overlay!!!!!!!!!!!!!!", width1 / 2, height1 / 2, textPaint);
            canvas.drawText("Hello overlay!!!!!!!!!!!!!!", 400, 400, textPaint);
        }*/

        @Override
        protected void drawSafe(ISafeCanvas canvas, MapView mapView, boolean b) {

            SafePaint circlePaint = new SafePaint();
            SafePaint textPaint = new SafePaint();
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setColor(Color.BLACK);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(100);
            circlePaint.setPathEffect(new SafeDashPathEffect(new float[]{10, 20}, 0,0));
            circlePaint.setStrokeWidth(2);

            int width = canvas.getWidth();
            int height = canvas.getHeight();
            RectF scrollableAreaLimit = mapView.getScrollableAreaLimit();
            float height1 = scrollableAreaLimit.height();
            float width1 = scrollableAreaLimit.width();
            int padding = 200;
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, width - padding, circlePaint);

            canvas.drawText("Hello overlay!!!!!!!!!!!!!!", width1 / 2, height1 / 2, textPaint);
            canvas.drawText("Hello overlay!!!!!!!!!!!!!!", 400, 400, textPaint);

            /*Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.floorplan);
            canvas.drawBitmap(bmp, 0, 0, null);*/
            /*Resources res = getApplicationContext().getResources();
            Drawable myImage = res.getDrawable(R.drawable.floorplan);
            myImage.setBounds(0, 0, myImage.getIntrinsicWidth(), myImage.getIntrinsicHeight());
            myImage.draw(canvas);*/
            //canvas.dr.drawPicture(myImage);
        }
    }
}
