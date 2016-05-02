package com.patcomp.instagramclient;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";
    public ArrayList<InstagramPhoto> photos;
    InstagramPhotosAdapter aPhotos;
    public ListView lvPhotos;
    private SwipeRefreshLayout swipeContainer;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        photos = new ArrayList<InstagramPhoto>();

        // send API request to popular instagram photos

        aPhotos = new InstagramPhotosAdapter(this,photos);
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                aPhotos.clear();
                fetchPopularPhotos();
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();


    }


    // send network request
    public void fetchPopularPhotos() {

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
// e05c462ebd86446ea48a5af73769b602
        // creating the network client
        client = new AsyncHttpClient();
        // trigger the get request
        client.get(url,null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray photosJSON = null;

                try {
                    photosJSON = response.getJSONArray("data");

                    for (int i=0; i < photosJSON.length(); i++){
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        if (i==0)
                            Log.d("aaaa",photoJSON.toString());

                        InstagramPhoto photo = new InstagramPhoto();
                        if (photoJSON.getJSONObject("user") != null) {
                            photo.username = photoJSON.getJSONObject("user").getString("username");
                            photo.profileImageUrl = photoJSON.getJSONObject("user").getString("profile_picture");
                        }

                        ArrayList<InstagramComment> comments = new ArrayList<InstagramComment>();

                        if (photoJSON.getJSONObject("comments") != null) {
                            JSONObject commentJSON = photoJSON.getJSONObject("comments");
                            photo.commentsCount = commentJSON.getInt("count");
                            JSONArray commentsJSON_array = commentJSON.getJSONArray("data");




                            for (int j=0; j < commentsJSON_array.length(); j++){
                                InstagramComment comment = new InstagramComment();
                                JSONObject commentJSON_object = commentsJSON_array.getJSONObject(j);
                                comment.text = commentJSON_object.getString("text");

                                if (commentJSON_object.getJSONObject("from") != null){
                                    comment.username = commentJSON_object.getJSONObject("from").getString("username");
                                    comment.photoUrl = commentJSON_object.getJSONObject("from").getString("profile_picture");
                                    comment.fullName = commentJSON_object.getJSONObject("from").getString("full_name");
                                }
                                comments.add(comment);
                            }

                        }
                        photo.comments = comments;

                        if (photoJSON.getJSONObject("caption") != null) {

                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                            photo.timespan = DateUtils.getRelativeTimeSpanString(
                                    photoJSON.getJSONObject("caption").getLong("created_time")*1000,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS).toString();
                        }

                        if (photoJSON.getJSONObject("images") != null && photoJSON.getJSONObject("images").getJSONObject("standard_resolution")!= null ) {
                            photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                            photo.imagHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                            photo.imageWidth = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");

                        }

                        if (photoJSON.getJSONObject("likes") != null)
                            photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");




                        photos.add(photo);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
                aPhotos.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
             // Do something
            }
        });
    }
}
