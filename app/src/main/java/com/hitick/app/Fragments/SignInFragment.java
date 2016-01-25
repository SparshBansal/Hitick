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
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.Vector;

import com.hitick.app.Data.DatabaseContract.*;

public class SignInFragment extends Fragment {

    private static final String LOG_TAG = SignInFragment.class.getSimpleName();
    private static final String REQUEST_TAG = "SIGN_IN_TAG";
    private static EditText etMobile;
    private static EditText etPassword;
    private static Button bSignIn;
    private static int MOBILE_NUMBER_LENGTH = 10;
    private GCMRegistrationReceiver mRegistrationBroadcastReceiver;


    private static final String KEY_RESPONSE_USER_ID = "id";
    private static final String KEY_RESPONSE_FIRST_NAME = "firstName";
    private static final String KEY_RESPONSE_LAST_NAME = "lastName";
    private static final String KEY_RESPONSE_PASSWORD = "password";
    private static final String KEY_RESPONSE_EMAIL = "email";
    private static final String KEY_RESPONSE_MOBILE = "mobileNumber";
    private static final String KEY_RESPONSE_GROUP_LIST = "listOfHisGroups";
    private static final String KEY_RESPONSE_PERSON_OBJECT = "person";

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
        View rootView = inflater.inflate(R.layout.fragment_sign_in_details, container, false);

        //Initialize the instance variables
        etMobile = (EditText) rootView.findViewById(R.id.etMobile);
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);
        bSignIn = (Button) rootView.findViewById(R.id.bSignIn);

        //Setup the onClick Listener and Post the data to the server
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do the basic checks on data
                String mobileNumber = etMobile.getText().toString();
                String password = etPassword.getText().toString();

                if (mobileNumber.length() != MOBILE_NUMBER_LENGTH) {
                    //Make a toast to notify the user for wrong mobile number
                    Toast.makeText(getActivity(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the details in Shared Preferences
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString(getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_MOBILE_NUMBER), mobileNumber);
                editor.putString(getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_PASSWORD), password);
                editor.apply();

                /** Begin the signIn procedure , First start the GCM registration service ,
                 * And when the GCM regToken is obtained, use the broadcast receiver to send the
                 * signIn request to the server*/
                signIn();
            }
        });
        return rootView;
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
    public void onPause() {
        super.onPause();
        LocalBroadcastManager
                .getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cancel all the requests from the request queue
        VolleySingleton.getInstance().getRequestQueue().cancelAll(REQUEST_TAG);
    }

    //Helper method to SignIn to the server
    boolean signIn() {

        if (Utility.checkPlayServices(getActivity())) {
            // Start intent service to register with GCM
            Intent intent = new Intent(getActivity(), HitickGCMRegistrationService.class);
            getActivity().startService(intent);
        }
        return false;
    }

    public class GCMRegistrationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

            RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            final String mobileNumber = preferences.getString(
                    context.getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_MOBILE_NUMBER), "");
            final String password = preferences.getString(
                    context.getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_PASSWORD), "");

            if (TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(password))
                return;

            JsonObjectRequest mSignInRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getContext().getString(R.string.URL_SIGN_IN),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(LOG_TAG, "Response Received");
                            // Parse the data and then insert in the database
                            parseInsert(response,context);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Some Error Occurred
                            //Notify the User
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Please try again",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Put the parameters in the hashmap
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(getContext().getString(R.string.KEY_SIGNIN_JSON_MOBILE), mobileNumber);
                    paramsMap.put(getContext().getString(R.string.KEY_SIGNIN_JSON_PASSWORD), password);
                    paramsMap.put(getContext().getString(R.string.KEY_SIGNIN_JSON_GCM_TOKEN),
                            Utility.getGCMRegToken(getActivity()));
                    return paramsMap;
                }
            };
            mSignInRequest.setTag(REQUEST_TAG);
            mRequestQueue.add(mSignInRequest);
        }
    }

    /** Helper method to parse the data and insert into the database */
    public void parseInsert(JSONObject response,Context context) {
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
            if (cvVector.size()>0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                context.getContentResolver().bulkInsert(DatabaseContract.GroupEntry.CONTENT_URI,cvArray);

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
                if (cvVector.size()>0) {
                    cvArray = new ContentValues[cvVector.size()];
                    cvVector.toArray(cvArray);
                    Uri uri = DatabaseContract.UserParticipationEntry.buildContentUri(userParticipationTable);
                    context.getContentResolver().bulkInsert(uri,cvArray);
                }
                cvVector.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
