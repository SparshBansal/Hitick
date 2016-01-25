package com.hitick.app.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.StringRequest;
import com.hitick.app.Network.VolleySingleton;
import com.hitick.app.QuickstartPreferences;
import com.hitick.app.R;
import com.hitick.app.Services.HitickGCMRegistrationService;
import com.hitick.app.Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sparsha on 12/7/2015.
 */
public class SignInFragment extends Fragment {

    private static final String LOG_TAG = SignInFragment.class.getSimpleName();
    private static EditText etMobile;
    private static EditText etPassword;
    private static Button bSignIn;
    private static int MOBILE_NUMBER_LENGTH = 10;
    private GCMRegistrationReceiver mRegistrationBroadcastReceiver;

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
                String password = etMobile.getText().toString();

                if (mobileNumber.length() != MOBILE_NUMBER_LENGTH) {
                    //Make a toast to notify the user for wrong mobile number
                    Toast.makeText(getActivity(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the details in Shared Preferences
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString(getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_MOBILE_NUMBER), mobileNumber);
                editor.putString(getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_PASSWORD), password);
                editor.commit();

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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
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
        public void onReceive(Context context, Intent intent) {

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
                            /** Check if the user entry is already in our database , if not there
                             * then add the user to the user table , if already there then update*/

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
            mRequestQueue.add(mSignInRequest);
        }
    }
}
