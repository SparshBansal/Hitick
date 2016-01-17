package com.hitick.app.Network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Sparsha on 10/20/2015.
 */

// Volley Singleton class to create a singleton Volley Request Queue
// Handles the network Operations
// A Request Queue can now be used anywhere in the app using static method getRequestQueue()
// Network Operations can be performed from anywhere using worker threads


public class VolleySingleton {

    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(VolleyApplication.getContext());
    }

    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
}
