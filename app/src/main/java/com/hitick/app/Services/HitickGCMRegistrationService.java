package com.hitick.app.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.hitick.app.QuickstartPreferences;
import com.hitick.app.R;
import com.hitick.app.Utility;

import java.io.IOException;

/**
 * Created by Sparsha on 1/22/2016.
 */
public class HitickGCMRegistrationService extends IntentService {

    // The name of the service which registers our application on GCM
    private static final String SERVICE_NAME = "Hitick_Registration_Service";
    private static final String LOG_TAG = HitickGCMRegistrationService.class.getSimpleName();

    public HitickGCMRegistrationService() {
        super(SERVICE_NAME);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        /**We register our application to GCM here in this service, This service will be launched
         * at the time of SignIn and SignUp from our mobile application*/
        try {
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.GCM_SENDER_ID),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Utility.saveGCMRegToken(getApplicationContext(),token);
            Log.d(LOG_TAG , token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
