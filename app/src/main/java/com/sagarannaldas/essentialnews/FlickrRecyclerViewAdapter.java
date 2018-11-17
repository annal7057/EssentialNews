package com.sagarannaldas.essentialnews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sagarannaldas on 14/09/17.
 */

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {

    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotosList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context mContext ,List<Photo> mPhotosList) {
        this.mContext = mContext;
        this.mPhotosList = mPhotosList;
        Log.d(TAG, "FlickrRecyclerViewAdapter: constructor");
        Log.d(TAG, "FlickrRecyclerViewAdapter: "+mPhotosList);

    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         //called by a layout manager when it needs a view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse,parent,false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        //called by layout manager when it wants new data in a existing row

        Photo photoItem = mPhotosList.get(position);
        Log.d(TAG, "onBindViewHolder: "+ photoItem.getTitle()+"--->" + position);
        Picasso.with(mContext).load(photoItem.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);

        holder.title.setText(photoItem.getTitle());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        Log.d(TAG, "getItemCount: " +((mPhotosList != null) && (mPhotosList.size() !=0) ? mPhotosList.size() : 0));
        return ((mPhotosList != null) && (mPhotosList.size() !=0) ? mPhotosList.size() : 0);
    }

    void loadNewData(List<Photo> newPhotos){
        mPhotosList= newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return((mPhotosList != null) && (mPhotosList.size() != 0) ? mPhotosList.get(position) : null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrViewImageHolder";
        ImageView thumbnail;
        TextView title = null;

        public FlickrImageViewHolder(View itemView){
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title=(TextView) itemView.findViewById(R.id.title);
        }

    }
}