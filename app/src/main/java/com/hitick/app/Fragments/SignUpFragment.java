package com.hitick.app.Fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.hitick.app.Broadcast_Receivers.SmsReceiver;
import com.hitick.app.Data.DatabaseContract;
import com.hitick.app.Data.DatabaseHelper;
import com.hitick.app.Network.VolleySingleton;
import com.hitick.app.QuickstartPreferences;
import com.hitick.app.R;
import com.hitick.app.Services.HitickGCMRegistrationService;
import com.hitick.app.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Sparsha on 12/7/2015.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {


    private static final String LOG_TAG = SignUpFragment.class.getSimpleName();
    private static Button bSignUp;
    private static EditText etFirstName;
    private static EditText etLastName;
    private static EditText etEmail;
    private static EditText etMobile;
    private static EditText etPassword;
    private static Toolbar toolbar;
    private static SmsReceiver mSmsReceiver;
    private static IntentFilter intentFilter;

    private GCMRegistrationReceiver mRegistrationBroadcastReceiver;
    private static final String REQUEST_TAG = "SIGN_UP_TAG";

    private static final String KEY_RESPONSE_PERSON_OBJECT = "person";
    private static final String KEY_RESPONSE_USER_ID = "id";
    private static final String KEY_RESPONSE_FIRST_NAME = "firstName";
    private static final String KEY_RESPONSE_LAST_NAME = "lastName";
    private static final String KEY_RESPONSE_PASSWORD = "password";
    private static final String KEY_RESPONSE_EMAIL = "email";
    private static final String KEY_RESPONSE_MOBILE = "mobileNumber";
    private static final String KEY_RESPONSE_GROUP_LIST = "listOfHisGroups";

    private static final String KEY_RESPONSE_GROUP_NAME = "groupName";
    private static final String KEY_RESPONSE_GROUP_PASSWORD = "groupPassword";
    private static final String KEY_RESPONSE_GROUP_ADMIN_ID = "refAdminId";
    private static final String KEY_RESPONSE_MEMBER_COUNT = "memberCount";
    private static final String KEY_RESPONSE_GROUP_ID = "id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Broadcast receiver to listen for GCM registration token
        mRegistrationBroadcastReceiver = new GCMRegistrationReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the rootView and use it to initialize the instance variables
        View rootView = inflater.inflate(R.layout.fragment_sign_up_details, container, false);

        //Find the button and set onClick Listener
        bSignUp = (Button) rootView.findViewById(R.id.bSignUp);
        etFirstName = (EditText) rootView.findViewById(R.id.etFirstName);
        etLastName = (EditText) rootView.findViewById(R.id.etLastName);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etMobile = (EditText) rootView.findViewById(R.id.etMobile);
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);

        mSmsReceiver = new SmsReceiver(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        //Create and register the receiver
        getActivity().registerReceiver(mSmsReceiver, intentFilter);

        bSignUp.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager
                .getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE)
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bSignUp:
                signUp();
                break;
        }
    }


    void signUp() {

        //Save the user data in Shared Preferences for now
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString(getString(R.string.KEY_PREFERENCE_FIRST_NAME), etFirstName.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_LAST_NAME), etLastName.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_EMAIL_ID), etEmail.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_MOBILE_NUMBER), etMobile.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_PASSWORD), etPassword.getText().toString());
        editor.commit();

        // Start the service for obtaining a registration token for the device
        if (Utility.checkPlayServices(getActivity())) {
            // Start intent service to register with GCM
            Intent intent = new Intent(getActivity(), HitickGCMRegistrationService.class);
            getActivity().startService(intent);
        }

    }

    public class GCMRegistrationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            //Send the user data to the Server using Volley Framework
            RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
            JsonObjectRequest mSignUpRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    preferences.getString(getString(R.string.URL_SIGN_UP), ""),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            /**Parse the response from the server and insert the user
                             * details into the database*/
                            parseInsert(response, getContext());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            /**Some error occurred , notify the user to try again */

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Put the JSON data into a HashMap and send it to the server
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_FIRST_NAME),
                            preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_FIRST_NAME), null));
                    paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_LAST_NAME),
                            preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_LAST_NAME), null));
                    paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_EMAIL),
                            preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_EMAIL_ID), null));
                    paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_MOBILE),
                            preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_MOBILE_NUMBER), null));
                    paramsMap.put(getString(R.string.KEY_SIGNUP_JSON__PASSWORD),
                            preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_PASSWORD), null));
                    paramsMap.put(getString(R.string.KEY_SIGNIN_JSON_GCM_TOKEN),
                            Utility.getGCMRegToken(getContext()));
                    return paramsMap;
                }
            };
            mRequestQueue.add(mSignUpRequest);
        }
    }


    /**
     * Helper method to parse the data and insert into the database
     */
    public void parseInsert(JSONObject response, Context context) {
        try {
            JSONObject personObject = response.getJSONObject(KEY_RESPONSE_PERSON_OBJECT);
            final long userId = personObject.getLong(KEY_RESPONSE_USER_ID);
            final String firstName = personObject.getString(KEY_RESPONSE_FIRST_NAME);
            final String lastName = personObject.getString(KEY_RESPONSE_LAST_NAME);
            final String mobileNumber = personObject.getString(KEY_RESPONSE_MOBILE);
            final String email = personObject.getString(KEY_RESPONSE_EMAIL);
            final String password = personObject.getString(KEY_RESPONSE_PASSWORD);
            final String userParticipationTable;
            if (Utility.checkUserInDatabase(userId, getActivity())) {
                userParticipationTable =
                        Utility.getUserGroupParticipationTable(getActivity(), userId);
            } else {
                userParticipationTable = "UPT_" + userId;

                // Create a fresh User-Participation-Table using the helper method from our
                // DatabaseHelper
                DatabaseHelper mHelper = new DatabaseHelper(context);
                mHelper.addGroupParticipationTable(userParticipationTable);

            }

            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.UserEntry.COLUMN_FIRST_NAME, firstName);
            userValues.put(DatabaseContract.UserEntry.COLUMN_LAST_NAME, lastName);
            userValues.put(DatabaseContract.UserEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            userValues.put(DatabaseContract.UserEntry.COLUMN_EMAIL, email);
            userValues.put(DatabaseContract.UserEntry.COLUMN_PASSWORD, password);
            userValues.put(DatabaseContract.UserEntry.COLUMN_USER_GROUP_PARTICIPATION_TABLE, userParticipationTable);
            userValues.put(DatabaseContract.UserEntry.COLUMN_USER_ID, userId);

            getContext().getContentResolver().
                    insert(DatabaseContract.UserEntry.CONTENT_URI, userValues);

            // Parse the group data and insert in the database
            JSONArray groupListArray = response.getJSONArray(KEY_RESPONSE_GROUP_LIST);

            Vector<ContentValues> cvVector = new Vector<>();
            ContentValues groupValues = new ContentValues();
            for (int i = 0; i < groupListArray.length(); i++) {
                JSONObject groupObject = groupListArray.getJSONObject(i);
                final long groupId = groupObject.getLong(KEY_RESPONSE_GROUP_ID);
                final int groupMemberCount = groupObject.getInt(KEY_RESPONSE_MEMBER_COUNT);
                final String groupName = groupObject.getString(KEY_RESPONSE_GROUP_NAME);
                final String groupPassword = groupObject.getString(KEY_RESPONSE_GROUP_PASSWORD);
                final long groupAdminId = groupObject.getLong(KEY_RESPONSE_GROUP_ADMIN_ID);
                final String groupDetailsTable;
                if (Utility.checkGroupInDatabase(groupId, context)) {
                    groupDetailsTable = Utility.getGroupDetailsTable(groupId, context);
                } else {
                    groupDetailsTable = "GDT_" + groupId;
                    // Create a group details table from the Helper method from the Helper class
                    DatabaseHelper mHelper = new DatabaseHelper(context);
                    mHelper.addGroupDetailsTable(groupDetailsTable);
                }

                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_ID, groupId);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_ADMIN_ID, groupAdminId);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_NAME, groupName);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_PASSWORD, groupPassword);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_MEMBERS, groupMemberCount);
                groupValues.put(DatabaseContract.GroupEntry.COLUMN_GROUP_DETAILS, groupDetailsTable);

                cvVector.add(groupValues);
            }
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                context.getContentResolver().bulkInsert(DatabaseContract.GroupEntry.CONTENT_URI, cvArray);

                // Now insert in the user participation Table
                cvVector.clear();
                ContentValues userParticipationValues = new ContentValues();
                for (ContentValues contentValues : cvArray) {
                    userParticipationValues.put(
                            DatabaseContract.UserParticipationEntry.COLUMN_GROUP_KEY,
                            contentValues.getAsLong(DatabaseContract.GroupEntry.COLUMN_GROUP_ID));
                    final long adminId = contentValues.getAsLong(DatabaseContract.GroupEntry.COLUMN_GROUP_ADMIN_ID);
                    userParticipationValues.put(
                            DatabaseContract.UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR,
                            adminId == userId ? 1 : 0
                    );
                    cvVector.add(userParticipationValues);
                }
                if (cvVector.size() > 0) {
                    cvArray = new ContentValues[cvVector.size()];
                    cvVector.toArray(cvArray);
                    Uri uri = DatabaseContract.UserParticipationEntry.buildContentUri(userParticipationTable);
                    context.getContentResolver().bulkInsert(uri, cvArray);
                }
                cvVector.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Helper Method to validate the mobile number entered by the user
    private void validateMobileNumber() {
        final int VERIFICATION_CODE = Utility.generateVerificationCode();
        Utility.sendSMS(getActivity(), VERIFICATION_CODE);
    }

}
