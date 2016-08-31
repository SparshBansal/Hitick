package com.hitick.app.Services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hitick.app.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sparsh on 24/8/16.
 */
public class HitickFcmListenerService extends FirebaseMessagingService {


    private static final String TAG = HitickFcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("poll"));
            JSONObject poll = new JSONObject(remoteMessage.getData().get("poll"));
            JsonParser.parseInsertPoll(poll , getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
