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

    /* Static method to the Table Name of the user group Participation Table ,
       User Participation Table Name will be stored at the time of login in Shared Preferences */
    public static String getUserGroupParticipationTable(Context context) {

        // Build the uri using the stored User Id
        final Uri contentUri = DatabaseContract.UserEntry.buildUsersUri(getUserId(context));

        // Query the database and extract the user_group_participation_table_name
        Cursor retCursor = context.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null
        );

        final String userGroupParticipationTable = retCursor.
                getString(retCursor.getColumnIndex(UserEntry.COLUMN_USER_GROUP_PARTICIPATION_TABLE));
        return userGroupParticipationTable;
    }

    // Static method to get the Unique User Id assigned to the user by the server
    public static long getUserId(Context context){
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
        String phoneNumber = preferences.getString(context.getString(R.string.KEY_PREFERENCE_MOBILE_NUMBER), "");
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


    // Helper method to check for required version of play services

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Context context) {
        int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {

            } else {
                Log.i(LOG_TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
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
