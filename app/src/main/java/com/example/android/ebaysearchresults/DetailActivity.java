package com.example.android.ebaysearchresults;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.ebaysearchresults.data.EbayContract;
import com.example.android.ebaysearchresults.data.EbayDbHelper;
import com.example.android.ebaysearchresults.sync.EbaySyncTask;
import com.example.android.ebaysearchresults.sync.EbaySyncUtils;
import com.example.android.ebaysearchresults.utilities.NetworkUtils;
import com.example.android.ebaysearchresults.utilities.NotificationUtils;
import com.example.android.ebaysearchresults.utilities.OpenEbayJsonUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by tanujanuj on 11/07/17.
 */

public class DetailActivity  extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>{

    TextView mEbayDetailTitle;
    TextView mEbayDetailLoc;
    TextView mEbayDetailDes;
    TextView mEbayDetailPrice;
    Button mNotifyButton,mRemoveFromNotificationsButton,mUrlButton;
    Toast mToast;
    private static final int EBAY_DETAIL_LOADER_ID=1;
    String ebayDetailId;
    int mFlag;
    static ImageView bmImage;
    static Bitmap icon=null;
    static String mUrl=null;



    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mEbayDetailTitle=(TextView) findViewById(R.id.tv_detail_title);
        mEbayDetailLoc=(TextView) findViewById(R.id.tv_detail_loc);
        mEbayDetailDes=(TextView) findViewById(R.id.tv_detail_des);
        mEbayDetailPrice=(TextView) findViewById(R.id.tv_detail_price);
        mNotifyButton=(Button) findViewById(R.id.add_to_notify_button);
        mRemoveFromNotificationsButton=(Button) findViewById(R.id.remove_notification_button);
        mUrlButton=(Button) findViewById(R.id.url_button);
        bmImage=(ImageView) findViewById(R.id.image_form_url);


        EbayDbHelper dbHelper=new EbayDbHelper(this);
        mDb=dbHelper.getWritableDatabase();


        Intent intentThatStartedThisActivity=getIntent();
        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
             ebayDetailId=intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);

        }

        Cursor cursor=getItem();
        if(cursor.getCount()>0)
           mFlag=1;//mRemoveFromNotificationsButton.setVisibility(View.VISIBLE);
        else
            mFlag=0;//mNotifyButton.setVisibility(View.VISIBLE);


        int loaderId=EBAY_DETAIL_LOADER_ID;
        Bundle bundleForLoader=null;
        getSupportLoaderManager().initLoader(loaderId,bundleForLoader,DetailActivity.this);

        EbaySyncUtils.initialise(this);


    }

    private Cursor getItem(){
        String whereClause="itemid=?";
        String[] whereArgs=new String[]{ebayDetailId};
        return mDb.query(
                EbayContract.EbayEntry.TABLE_NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                EbayContract.EbayEntry.COLUMN_TIMESTAMP);
    }
    public void addToNotifications(View view){
        showDisableButton();
        Context context = DetailActivity.this;
        String title=mEbayDetailTitle.getText().toString();
        String price=mEbayDetailPrice.getText().toString();
        CharSequence text = "Will Notify when "+ title + " price lowers!@";
        int duration = Toast.LENGTH_SHORT;

        if(mToast!=null)
            mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mToast.cancel();
            }
        }, 950);

        addToDatabase(ebayDetailId,price,title);


    }

    public void removeFromNotifications(View view) {
         showNotifyButton();
        boolean deleted=removenotification(ebayDetailId);
        if(deleted==true){
            Toast.makeText(this,"deleted",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"could not delete",Toast.LENGTH_SHORT).show();
        }

    }

    public void openEbay(View view){
        Uri uri = Uri.parse(mUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    private void showNotifyButton(){
        mNotifyButton.setVisibility(View.VISIBLE);
        mRemoveFromNotificationsButton.setVisibility(View.GONE);
    }

    private void showDisableButton() {
        mNotifyButton.setVisibility(View.GONE);
        mRemoveFromNotificationsButton.setVisibility(View.VISIBLE);

    }

    public long addToDatabase(String itemId,String price,String title){
        ContentValues cv=new ContentValues();
        cv.put(EbayContract.EbayEntry.COLUMN_ITEM_ID,itemId);
        cv.put(EbayContract.EbayEntry.COLUMN_OLD_PRICE,price);
        cv.put(EbayContract.EbayEntry.COLUMN_NEW_PRICE,price);
        cv.put(EbayContract.EbayEntry.COLUMN_TITLE,title);

        long rowInserted= mDb.insert(EbayContract.EbayEntry.TABLE_NAME,null,cv);
        if(rowInserted!=-1)
            Toast.makeText(this,"added",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"could not add",Toast.LENGTH_SHORT).show();
        return rowInserted;
    }

    private boolean removenotification(String itemId){
        return mDb.delete(EbayContract.EbayEntry.TABLE_NAME,EbayContract.EbayEntry.COLUMN_ITEM_ID+"="+itemId,null)>0;

    }

    public void testNotifications(View view){
        EbaySyncTask.syncEbayItem(this);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] mEbayDetailData=null;

            @Override
            protected void onStartLoading(){
                if (mEbayDetailData!=null)
                    deliverResult(mEbayDetailData);
                else {
                    forceLoad();
                }

            }

            @Override
            public String[] loadInBackground() {
                URL ebaySearchUrl = NetworkUtils.buildUrlForDetails(ebayDetailId);


                try {
                    String ebaySearchResults = NetworkUtils.getResponseFromHttpUrl(ebaySearchUrl);
                    String[] simpleJsonEbayData= OpenEbayJsonUtils.getSimpleEbayDetailStringsFromJson(DetailActivity.this,ebaySearchResults);
                    InputStream in=new java.net.URL(simpleJsonEbayData[4]).openStream();
                    icon= BitmapFactory.decodeStream(in);
                    mUrl=simpleJsonEbayData[5];
                    return simpleJsonEbayData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
            public void deliveryResult(String[] data){
                mEbayDetailData=data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        if(data!=null){

            mEbayDetailTitle.setText(data[0]);
            mEbayDetailLoc.setText(data[1]);
            if(data[2]!=null)
            mEbayDetailDes.setText(data[2]);
            mEbayDetailPrice.setText(data[3]);
            if(mFlag==1)
                mRemoveFromNotificationsButton.setVisibility(View.VISIBLE);
            else
                mNotifyButton.setVisibility(View.VISIBLE);
            mUrlButton.setVisibility(View.VISIBLE);

            bmImage.setImageBitmap(icon);

        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }




}
