package com.hitick.app.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitick.app.Activities.MainActivity;
import com.hitick.app.R;

/**
 * Created by sparsh on 27/8/16.
 */
public class HitickPollViewAdapter extends
        HitickPollViewCursorAdapter<HitickPollViewAdapter.PollViewHolder> {

    private final Context context;

    public HitickPollViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(PollViewHolder viewHolder, int position, Cursor cursor) {
        if (cursor!= null && !cursor.isClosed()) {
            cursor.moveToPosition(position);

            // Set the data in the views
            viewHolder.tvPollTopic.setText(cursor.getString(MainActivity.COL_POLL_TOPIC));
            viewHolder.tvInFavor.setText(String.valueOf(cursor.getInt(MainActivity.COL_IN_FAVOR)));
            viewHolder.tvOpposed.setText(String.valueOf(cursor.getInt(MainActivity.COL_OPPOSED)));
            viewHolder.tvNotVoted.setText(String.valueOf(cursor.getInt(MainActivity.COL_NOT_VOTED)));
        }
    }

    @Override
    public PollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.rv_poll_view_item_view, parent, false);

        PollViewHolder viewHolder = new PollViewHolder(rootView);
        return viewHolder;
    }

    public class PollViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPollTopic;
        public TextView tvInFavor;
        public TextView tvOpposed;
        public TextView tvNotVoted;

        public PollViewHolder(View itemView) {
            super(itemView);

            tvPollTopic = (TextView) itemView.findViewById(R.id.tv_poll_topic);
            tvInFavor = (TextView) itemView.findViewById(R.id.tv_in_favor);
            tvOpposed = (TextView) itemView.findViewById(R.id.tv_opposed);
            tvNotVoted = (TextView) itemView.findViewById(R.id.tv_not_voted);
        }
    }
}
