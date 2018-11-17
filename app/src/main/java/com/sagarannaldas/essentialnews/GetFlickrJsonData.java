package com.sagarannaldas.essentialnews;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagarannaldas on 12/09/17.
 */

class GetFlickrJsonData extends AsyncTask<String,Void ,List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null;
    private String mBaseUrl;
    private String mLanguage;
    private boolean mMatchAll;
    private boolean runningOnSameThread = false;

    private final OnDataAvailable mCallBack;

    interface OnDataAvailable{
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(OnDataAvailable CallBack , String BaseUrl, String Language, boolean MatchAll) {
        Log.d(TAG, "GetFlickrJsonData: called");
        this.mBaseUrl = BaseUrl;
        this.mLanguage = Language;
        this.mMatchAll = MatchAll;
        this.mCallBack = CallBack;
    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        String destinationUri = createUri(searchCriteria,mLanguage,mMatchAll);

        runningOnSameThread = true;

        GetRawData getrawdata = new GetRawData(this);
        getrawdata.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");

        if(mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList,DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts ");

        String destinationUri = createUri(params[0],mLanguage,mMatchAll);
        GetRawData getrawdata = new GetRawData(this);
        getrawdata.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");

        return mPhotoList ;
    }

    private String createUri(String searchCriteria, String language, Boolean matchAll){
        Log.d(TAG, "createUri: starts");

        return Uri.parse(mBaseUrl).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll  ? "ALL" : "ANY")
                .appendQueryParameter("lang", language)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback","1")
                .build().toString();

    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: status"+ status);

        if(status== DownloadStatus.OK){
            mPhotoList = new ArrayList<>();
            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for(int i =0;i<itemsArray.length();i++){

                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    String link =photoUrl.replaceFirst("_m.","_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags,photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: "+photoObject.toString());
                }
            }catch (JSONException jsone){
                jsone.printStackTrace();
                Log.e(TAG, "onDownloadComplete:  Error processiong json data"+jsone.getMessage());
                status=DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if( runningOnSameThread && mCallBack != null){
            //now inform the caller that processing is done - possibly return null if there
            //was a errror
            mCallBack.onDataAvailable(mPhotoList,status); 
        }

        Log.d(TAG, "onDownloadComplete: ends ");
    }
}
