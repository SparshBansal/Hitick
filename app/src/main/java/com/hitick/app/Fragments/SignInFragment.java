package com.hitick.app.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hitick.app.Data.DatabaseContract.GroupEntry;
import com.hitick.app.Data.DatabaseContract.UserEntry;
import com.hitick.app.Data.DatabaseContract.UserParticipationEntry;
import com.hitick.app.JsonParser;
import com.hitick.app.Network.VolleySingleton;
import com.hitick.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class SignInFragment extends Fragment {

    private static final String LOG_TAG = SignInFragment.class.getSimpleName();
    private static final String REQUEST_TAG = "SIGN_IN_TAG";
    private static EditText etMobile;
    private static EditText etPassword;
    private static Button bSignIn;
    private static int MOBILE_NUMBER_LENGTH = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in_details, container, false);

        //Initialize the instance variables
        etMobile = (EditText) rootView.findViewById(R.id.et_mobile);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);
        bSignIn = (Button) rootView.findViewById(R.id.b_sign_in);

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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cancel all the requests from the request queue
        VolleySingleton.getInstance().getRequestQueue().cancelAll(REQUEST_TAG);
    }

    //Helper method to SignIn to the server
    public boolean signIn() {

        final String registrationToken = FirebaseInstanceId.getInstance().getToken();
        if (registrationToken != null) {
            RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final String mobileNumber = preferences.getString(
                    getActivity().getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_MOBILE_NUMBER), "");
            final String password = preferences.getString(
                    getActivity().getResources().getString(R.string.KEY_PREFERENCE_SIGNIN_PASSWORD), "");

            if (TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(password))
                return false;

            String baseUrl = getActivity().getString(R.string.URL_SIGN_IN);

            Uri url = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("mobile", mobileNumber)
                    .appendQueryParameter("password", password).build();

            Log.d(LOG_TAG, "signIn: " + url.toString());
            JsonObjectRequest mSignInRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url.toString(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(LOG_TAG, "Response Received");
                            Log.d(LOG_TAG, "onResponse: " + response.toString());
                            // Parse the data and then insert in the database
                            JsonParser.parseInsert(response,getActivity());
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
                    });
            mSignInRequest.setTag(REQUEST_TAG);
            mRequestQueue.add(mSignInRequest);
        }
        return false;
    }
}
