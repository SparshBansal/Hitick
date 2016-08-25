package com.hitick.app.Fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hitick.app.Broadcast_Receivers.SmsReceiver;
import com.hitick.app.Network.VolleySingleton;
import com.hitick.app.QuickstartPreferences;
import com.hitick.app.R;
import com.hitick.app.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.hitick.app.Data.DatabaseContract.GroupEntry;
import static com.hitick.app.Data.DatabaseContract.UserEntry;
import static com.hitick.app.Data.DatabaseContract.UserParticipationEntry;

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

    private static final String REQUEST_TAG = "SIGN_UP_TAG";

    private static final String KEY_RESPONSE_PERSON_OBJECT = "person";
    private static final String KEY_RESPONSE_USER_ID = "id";
    private static final String KEY_RESPONSE_USERNAME = "username";
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the rootView and use it to initialize the instance variables
        View rootView = inflater.inflate(R.layout.fragment_sign_up_details, container, false);

        //Find the button and set onClick Listener
        bSignUp = (Button) rootView.findViewById(R.id.b_sign_up);
        etFirstName = (EditText) rootView.findViewById(R.id.et_first_name);
        etLastName = (EditText) rootView.findViewById(R.id.et_last_name);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);
        etMobile = (EditText) rootView.findViewById(R.id.et_mobile);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);

        mSmsReceiver = new SmsReceiver(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

        //Create and register the receiver
        //getActivity().registerReceiver(mSmsReceiver, intentFilter);

        bSignUp.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_sign_up:
                signUp();
                break;
        }
    }


    private void signUp() {
        saveUserData();
        // Start the service for obtaining a registration token for the device
        final String registrationToken = FirebaseInstanceId.getInstance().getToken();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Send the user data to the Server using Volley Framework
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        String urlSignUp = getActivity().getString(R.string.URL_SIGN_UP);

        Log.d(LOG_TAG, urlSignUp);
        JsonObjectRequest mSignUpRequest = new JsonObjectRequest(
                Request.Method.POST,
                urlSignUp,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /**Parse the response from the server and insert the user
                         * details into the database*/
                        Log.d(LOG_TAG, "Response Received");
                        parseInsert(response, getContext());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /**Some error occurred , notify the user to try again */
                        Log.d(LOG_TAG, error.getLocalizedMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Put the JSON data into a HashMap and send it to the server
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_FIRST_NAME),
                        preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_SIGNUP_FIRST_NAME), null));
                paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_LAST_NAME),
                        preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_SIGNUP_LAST_NAME), null));
                paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_EMAIL),
                        preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_SIGNUP_EMAIL_ID), null));
                paramsMap.put(getString(R.string.KEY_SIGNUP_JSON_MOBILE),
                        preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_SIGNUP_MOBILE_NUMBER), null));
                paramsMap.put(getString(R.string.KEY_SIGNUP_JSON__PASSWORD),
                        preferences.getString(getContext().getString(R.string.KEY_PREFERENCE_SIGNUP_PASSWORD), null));
                paramsMap.put("institution", "D.T.U");
                paramsMap.put(getString(R.string.KEY_SIGNIN_JSON_GCM_TOKEN), registrationToken);
                return paramsMap;
            }
        };
        mRequestQueue.add(mSignUpRequest);
    }

    private void saveUserData() {
        //Save the user data in Shared Preferences for now
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString(getString(R.string.KEY_PREFERENCE_SIGNUP_FIRST_NAME), etFirstName.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_SIGNUP_LAST_NAME), etLastName.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_SIGNUP_EMAIL_ID), etEmail.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_SIGNUP_MOBILE_NUMBER), etMobile.getText().toString());
        editor.putString(getString(R.string.KEY_PREFERENCE_SIGNUP_PASSWORD), etPassword.getText().toString());
        editor.commit();
    }


    /**
     * Helper method to parse the data and insert into the database
     */
    public void parseInsert(JSONObject response, Context context) {
        try {
            Log.d(LOG_TAG, "parseInsert: " + response.toString());
            JSONObject personObject = response.getJSONObject(KEY_RESPONSE_PERSON_OBJECT);
            final long userId = personObject.getLong(KEY_RESPONSE_USER_ID);
            if (userId < 0) {
                Log.d(LOG_TAG, "parseInsert: Some error occurred");
                return;
            }
            final String username = personObject.getString(KEY_RESPONSE_USERNAME);
            final String mobileNumber = personObject.getString(KEY_RESPONSE_MOBILE);
            final String email = personObject.getString(KEY_RESPONSE_EMAIL);
            final String password = personObject.getString(KEY_RESPONSE_PASSWORD);


            ContentValues userValues = new ContentValues();
            userValues.put(UserEntry.COLUMN_USERNAME , username);
            userValues.put(UserEntry.COLUMN_MOBILE_NUMBER, mobileNumber);
            userValues.put(UserEntry.COLUMN_EMAIL, email);
            userValues.put(UserEntry.COLUMN_PASSWORD, password);
            userValues.put(UserEntry.COLUMN_USER_ID, userId);

            getContext().getContentResolver().
                    insert(UserEntry.CONTENT_URI, userValues);

            // Parse the group data and insert in the database
            JSONArray groupListArray = response.getJSONArray(KEY_RESPONSE_GROUP_LIST);

            Vector<ContentValues> cvVector = new Vector<>();
            Vector<ContentValues> upVector = new Vector<>();

            ContentValues groupValues = new ContentValues();
            ContentValues userParticipationValues = new ContentValues();

            for (int i = 0; i < groupListArray.length(); i++) {
                JSONObject groupObject = groupListArray.getJSONObject(i);
                final long groupId = groupObject.getLong(KEY_RESPONSE_GROUP_ID);
                final int groupMemberCount = groupObject.getInt(KEY_RESPONSE_MEMBER_COUNT);
                final String groupName = groupObject.getString(KEY_RESPONSE_GROUP_NAME);
                final String groupPassword = groupObject.getString(KEY_RESPONSE_GROUP_PASSWORD);
                final long groupAdminId = groupObject.getLong(KEY_RESPONSE_GROUP_ADMIN_ID);


                groupValues.put(GroupEntry.COLUMN_GROUP_ID, groupId);
                groupValues.put(GroupEntry.COLUMN_GROUP_ADMIN_ID, groupAdminId);
                groupValues.put(GroupEntry.COLUMN_GROUP_NAME, groupName);
                groupValues.put(GroupEntry.COLUMN_GROUP_PASSWORD, groupPassword);
                groupValues.put(GroupEntry.COLUMN_GROUP_MEMBERS, groupMemberCount);


                userParticipationValues.put(UserParticipationEntry.COLUMN_USER_ID, userId);
                userParticipationValues.put(UserParticipationEntry.COLUMN_GROUP_ID, groupId);
                userParticipationValues.put(UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR,
                        userId == groupAdminId ? 1 : 0);

                cvVector.add(groupValues);
                upVector.add(userParticipationValues);
            }
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                context.getContentResolver().bulkInsert(GroupEntry.CONTENT_URI, cvArray);

                ContentValues[] upArray = new ContentValues[upVector.size()];
                upVector.toArray(upArray);
                context.getContentResolver().bulkInsert(UserParticipationEntry.CONTENT_URI, upArray);
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
