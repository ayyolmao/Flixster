package com.example.flixster;

import android.os.Bundle;
import android.util.Log;

import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class MovieTrailerActivity extends YouTubeBaseActivity {

    // instance fields
    AsyncHttpClient client;

    Movie movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);

        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.api_key));
        client.get("https://api.themoviedb.org/3/movie/" + movie.getId() +"/videos", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
              try {
                  JSONArray results = response.getJSONArray("results");
                  String videoId = results.getJSONObject(0).getString("key");
                  Log.i("WHYYY", String.format("Loaded %s movies", results.length()));


                  // resolve the player view from the layout
                  YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);
                  play(playerView, videoId);

              } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // logE("Failed to get data from now_playing endpoint", throwable, true);
            }
        });

        // temporary test video id -- TODO replace with movie trailer video id


    }

    private void play(YouTubePlayerView playerView, String videoId) {
        // initialize with API key stored in secrets.xml
        playerView.initialize(getString(R.string.api_key_Youtube), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }
}
