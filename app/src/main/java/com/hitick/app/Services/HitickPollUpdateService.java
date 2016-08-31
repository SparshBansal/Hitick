package com.hitick.app.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hitick.app.Data.DatabaseContract;
import com.hitick.app.JsonParser;
import com.hitick.app.Network.VolleySingleton;
import com.hitick.app.R;
import com.hitick.app.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Vector;

import static com.hitick.app.Data.DatabaseContract.*;


public class HitickPollUpdateService extends IntentService {

    private static final String ACTION_UPDATE_POLL = "com.hitick.app.Services.action.UPDATE_POLL";

    private static final String EXTRA_GROUP_ID = "com.hitick.app.Services.extra.EXTRA_GROUP_ID";
    private static final String EXTRA_TIMESTAMP = "com.hitick.app.Services.extra.EXTRA_TIMESTAMP";
    private static final String EXTRA_BATCH_SIZE = "com.hitick.app.Services.extra.EXTRA_BATCH_SIZE";

    private static final String KEY_QUERY_PARAM_GROUP_ID = "groupId";
    private static final String KEY_QUERY_PARAM_TIMESTAMP = "lastTime";
    private static final String KEY_QUERY_PARAM_BATCH_SIZE = "batchSize";
    private static final String KEY_QUERY_PARAM_USER_ID = "userId";




    private static final String TAG = HitickPollUpdateService.class.getSimpleName();


    public HitickPollUpdateService() {
        super("HitickPollUpdateService");
    }

    public static void startPollUpdate(Context context, String groupId, String timestamp, long batchSize) {
        Intent intent = new Intent(context, HitickPollUpdateService.class);
        intent.setAction(ACTION_UPDATE_POLL);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_TIMESTAMP, timestamp);
        intent.putExtra(EXTRA_BATCH_SIZE, batchSize);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_POLL.equals(action)) {
                final String groupId = intent.getStringExtra(EXTRA_GROUP_ID);
                final String timestamp = intent.getStringExtra(EXTRA_TIMESTAMP);
                final long batchSize = intent.getLongExtra(EXTRA_BATCH_SIZE, 20);
                updatePolls(groupId, timestamp, batchSize);
            }
        }
    }


    private void updatePolls(String groupId, String timestamp, long batchSize) {

        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        Uri uri = Uri.parse(getResources().getString(R.string.URL_FECTH_LATEST_POLL));

        uri = uri.buildUpon().appendQueryParameter(KEY_QUERY_PARAM_USER_ID , Utility.getCurrentUserId(getApplicationContext()))
                .appendQueryParameter(KEY_QUERY_PARAM_GROUP_ID, groupId)
                .appendQueryParameter(KEY_QUERY_PARAM_TIMESTAMP, timestamp)
                .appendQueryParameter(KEY_QUERY_PARAM_BATCH_SIZE, String.valueOf(batchSize))
                .build();

        Log.d(TAG, "updatePolls: " + uri.toString());

        JsonArrayRequest mRequest = new JsonArrayRequest(
                Request.Method.GET,
                uri.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JsonParser.parseInsertPolls(response , getApplicationContext());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });

        mRequestQueue.add(mRequest);
    }

}
