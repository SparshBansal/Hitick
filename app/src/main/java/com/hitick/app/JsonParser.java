package com.hitick.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hitick.app.Data.DatabaseContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by sparsh on 25/8/16.
 */
public class JsonParser {

    // Keys to parse
    private static final String KEY_RESPONSE_USER_ID = "_id";
    private static final String KEY_RESPONSE_USERNAME = "username";
    private static final String KEY_RESPONSE_PASSWORD = "password";
    private static final String KEY_RESPONSE_EMAIL = "email";
    private static final String KEY_RESPONSE_MOBILE = "mobile";
    private static final String KEY_RESPONSE_GROUP_LIST = "groups";

    private static final String KEY_RESPONSE_GROUP_NAME = "groupName";
    private static final String KEY_RESPONSE_GROUP_PASSWORD = "groupPassword";
    private static final String KEY_RESPONSE_GROUP_ADMIN_ID = "adminId";
    private static final String KEY_RESPONSE_MEMBER_COUNT = "groupMembers";
    private static final String KEY_RESPONSE_GROUP_ID = "_id";

    private static final String KEY_RESPONSE_ERROR = "error";

    private static final String TAG = JsonParser.class.getSimpleName();


    /**
     * Helper method to parse the data and insert into the database
     */
    public static void parseInsert(JSONObject response, Context context , OnLoginListener mListener) {
        try {
            Log.d(TAG, "parseInsert: " + response.toString());

            if (response.has(KEY_RESPONSE_ERROR)) {
                mListener.onLogin(false, response.getString(KEY_RESPONSE_ERROR));
                return;
            }

            final String userId = response.getString(KEY_RESPONSE_USER_ID);

            if (userId.isEmpty()) {
                Log.d(TAG, "parseInsert: Some error occurred");
                mListener.onLogin(false , "Please try again");
                return;
            }

            Utility.saveCurrentUserId(context , userId);

            final String username = response.getString(KEY_RESPONSE_USERNAME);
            final String mobileNumber = response.getString(KEY_RESPONSE_MOBILE);
            final String email = response.getString(KEY_RESPONSE_EMAIL);
            final String password = response.getString(KEY_RESPONSE_PASSWORD);


            ContentValues userValues = new ContentValues();

            userValues.put(DatabaseContract.UserEntry.COLUMN_USERNAME, username);
            userValues.put(DatabaseContract.UserEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            userValues.put(DatabaseContract.UserEntry.COLUMN_EMAIL, email);
            userValues.put(DatabaseContract.UserEntry.COLUMN_PASSWORD, password);
            userValues.put(DatabaseContract.UserEntry.COLUMN_USER_ID, userId);

            context.getContentResolver().
                    insert(DatabaseContract.UserEntry.CONTENT_URI, userValues);

            // Parse the group data and insert in the database
            JSONArray groupListArray = response.getJSONArray(KEY_RESPONSE_GROUP_LIST);

            Vector<ContentValues> cvVector = new Vector<>();
            Vector<ContentValues> upVector = new Vector<>();


            for (int i = 0; i < groupListArray.length(); i++) {

                ContentValues groupValues = new ContentValues();
                ContentValues userParticipationValues = new ContentValues();

                JSONObject groupObject = groupListArray.getJSONObject(i);

                final String groupId = groupObject.getString(KEY_RESPONSE_GROUP_ID);
                final int groupMemberCount = groupObject.getInt(KEY_RESPONSE_MEMBER_COUNT);
                final String groupName = groupObject.getString(KEY_RESPONSE_GROUP_NAME);
                final String groupPassword = groupObject.getString(KEY_RESPONSE_GROUP_PASSWORD);
                final String groupAdminId = groupObject.getString(KEY_RESPONSE_GROUP_ADMIN_ID);


                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_ID, groupId);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_ADMIN_ID, groupAdminId);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_NAME, groupName);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_PASSWORD, groupPassword);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_MEMBERS, groupMemberCount);

                userParticipationValues.put(DatabaseContract.UserParticipationEntry.COLUMN_USER_ID, userId);
                userParticipationValues.put(DatabaseContract.UserParticipationEntry.COLUMN_GROUP_ID, groupId);
                userParticipationValues.put(DatabaseContract.UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR,
                        userId.equals(groupAdminId) ? 1 : 0);

                cvVector.add(groupValues);
                upVector.add(userParticipationValues);
            }
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                context.getContentResolver().bulkInsert(DatabaseContract.GroupEntry.CONTENT_URI, cvArray);

                ContentValues[] upArray = new ContentValues[upVector.size()];
                upVector.toArray(upArray);
                context.getContentResolver().bulkInsert(DatabaseContract.UserParticipationEntry.CONTENT_URI, upArray);
            }
            mListener.onLogin(true , "Logged in Successfully");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseInsert: " + "JSON couldn't be parsed");
            mListener.onLogin(false , "JSON couldn't be parsed");
        }
    }

    public interface OnLoginListener {
        void onLogin(boolean status, String message);
    }
}
