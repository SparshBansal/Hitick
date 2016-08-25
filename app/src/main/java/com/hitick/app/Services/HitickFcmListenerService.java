package com.hitick.app.Services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sparsh on 24/8/16.
 */
public class HitickFcmListenerService extends FirebaseMessagingService {


    private static final String TAG = HitickFcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getMessageId());
    }
}
