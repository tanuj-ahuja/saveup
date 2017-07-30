package com.example.android.ebaysearchresults;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.ebaysearchresults.sync.EbaySyncUtils;
import com.example.android.ebaysearchresults.utilities.NetworkUtils;
import com.example.android.ebaysearchresults.utilities.OpenEbayJsonUtils;

import java.io.InputStream;
import java.net.URL;

public class  MainActivity extends AppCompatActivity implements EbayAdapter.EbayAdapterOnClickHandler
,LoaderManager.LoaderCallbacks<String[][]>{


    private RecyclerView mRecyclerView;
    EditText mSearchQuery;
    EbayAdapter mEbayAdapter;
    private static final int EBAY_LOADER_ID=0;
    String ebayQuery;
    Toast mToast;
    int flag;
    static Bitmap[] image;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchQuery=(EditText) findViewById(R.id.search_item);
        mRecyclerView=(RecyclerView) findViewById(R.id.recyclerview_ebay);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mEbayAdapter=new EbayAdapter(this);
        mRecyclerView.setAdapter(mEbayAdapter);

        flag=0;

        int loaderId=EBAY_LOADER_ID;
        Bundle bundleForLoader=null;
        getSupportLoaderManager().initLoader(loaderId,bundleForLoader,MainActivity.this);



    }



    @Override
    public void onListItemClick(String ClickedString) {
      Class destinationActivity=DetailActivity.class;
        Intent intentToStartDetailActivity=new Intent(MainActivity.this,destinationActivity);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT,ClickedString);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public Loader<String[][]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[][]>(this) {

            String[][] mEbayData=null;


            @Override
            protected void onStartLoading(){
                if (mEbayData!=null)
                    deliverResult(mEbayData);
                else {
                    forceLoad();
                }

            }

            @Override
            public String[][] loadInBackground() {
                if (flag == 0) {
                    URL ebaySearchUrl = NetworkUtils.buildUrl();
                    try {

                        String ebaySearchResults = NetworkUtils.getResponseFromHttpUrl(ebaySearchUrl);
                        String[][] simpleJsonEbayData = OpenEbayJsonUtils.getSimpleEbayStringsFromJson(MainActivity.this, ebaySearchResults);
                        image=new Bitmap[simpleJsonEbayData.length];
                        for(int i=0;i<simpleJsonEbayData.length;i++){

                            InputStream in=new java.net.URL(simpleJsonEbayData[i][2]).openStream();
                            image[i]= BitmapFactory.decodeStream(in);
                        }
                        return simpleJsonEbayData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else if (flag == 1) {
                    URL ebaySearchUrl = NetworkUtils.buildUrlForSearch(ebayQuery);
                    try {
                        String ebaySearchResults = NetworkUtils.getResponseFromHttpUrl(ebaySearchUrl);
                        String[][] simpleJsonEbayData=OpenEbayJsonUtils.getSimpleEbaySearchStringsFromJson(MainActivity.this,ebaySearchResults);
                        //simpleJsonEbayData[0][0]=ebaySearchUrl.toString();
                        image=new Bitmap[simpleJsonEbayData.length];
                        for(int i=0;i<simpleJsonEbayData.length;i++){

                            InputStream in=new java.net.URL(simpleJsonEbayData[i][2]).openStream();
                            image[i]= BitmapFactory.decodeStream(in);
                        }
                        return simpleJsonEbayData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                 return null;
            }
            public void deliveryResult(String[][] data){
                mEbayData=data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[][]> loader, String[][] data) {
        if(data!=null){
            mEbayAdapter.setEbayData(data,image);

        }
    }

    @Override
    public void onLoaderReset(Loader<String[][]> loader) {

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            ebayQuery=mSearchQuery.getText().toString();
            if(ebayQuery.length()==0){
                mToast =Toast.makeText(this,"Please enter a query",Toast.LENGTH_LONG);
                mToast.show();
            }
            else {
                flag=1;
                mEbayAdapter.setEbayData(null,null);
                getSupportLoaderManager().restartLoader(1, null, this);
            }
            return true;

        }
        else if(itemThatWasClickedId==R.id.action_show_notifications){
            Intent intentToStartNotificationsActivity=new Intent(MainActivity.this,NotificationsActivity.class);
            startActivity(intentToStartNotificationsActivity);
        }
        return super.onOptionsItemSelected(item);
    }
}