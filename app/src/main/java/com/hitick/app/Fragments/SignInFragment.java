package com.hitick.app.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.hitick.app.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                //Send Data to the Server
                boolean mSignedIn = signIn(mobileNumber, password);
            }
        });
        return rootView;
    }

    //Helper method to SignIn to the server
    boolean signIn(final String mobileNumber, final String password) {

        //Setup the Request Queue and post the Request to the server
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
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
                return paramsMap;
            }
        };
        mRequestQueue.add(mSignInRequest);
        return false;
    }
}
