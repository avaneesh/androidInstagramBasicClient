package com.instastuff.instaclient2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends Activity {

    private static final String TAG = "Insta2";
    private static final String CLIENT_ID = "92412292374c42ffae8e1033d888dfc0";

    ArrayList<InstagramPhoto> instagramPhotoArrayList;
    ListView lvPhotos;

    // Declare custom adapter
    InstagramPhotosAdapter aphoto;

    private SwipeRefreshLayout sdRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // initialize array list to hold list of photos
        instagramPhotoArrayList = new ArrayList();
        // Find ListView
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        // get custom adapter
        aphoto = new InstagramPhotosAdapter(this, instagramPhotoArrayList);
        // link ListView to custom adapter
        lvPhotos.setAdapter(aphoto);

        sdRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        sdRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

        sdRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Get photos using instrgram API and populate the array list
        fetchPopularPhotos();
    }

    private void fetchPopularPhotos(){
        /*
            https://api.instagram.com/v1/media/popular?client_id=92412292374c42ffae8e1033d888dfc0
            - Response JSON:
            Type: { “data"  ==> [x] ==> "type” } (image or video)
            URL: { “data"  ==> [x] ==> “images” ==> “standard_resolution” ==> “url” } (image or video)
            Caption: { “data"  ==> [x] ==> “caption” ==> “text" } (image or video)
            Author: { “data"  ==> [x] ==> “user” ==> “username" }
         */
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();

        Log.i(TAG, "Sending URL: "+url);

        client.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.i(TAG, "Got Response: "+response.toString());

                instagramPhotoArrayList.clear();

                // Interate each photo and decode into java object
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data");

                    // iterate photos array
                    for (int i = 0; i < photosJSON.length(); i++){
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        // decode into data model
                        InstagramPhoto iPhoto = new InstagramPhoto();

                        //Author: { “data"  ==> [x] ==> “user” ==> “username" }
                        iPhoto.username = photoJSON.getJSONObject("user").getString("username");

                        //Profile Pic: { “data"  ==> [x] ==> “user” ==> “profile_picture" }
                        iPhoto.profilePicURL = photoJSON.getJSONObject("user").getString("profile_picture");

                        //Caption: { “data"  ==> [x] ==> “caption” ==> “text" } (image or video)
                        iPhoto.caption = photoJSON.getJSONObject("caption").getString("text");

                        //URL: { “data"  ==> [x] ==> “images” ==> “standard_resolution” ==> “url” } (image or video)
                        iPhoto.imageURL = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

                        iPhoto.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                        //Timestamp: { “data"  ==> [x] ==> “created_time" }
                        iPhoto.timestamp = new Integer(photoJSON.getString("created_time"));

                        //Log.i(TAG, "Got photo :"+i+" url: "+iPhoto.imageURL);
                        instagramPhotoArrayList.add(iPhoto);

                        // what if we call notify-data-changed everytime?
                        aphoto.notifyDataSetChanged();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                sdRefresh.setRefreshing(false);

                // Notify custom adapter that data has been changed, so it can start rendering it.
                //aphoto.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
