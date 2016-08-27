package com.hitick.app.Activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hitick.app.Adapters.HitickPollViewAdapter;
import com.hitick.app.Adapters.HitickSpinnerAdapter;
import com.hitick.app.Data.DatabaseContract;
import com.hitick.app.Data.DatabaseContract.GroupEntry;
import com.hitick.app.Data.DatabaseContract.*;
import com.hitick.app.R;
import com.hitick.app.Services.HitickPollUpdateService;
import com.hitick.app.Utility;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static Toolbar toolbar;
    private static Spinner spinner;
    private static HitickSpinnerAdapter spinnerAdapter;
    private static HitickPollViewAdapter hitickPollViewAdapter;
    private static RecyclerView rvPollView;

    // Loader ID for loading the data for the spinner and Main Content View
    private static final int SPINNER_LOADER_ID = 100;
    private static final int GROUP_DETAILS_LOADER_ID = 101;

    // KEY value for the argument of the bundle passed in onCreateLoader()
    private static final String KEY_ARGS_GROUP_ID = "KEY_GROUP_DETAILS_ID";

    // PROJECTIONS for the join query used
    private static final String[] USER_PARTICIPATION_WITH_GROUPS_PROJECTION = new String[]{
            UserParticipationEntry.TABLE_NAME + "." + UserParticipationEntry.COLUMN_GROUP_ADMINISTRATOR,
            GroupEntry.TABLE_NAME + "." + GroupEntry.COLUMN_GROUP_ID + " AS " + GroupEntry._ID,
            GroupEntry.TABLE_NAME + "." + GroupEntry.COLUMN_GROUP_MEMBERS,
            GroupEntry.TABLE_NAME + "." + GroupEntry.COLUMN_GROUP_NAME,
    };

    // Integer Constants corresponding to each item in the projections array
    public static final int COL_GROUP_ADMINISTRATOR = 0;
    public static final int COL_GROUP_ID = 1;
    public static final int COL_GROUP_MEMBERS = 2;
    public static final int COL_GROUP_NAME = 3;

    // PROJECTIONS for the GROUP_DETAILS query
    private static final String[] GROUP_DETAILS_PROJECTION = new String[]{
            GroupDetailsEntry.COLUMN_POLL_ID,
            GroupDetailsEntry.COLUMN_POLL_TOPIC,
            GroupDetailsEntry.COLUMN_IN_FAVOR,
            GroupDetailsEntry.COLUMN_OPPOSED,
            GroupDetailsEntry.COLUMN_NOT_VOTED,
            GroupDetailsEntry.COLUMN_POLL_DATETIME,
            GroupDetailsEntry.COLUMN_STIPULATED_TIME,
            GroupDetailsEntry.COLUMN_POLL_RESULT
    };

    public static final int COL_POLL_ID = 0;
    public static final int COL_POLL_TOPIC = 1;
    public static final int COL_IN_FAVOR = 2;
    public static final int COL_OPPOSED = 3;
    public static final int COL_NOT_VOTED = 4;
    public static final int COL_POLL_DATETIME = 5;
    public static final int COL_TIME_LEFT = 6;
    public static final int COL_POLL_RESULT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the instance variable
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner) findViewById(R.id.spinner_group_spinner);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spinnerAdapter = new HitickSpinnerAdapter(this , null , false);
        rvPollView = (RecyclerView) findViewById(R.id.rv_poll_view);

        // Initialize the loader
        getSupportLoaderManager().initLoader(SPINNER_LOADER_ID, null, this);


        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        hitickPollViewAdapter = new HitickPollViewAdapter(this);
        rvPollView.setLayoutManager(new LinearLayoutManager(this));
        rvPollView.setAdapter(hitickPollViewAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Loader Callback methods for the spinner loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /**Based on the loader id we create the corresponding cursor loader
         * if loaderId == SPINNER LOADER ID we create the loader to populate the spinner view
         * if loaderId == GROUP DETAILS LOADER ID , we create the loader to populate the viewPager and listView
         * */
        switch (id) {
            case SPINNER_LOADER_ID:
                Uri uri = DatabaseContract.Joins.buildUserPartcipationWithGroupUri(Utility.getCurrentUserId(this));
                return new CursorLoader(
                        this,
                        uri,
                        USER_PARTICIPATION_WITH_GROUPS_PROJECTION,
                        null,
                        null,
                        null
                );
            case GROUP_DETAILS_LOADER_ID:
                // The table name will be passed as an argument, so we obtain it from args bundle
                final String groupId = args.getString(KEY_ARGS_GROUP_ID);
                uri = GroupDetailsEntry.buildGroupDetailsUri(groupId);
                Log.d(TAG, "onCreateLoader: " + uri.toString());
                return new CursorLoader(
                        this,
                        uri,
                        GROUP_DETAILS_PROJECTION,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /* Update our views here */
        final int id = loader.getId();
        switch (id) {
            case SPINNER_LOADER_ID :
                spinnerAdapter.swapCursor(data);
                break;
            case GROUP_DETAILS_LOADER_ID:
                hitickPollViewAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        final int id = loader.getId();
        switch (id) {
            case SPINNER_LOADER_ID :
                spinnerAdapter.swapCursor(null);
                break;
            case GROUP_DETAILS_LOADER_ID:
                /*TODO -- Update the viewPager and listView*/
                break;
        }
    }

    /* Spinner callbacks for item selection , we will load the data from the database
       based on the team selected by the user */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // Get the cursor and position it to the current location and then determine the
        // GROUP_DETAILS table name.

        Cursor cursor = spinnerAdapter.getCursor();
        cursor.moveToPosition(position);

        final String groupID = cursor.getString(COL_GROUP_ID);
        final long timestamp = System.currentTimeMillis();

        // Start the service to fetch the latest polls
        HitickPollUpdateService.startPollUpdate(this , groupID , String.valueOf(timestamp) , 20);

        // Now we restart the loader to load the Group Details from the GROUP_DETAILS table
        // Pass the group details table name as an argument in the bundle
        Bundle args = new Bundle();
        args.putString(KEY_ARGS_GROUP_ID, groupID);

        getSupportLoaderManager().restartLoader(GROUP_DETAILS_LOADER_ID, args, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
