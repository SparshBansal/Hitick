package com.hitick.app.Adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sparsh on 27/8/16.
 */
public abstract class HitickPollViewCursorAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    Cursor mCursor = null;

    public abstract void onBindViewHolder(VH viewHolder , int position , Cursor cursor);

    public void swapCursor(Cursor mCursor){
        this.mCursor = mCursor;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        this.onBindViewHolder(holder , position , mCursor);
    }

    @Override
    public int getItemCount() {
        return this.mCursor!=null ? this.mCursor.getCount() : 0;
    }
}
