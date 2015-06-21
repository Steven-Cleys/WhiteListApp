package com.ap.steven.digipolis;

import android.app.Application;
import android.content.Context;

/**
 * Created by Steven on 3/5/2015.
 * In deze klasse zal de context bijgehouden worden
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}