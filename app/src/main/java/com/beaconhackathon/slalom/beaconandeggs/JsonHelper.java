package com.beaconhackathon.slalom.beaconandeggs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.beaconhackathon.slalom.beaconandeggs.Models.Categories;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by lucy on 10/25/15.
 */
public class JsonHelper {

    public static Categories convertToJson(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(
                jsonString,
                Categories.class
        );
    }

    public static String getJsonString(Context context) {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try (InputStream is = context.getResources().openRawResource(R.raw.data)) {
            Reader reader = new BufferedReader(new InputStreamReader(is));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
        return writer.toString();
    }
}
