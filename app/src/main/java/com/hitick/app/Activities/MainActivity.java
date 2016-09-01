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
import com.hitick.app.Fragments.PollListFragment;
import com.hitick.app.R;
import com.hitick.app.Services.HitickPollUpdateService;
import com.hitick.app.Utility;

public class MainActivity extends AppCompatActivity implements PollListFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the instance variable
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
