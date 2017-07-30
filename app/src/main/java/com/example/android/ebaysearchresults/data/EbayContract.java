package com.example.android.ebaysearchresults.data;

import android.provider.BaseColumns;

/**
 * Created by tanujanuj on 13/07/17.
 */

public class EbayContract {

    public static final class EbayEntry implements BaseColumns {
        public static final String TABLE_NAME="ebayd";
        public static final String COLUMN_ITEM_ID="itemid";
        public static final String COLUMN_OLD_PRICE="oldprice";
        public static final String COLUMN_NEW_PRICE="newprice";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_TIMESTAMP="timestamp";

    }
}
