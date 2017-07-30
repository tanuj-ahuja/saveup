package com.example.android.ebaysearchresults.utilities;

import android.content.Context;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by tanujanuj on 11/07/17.
 */

public final class OpenEbayJsonUtils {

    public static String[][] getSimpleEbayStringsFromJson(Context context,String ebayJsonStr)
    throws JSONException{
        final String OWM_ITEM="item";
        final String OWM_TITLE="title";
        final String OWM_ID="itemId";
        final String OWM_IMG="imageURL";
        final String OWM_VALUE="__value__";
        String[][] parsedEbayData=null;
        JSONObject ebayJson=new JSONObject(ebayJsonStr);

        JSONObject gmwi=ebayJson.getJSONObject("getMostWatchedItemsResponse");
        JSONObject it=gmwi.getJSONObject("itemRecommendations");


        JSONArray ebayArray=it.getJSONArray(OWM_ITEM);

        parsedEbayData =new String[ebayArray.length()][4];

        for(int i=0;i<ebayArray.length();i++) {
                String title;
                String id;
                String price;
                JSONObject ebayPopular = ebayArray.getJSONObject(i);
                JSONObject buyPrice=ebayPopular.getJSONObject("buyItNowPrice");
                price=buyPrice.getString(OWM_VALUE);
                title = ebayPopular.getString(OWM_TITLE);
                id=ebayPopular.getString(OWM_ID);
                parsedEbayData[i][0] = title;
                parsedEbayData[i][1]=id;
                parsedEbayData[i][2]=ebayPopular.getString(OWM_IMG);
                parsedEbayData[i][3]=price;


            }

        return parsedEbayData;
    }

    public static String[][] getSimpleEbaySearchStringsFromJson(Context context,String ebayJsonStr)
    throws JSONException{
        String[][] parsedEbayData=null;
        JSONObject ebayJson=new JSONObject(ebayJsonStr);
        JSONArray fibkr=ebayJson.getJSONArray("findItemsByKeywordsResponse");
        JSONObject jj=fibkr.getJSONObject(0);
        JSONArray sr=jj.getJSONArray("searchResult");
        JSONObject kk=sr.getJSONObject(0);
        JSONArray item=kk.getJSONArray("item");

        parsedEbayData=new String[item.length()][4];

        for (int i=0;i<item.length();i++) {

            JSONObject uu = item.getJSONObject(i);
            JSONArray title=uu.getJSONArray("title");
            JSONArray id=uu.getJSONArray("itemId");
            JSONArray im=uu.getJSONArray("galleryURL");


            JSONArray buyItNowPrice=uu.getJSONArray("sellingStatus");
           JSONObject price=buyItNowPrice.getJSONObject(0);
           JSONArray p=price.getJSONArray("currentPrice");
           JSONObject m=p.getJSONObject(0);

           String x=m.getString("__value__");

                String t=title.getString(0).toString();
                String idi=id.getString(0).toString();
                String ima=im.getString(0).toString();

            //String t=uu.getString("title").toString();
            //String id=uu.getString("itemId");

            parsedEbayData[i][0] = t;
            parsedEbayData[i][1] = idi;
            parsedEbayData[i][2]=ima;
           parsedEbayData[i][3]=x;
        }

        return parsedEbayData;
    }

    public static String[] getSimpleEbayDetailStringsFromJson(Context context,String ebayJsonStr)
            throws JSONException{
        final String OWM_ITEM="item";
        final String OWM_TITLE="Title";
        final String OWM_ID="ItemId";
        final String OWM_LOCATION="Location";
        final String OWM_DES="ConditionDescription";
        final String OWM_PRICE="Value";
        final String OWM_CURRENCY="CurrencyID";
        final String OWM_URL="ViewItemURLForNaturalSearch";
        String[] parsedEbayData=null;
        JSONObject ebayJson=new JSONObject(ebayJsonStr);


        JSONObject it=ebayJson.getJSONObject("Item");
        JSONObject price=it.getJSONObject("ConvertedCurrentPrice");
        JSONArray image=it.getJSONArray("PictureURL");


         parsedEbayData =new String[it.length()];
         parsedEbayData[0]=it.getString(OWM_TITLE);
         parsedEbayData[1]=it.getString(OWM_LOCATION);
        if(it.has(OWM_DES))
         parsedEbayData[2]=it.getString(OWM_DES);
         parsedEbayData[3]=price.getString(OWM_PRICE)+" "+price.getString(OWM_CURRENCY);
         parsedEbayData[4]=image.getString(0).toString();
        parsedEbayData[5]=it.getString(OWM_URL);

        return parsedEbayData;
    }

}
