package com.example.android.ebaysearchresults.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by tanujanuj on 10/07/17.
 */

public class NetworkUtils {
    final static String EBAY_POPULAR_URL=
            "http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getMostWatchedItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=API_KEY&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&maxResults=10";

    final static String EBAY_DETAIL_URL="http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=API_KEY&siteid=0&version=967";

    final static String EBAY_SEARCH_URL="http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.12.0&SECURITY-APPNAME=API_KEY&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD";

    private static final String ID_PARAM = "ItemId";

    private static final String KEY_PARAM="keywords";
    private static final String KEY_ENTRIES="paginationInput.entriesPerPage";

    public static URL buildUrl(){
        Uri builtUri=Uri.parse(EBAY_POPULAR_URL);
        URL url=null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlForDetails(String detailId){
        Uri builtUri=Uri.parse(EBAY_DETAIL_URL).buildUpon()
                .appendQueryParameter(ID_PARAM,String.valueOf(detailId))
                .build();

        URL url=null;
        try{
            url=new URL(builtUri.toString());

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForSearch(String query){
        Uri builtUri=Uri.parse(EBAY_SEARCH_URL).buildUpon()
                .appendQueryParameter(KEY_PARAM,String.valueOf(query))
                .appendQueryParameter(KEY_ENTRIES,"7")
                .build();
        URL url=null;
        try{
            url=new URL(builtUri.toString());

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }




    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
