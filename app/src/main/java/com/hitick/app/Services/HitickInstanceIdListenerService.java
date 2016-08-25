package com.hitick.app.Services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hitick.app.QuickstartPreferences;

/**
 * Created by Sparsha on 1/21/2016.
 */
public class HitickInstanceIdListenerService extends FirebaseInstanceIdService{

    private static final String SERVICE_NAME = "Hitick Service";
    private static final String TAG = HitickInstanceIdListenerService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Token refreshed
        final String registrationToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: " + registrationToken);
    }
}
