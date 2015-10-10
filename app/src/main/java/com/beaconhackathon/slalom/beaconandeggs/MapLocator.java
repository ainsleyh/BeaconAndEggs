package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ainsleyherndon on 10/9/15.
 */
public class MapLocator extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
