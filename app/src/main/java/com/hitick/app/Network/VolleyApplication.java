package com.hitick.app.Network;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sparsha on 10/20/2015.
 */
public class VolleyApplication extends Application {
    private static VolleyApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static VolleyApplication getInstance(){
        return mApplication;
    }

    public static Context getContext(){
        return mApplication.getApplicationContext();
    }
}
