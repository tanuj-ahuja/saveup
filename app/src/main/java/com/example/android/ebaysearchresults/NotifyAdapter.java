package com.example.android.ebaysearchresults;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.ebaysearchresults.data.EbayContract;

/**
 * Created by tanujanuj on 17/07/17.
 */

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyAdapterViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    final private NotificationOnClickHandler mNClickHandler;

    public  interface NotificationOnClickHandler{
        void onNotificationListItemClick(String ClickedString);
    }

    public NotifyAdapter(Context context, Cursor cursor,NotificationOnClickHandler listener) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mNClickHandler=listener;
    }

    @Override
    public NotifyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.notification_list_item, parent, false);
        return new NotifyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotifyAdapterViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;
        String title = mCursor.getString(mCursor.getColumnIndex(EbayContract.EbayEntry.COLUMN_TITLE));
        String oldp = mCursor.getString(mCursor.getColumnIndex(EbayContract.EbayEntry.COLUMN_OLD_PRICE));
        String newp = mCursor.getString(mCursor.getColumnIndex(EbayContract.EbayEntry.COLUMN_NEW_PRICE));
        long id=mCursor.getLong(mCursor.getColumnIndex(EbayContract.EbayEntry._ID));
        holder.titleTextView.setText(title);
        holder.oldPriceTextView.setText(oldp);
        holder.newPriceTextView.setText(newp);
        holder.itemView.setTag(id);

    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    public class NotifyAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleTextView;
        TextView oldPriceTextView;
        TextView newPriceTextView;


        public NotifyAdapterViewHolder(View view) {
            super(view);
            titleTextView = (TextView) itemView.findViewById(R.id.ebay_notification_results_title);
            oldPriceTextView=(TextView) itemView.findViewById(R.id.ebay_notification_results_oldprice);
            newPriceTextView=(TextView) itemView.findViewById(R.id.ebay_notification_results_newprice);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition=getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);
            String iid= mCursor.getString(mCursor.getColumnIndex(EbayContract.EbayEntry.COLUMN_ITEM_ID));
            mNClickHandler.onNotificationListItemClick(iid);
        }
    }
}
