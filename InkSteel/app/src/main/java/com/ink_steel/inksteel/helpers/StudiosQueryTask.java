package com.ink_steel.inksteel.helpers;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.ink_steel.inksteel.model.Studio;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class StudiosQueryTask extends AsyncTask<Void, Studio, Void> {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    private static final String QUERY_TEXT = "?query=";
    private static final String API_KEY = "&key=AIzaSyACnb8t5MxO5woaX7nQpS3qMowHnSZznno";

    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/" +
            "photo?maxwidth=600&photoreference=";

    private StudiosListener mListener;
    private Location mLocation;

    public StudiosQueryTask(StudiosListener listener, Location location) {
        mListener = listener;
        mLocation = location;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String url = BASE_URL + QUERY_TEXT + "tattoo" +
                "&location=" + mLocation.getLatitude() + "," + mLocation.getLongitude() +
                "&radius=40000" + API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                return null;

            extractStudiosFromJSON(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Error", e.getLocalizedMessage());
        }

        return null;
    }

    public interface StudiosListener {
        void onStudioLoaded(Studio studio);
    }

    @Override
    protected void onProgressUpdate(Studio... values) {
        mListener.onStudioLoaded(values[0]);
    }

    private void extractStudiosFromJSON(String jsonBody) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(jsonBody);
            JSONArray results;
            Studio studio;

            if (!jsonObject.has("results"))
                return;
            results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                studio = getStudioFromJSONObject(results.getJSONObject(i));
                publishProgress(studio);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Error", e.getLocalizedMessage());
        }
    }


    private Studio getStudioFromJSONObject(JSONObject object) throws JSONException, IOException {
        String place_id, name, photoReference, imageUrl = null;
        float rating;

        if (object.has("name"))
            name = object.getString("name");
        else name = "Name";
        if (object.has("place_id"))
            place_id = object.getString("place_id");
        else place_id = "placeid";
        if (object.has("rating"))
            rating = (float) object.getDouble("rating");
        else rating = 0;

        if (object.has("photos")) {
            JSONArray array = object.getJSONArray("photos");
            JSONObject photoObject = array.getJSONObject(0);
            if (photoObject.has("photo_reference")) {
                photoReference = photoObject.getString("photo_reference");
                imageUrl = getPhotoUrl(photoReference);
            }
        }

        return new Studio(name, rating, place_id, imageUrl);
    }

    private String getPhotoUrl(String photo) throws IOException {
        String url = PHOTO_BASE_URL + photo + API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.request().httpUrl().toString();
    }


}

