package com.example.android.ebaysearchresults;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tanujanuj on 11/07/17.
 */

public class EbayAdapter extends RecyclerView.Adapter<EbayAdapter.EbayAdapterViewHolder> {

    private String[][] mEbayData;
    private Bitmap[] mImage;

    final private EbayAdapterOnClickHandler mClickHandler;

    public interface EbayAdapterOnClickHandler{
        void onListItemClick(String ClickedString);
    }

    public EbayAdapter(EbayAdapterOnClickHandler listener){
        mClickHandler=listener;
    }

    @Override
    public EbayAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context=viewGroup.getContext();
        int layoutIdForListItem=R.layout.ebay_list_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately=false;

        View view=inflater.inflate(layoutIdForListItem,viewGroup,shouldAttachToParentImmediately);
        EbayAdapterViewHolder ViewHolder=new EbayAdapterViewHolder(view);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(EbayAdapterViewHolder holder, int position) {
        String EbayData=mEbayData[position][0];
        String EbayUrl=mEbayData[position][2];
        String ebayPrice=mEbayData[position][3];
        Bitmap EbayImage=mImage[position];
        holder.mEbayItemImage.setImageBitmap(EbayImage);
        holder.mEbayTextView.setText(EbayData);
        holder.mEbayPriceTextView.setText(ebayPrice+" USD");

    }

    @Override
    public int getItemCount() {
        if(mEbayData==null)
        return 0;
        else
            return mEbayData.length;
    }


   public class EbayAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mEbayTextView;
        public final ImageView mEbayItemImage;
       public final TextView mEbayPriceTextView;


        public EbayAdapterViewHolder(View view) {
            super(view);
            mEbayTextView=(TextView) itemView.findViewById(R.id.ebay_results);
            mEbayItemImage=(ImageView) itemView.findViewById(R.id.image_display);
            mEbayPriceTextView=(TextView) itemView.findViewById(R.id.price_of_item);
            view.setOnClickListener(this);

        }

       @Override
       public void onClick(View view){
           int ClickedPosition=getAdapterPosition();
           String infoOfOneItem=mEbayData[ClickedPosition][1];
           mClickHandler.onListItemClick(infoOfOneItem);
       }
    }

    public void setEbayData(String[][] EbayData,Bitmap[] image){
        mEbayData=EbayData;
        mImage=image;
        notifyDataSetChanged();
    }
}
