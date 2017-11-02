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
            "photo?maxwidth=400&photoreference=";

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
//            ArrayList<Studio> studios = new ArrayList<>();
            JSONArray results;
            Studio studio;

            if (!jsonObject.has("results"))
                return;
            results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                studio = getStudioFromJSONObject(results.getJSONObject(i));
                if (studio != null) {
                    publishProgress(studio);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Error", e.getLocalizedMessage());
        }
    }

    private Studio getStudioFromJSONObject(JSONObject object) throws JSONException, IOException {
        String address, place_id, name, photo;
        float rating;
        JSONArray photos;

        if (object.has("formatted_address"))
            address = object.getString("formatted_address");
        else address = "Address";
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
            photos = object.getJSONArray("photos");

            JSONObject photoObject = photos.getJSONObject(0);
            if (photoObject.has("photo_reference")) {
                photo = photoObject.getString("photo_reference");
                photo = getPhotoUrl(photo);
            } else photo = "";
        } else photo = "";

        if (photo.isEmpty())
            return null;
        return new Studio(name, rating, address, place_id, photo);
    }

    private String getPhotoUrl(String photo) throws IOException {
        String url = PHOTO_BASE_URL + photo + API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.request().httpUrl().toString();
    }

}

