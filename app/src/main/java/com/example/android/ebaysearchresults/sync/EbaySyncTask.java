package com.example.android.ebaysearchresults.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.android.ebaysearchresults.DetailActivity;
import com.example.android.ebaysearchresults.data.EbayContract;
import com.example.android.ebaysearchresults.data.EbayDbHelper;
import com.example.android.ebaysearchresults.utilities.NetworkUtils;
import com.example.android.ebaysearchresults.utilities.NotificationUtils;
import com.example.android.ebaysearchresults.utilities.OpenEbayJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Objects;

/**
 * Created by tanujanuj on 14/07/17.
 */

public class EbaySyncTask {
    private static SQLiteDatabase mNdb;


    synchronized public static void syncEbayItem(Context context) {

        EbayDbHelper ebayDbHelper = new EbayDbHelper(context);
        mNdb = ebayDbHelper.getWritableDatabase();
        Cursor cursorNotify = getNotifyItem();
        //  Log.v("Cursor object", DatabaseUtils.dumpCursorToString(cursorNotify));
        cursorNotify.moveToFirst();
        int count=0;
        String t="";
        for (int i = 0; i < cursorNotify.getCount(); i++) {
            String data = cursorNotify.getString(cursorNotify.getColumnIndex(EbayContract.EbayEntry.COLUMN_OLD_PRICE));
            String newPrice = cursorNotify.getString(cursorNotify.getColumnIndex(EbayContract.EbayEntry.COLUMN_NEW_PRICE));
            String title = cursorNotify.getString(cursorNotify.getColumnIndex(EbayContract.EbayEntry.COLUMN_TITLE));
            String itemId = cursorNotify.getString(cursorNotify.getColumnIndex(EbayContract.EbayEntry.COLUMN_ITEM_ID));

            String[] simpleForm = getSimpleform(itemId, context);


            String price = simpleForm[3];



            if (price.equals(data)) {
                String where = "itemid=?";
                ContentValues contentValues=new ContentValues();
                contentValues.put(EbayContract.EbayEntry.COLUMN_OLD_PRICE,newPrice);
                contentValues.put(EbayContract.EbayEntry.COLUMN_NEW_PRICE,price);
                String[] whereArgs = new String[] {String.valueOf(itemId)};
                mNdb.update(EbayContract.EbayEntry.TABLE_NAME, contentValues, where, whereArgs);
                count++;
                if(count==1) {
                    t=title;
                    NotificationUtils.remindUserBecausePriceDropped(context, title);
                }
                else {
                    t=t+"  "+title;
                    NotificationUtils.remindUserBecausePriceDropped(context, t);
                }


            }
            cursorNotify.moveToNext();

        }

    }


    private static String[] getSimpleform(String itemId, Context context){
        URL ebaySearchUrl= NetworkUtils.buildUrlForDetails(itemId);

        try {
            String ebaySearchResults = NetworkUtils.getResponseFromHttpUrl(ebaySearchUrl);
            String[] simpleJsonEbayData= OpenEbayJsonUtils.getSimpleEbayDetailStringsFromJson(context,ebaySearchResults);
            return simpleJsonEbayData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    private static Cursor getNotifyItem() {
        return mNdb.query(
                EbayContract.EbayEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                EbayContract.EbayEntry.COLUMN_TIMESTAMP);
    }
}
