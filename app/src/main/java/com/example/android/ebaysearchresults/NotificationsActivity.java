package com.example.android.ebaysearchresults;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.android.ebaysearchresults.data.EbayContract;
import com.example.android.ebaysearchresults.data.EbayDbHelper;

public class NotificationsActivity extends AppCompatActivity implements NotifyAdapter.NotificationOnClickHandler {

    private SQLiteDatabase mDb;
    private NotifyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        RecyclerView notifyRecyclerView;

        notifyRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerview_notify_ebay);

        notifyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        EbayDbHelper dbHelper = new EbayDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllNotifications();

        mAdapter = new NotifyAdapter(this, cursor,this);

        notifyRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id=(long) viewHolder.itemView.getTag();
                removeNotification(id);
                mAdapter.swapCursor(getAllNotifications());
            }

        }).attachToRecyclerView(notifyRecyclerView);

    }

    @Override
    public void onNotificationListItemClick(String ClickedString) {
        Class destinationActivity=DetailActivity.class;
        Intent intentToStartDetailActivity=new Intent(NotificationsActivity.this,destinationActivity);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT,ClickedString);
        startActivity(intentToStartDetailActivity);
    }

    private Cursor getAllNotifications() {
        return mDb.query(
                EbayContract.EbayEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EbayContract.EbayEntry.COLUMN_TIMESTAMP
        );

    }
    private boolean removeNotification(long id){
        return mDb.delete(EbayContract.EbayEntry.TABLE_NAME,EbayContract.EbayEntry._ID+"="+id,null)>0;

    }
}
