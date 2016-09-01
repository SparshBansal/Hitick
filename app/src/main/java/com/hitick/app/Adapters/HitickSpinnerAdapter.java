package com.hitick.app.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.hitick.app.Activities.MainActivity;
import com.hitick.app.Fragments.PollListFragment;
import com.hitick.app.R;

/**
 * Created by sparsh on 27/8/16.
 */
public class HitickSpinnerAdapter extends CursorAdapter {


    public HitickSpinnerAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.spinner_item_view,parent , false);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.tv_group_name);
        textView.setText(cursor.getString(PollListFragment.COL_GROUP_NAME));
    }
}
