package com.hitick.app.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hitick.app.Data.DatabaseContract;
import com.hitick.app.Data.DatabaseContract.GroupDetailsEntry;
import com.hitick.app.Data.DatabaseContract.GroupEntry;
import com.hitick.app.Data.DatabaseContract.UserParticipationEntry;
import com.hitick.app.R;
import com.hitick.app.Utility;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {

    private static Toolbar toolbar;
    private static Spinner spinner;
    private static SimpleCursorAdapter spinnerAdapter;

    // Loader ID for loading the data for the spinner and Main Content View
    private static final int SPINNER_LOADER_ID = 100;
    private static final int GROUP_DETAILS_LOADER_ID = 101;

    // KEY value for the argument of the bundle passed in onCreateLoader()
    private static final String KEY_ARGS_GROUP_DETAILS_TABLE = "KEY_GROUP_DETAILS_TABLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the instance variable
        toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        spinner = (Spinner) findViewById(R.id.mainActivitySpinner);

        // Initialize the loader
        getSupportLoaderManager().initLoader(SPINNER_LOADER_ID, null, this);

        spinnerAdapter = new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_2,
                null,
                new String[]{GroupEntry.COLUMN_GROUP_NAME, GroupEntry.COLUMN_GROUP_MEMBERS},
                new int[]{android.R.id.text1, android.R.id.text2});

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
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
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /* Spinner callbacks for item selection , we will load the data from the database
       based on the team selected by the user */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Get the cursor and position it to the current location and then determine the
        // GROUP_DETAILS table name.

        Cursor cursor = spinnerAdapter.getCursor();
        cursor.moveToPosition(i);

        final String groupDetailsTableName = cursor.getString(
                cursor.getColumnIndex(DatabaseContract.GroupEntry.COLUMN_GROUP_DETAILS));

        // Now we restart the loader to load the Group Details from the GROUP_DETAILS table
        // Pass the group details table name as an argument in the bundle
        Bundle args = new Bundle();
        args.putString(KEY_ARGS_GROUP_DETAILS_TABLE, groupDetailsTableName);

        getSupportLoaderManager().restartLoader(GROUP_DETAILS_LOADER_ID, args, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
