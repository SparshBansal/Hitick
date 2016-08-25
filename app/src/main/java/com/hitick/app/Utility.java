package com.hitick.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hitick.app.Data.DatabaseContract.*;
import com.hitick.app.Data.DatabaseContract;

import java.util.Random;

/**
 * Created by Sparsha on 12/17/2015.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    // Static method to get the Unique User Id assigned to the user by the server
    public static long getCurrentUserId(Context context){
        // Read from the default Shared Preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long userId = preferences.getLong(
                context.getString(R.string.KEY_CURRENT_USER_ID),-1);
        return userId;
    }

    //Helper method to send sms to provided number
    public static void sendSMS(Context  context , int VERIFICATION_CODE) {
        //Get Saved telephone number
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String phoneNumber = preferences.getString(context.getString(R.string.KEY_PREFERENCE_SIGNUP_MOBILE_NUMBER), "");
        if (phoneNumber.equals("")) {
            Log.d(LOG_TAG, "PHONE NUMBER NOT SAVED");
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "" + VERIFICATION_CODE, null, null);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Message not Sent");
        }
    }

    //Helper method generate a Verification Code
    public static int generateVerificationCode() {
        Random random = new Random(System.currentTimeMillis());
        return 10000 + random.nextInt(80000);
    }



    /**Helper method to save the Registration Token in Shared Preferences*/
    public static void saveGCMRegToken(Context context,String regToken) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.KEY_PREFERENCE_GCM_TOKEN),regToken);
        editor.commit();
    }

    /**Helper method to retrieve the GCM regToken*/
    public static String getGCMRegToken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String regToken = preferences.getString(context.getString(R.string.KEY_PREFERENCE_GCM_TOKEN),"");
        if (TextUtils.isEmpty(regToken))
            return null;
        else
            return regToken;
    }

}
