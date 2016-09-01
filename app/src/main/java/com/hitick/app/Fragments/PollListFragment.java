package com.hitick.app.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hitick.app.Adapters.HitickPollViewAdapter;
import com.hitick.app.Adapters.HitickSpinnerAdapter;
import com.hitick.app.Data.DatabaseContract;
import com.hitick.app.R;
import com.hitick.app.Services.HitickPollUpdateService;
import com.hitick.app.Utility;

import static com.hitick.app.Data.DatabaseContract.*;


public class PollListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = PollListFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

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

    public PollListFragment() {
        // Required empty public constructor
    }

    public static PollListFragment newInstance(String param1, String param2) {
        PollListFragment fragment = new PollListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_poll_list, container, false);

        spinner = (Spinner) rootView.findViewById(R.id.spinner_group_spinner);
        spinnerAdapter = new HitickSpinnerAdapter(getContext(), null, false);
        rvPollView = (RecyclerView) rootView.findViewById(R.id.rv_poll_view);

        // Initialize the loader
        getActivity().getSupportLoaderManager().initLoader(SPINNER_LOADER_ID, null, this);


        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        hitickPollViewAdapter = new HitickPollViewAdapter(getContext());
        rvPollView.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPollView.setAdapter(hitickPollViewAdapter);

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize the loader
        getActivity().getSupportLoaderManager().initLoader(SPINNER_LOADER_ID, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        /**
         * Based on the loader id we create the corresponding cursor loader
         * if loaderId == SPINNER LOADER ID we create the loader to populate the spinner view
         * if loaderId == GROUP DETAILS LOADER ID , we create the loader to populate the viewPager
         * and listView
         * */
        switch (id) {
            case SPINNER_LOADER_ID:
                Uri uri = DatabaseContract.Joins
                        .buildUserPartcipationWithGroupUri(Utility.getCurrentUserId(getContext()));
                return new CursorLoader(
                        getContext(),
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
                        getContext(),
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
    public void onLoadFinished(Loader loader, Cursor data) {
        /* Update our views here */
        final int id = loader.getId();
        switch (id) {
            case SPINNER_LOADER_ID:
                spinnerAdapter.swapCursor(data);
                break;
            case GROUP_DETAILS_LOADER_ID:
                hitickPollViewAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        final int id = loader.getId();
        switch (id) {
            case SPINNER_LOADER_ID:
                spinnerAdapter.swapCursor(null);
                break;
            case GROUP_DETAILS_LOADER_ID:
                hitickPollViewAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the cursor and position it to the current location and then determine the
        // GROUP_DETAILS table name.

        Cursor cursor = spinnerAdapter.getCursor();
        cursor.moveToPosition(position);

        final String groupID = cursor.getString(COL_GROUP_ID);
        final long timestamp = System.currentTimeMillis();

        // Start the service to fetch the latest polls
        HitickPollUpdateService.startPollUpdate(getContext(), groupID, String.valueOf(timestamp), 20);

        // Now we restart the loader to load the Group Details from the GROUP_DETAILS table
        // Pass the group details table name as an argument in the bundle
        Bundle args = new Bundle();
        args.putString(KEY_ARGS_GROUP_ID, groupID);

        getActivity().getSupportLoaderManager().restartLoader(GROUP_DETAILS_LOADER_ID, args, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
