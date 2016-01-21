package com.hitick.app.Services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Sparsha on 1/21/2016.
 */
public class HitickService extends IntentService{

    private static final String SERVICE_NAME = "Hitick Service";

    public HitickService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
