package com.hitick.app.Fragments;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.hitick.app.Network.VolleySingleton;
import com.hitick.app.R;
import com.hitick.app.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //Unregister the receiver
        getActivity().unregisterReceiver(mSmsReceiver);
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

        //Create the JSON Object to send to the server for SignUp
        JSONObject postObject = new JSONObject();
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
                return paramsMap;
            }
        };
        mRequestQueue.add(mSignUpRequest);
    }

    //Helper Method to validate the mobile number entered by the user
    private void validateMobileNumber() {
        final int VERIFICATION_CODE = Utility.generateVerificationCode();
        Utility.sendSMS(getActivity(), VERIFICATION_CODE);
    }

}
