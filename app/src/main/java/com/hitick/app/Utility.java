package com.hitick.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.preference.PreferenceManager;

/**
 * Created by Sparsha on 12/17/2015.
 */
public class Utility {

    /* Static method to the Table Name of the user group Participation Table ,
       User Participation Table Name will be stored at the time of login in Shared Preferences */
    public static String getUserGroupParticipationTable(Context context) {
        // Read from the default Shared Preferences
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        final String userGroupParticipationTable = preferences.getString(
                context.getString(R.string.KEY_CURRENT_USER_GROUP_PARTCIPATION_TABLE),
                null
        );

        return userGroupParticipationTable;
    }

    // Static method to get the Unique User Id assigned to the user by the server
    public static String getUserId(Context context){
        // Read from the default Shared Preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String userId = preferences.getString(
                context.getString(R.string.KEY_CURRENT_USER_ID),null);
        return userId;
    }
}
