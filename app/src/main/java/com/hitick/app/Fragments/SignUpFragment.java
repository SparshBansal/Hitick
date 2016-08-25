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
import com.hitick.app.JsonParser;
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
                        JsonParser.parseInsert(response, getContext());
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

    //Helper Method to validate the mobile number entered by the user
    private void validateMobileNumber() {
        final int VERIFICATION_CODE = Utility.generateVerificationCode();
        Utility.sendSMS(getActivity(), VERIFICATION_CODE);
    }
}
