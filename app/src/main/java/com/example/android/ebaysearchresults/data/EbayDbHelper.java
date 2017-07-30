package com.example.android.ebaysearchresults.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.ebaysearchresults.data.EbayContract.*;


/**
 * Created by tanujanuj on 13/07/17.
 */

public class EbayDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="ebayd.db";
    private static final int DATABASE_VERSION=34;


    public EbayDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_EBAY_TABLE = "CREATE TABLE   " + EbayEntry.TABLE_NAME +" (" +
                EbayEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                EbayEntry.COLUMN_ITEM_ID+" TEXT  NOT NULL, "+
                EbayEntry.COLUMN_OLD_PRICE+" TEXT NOT NULL, "+
                EbayEntry.COLUMN_NEW_PRICE+" TEXT NOT NULL, "+
                EbayEntry.COLUMN_TITLE+" TEXT NOT NULL, "+
                EbayEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +

                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_EBAY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+ EbayEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
