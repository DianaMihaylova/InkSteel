package com.ink_steel.inksteel.helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ink_steel.inksteel.model.Studio;

public class StudioTypeAdapterFactory extends CustomizedTypeAdapterFactory<Studio> {


    public StudioTypeAdapterFactory() {
        super(Studio.class);
    }

    @Override
    protected void afterRead(Studio source, JsonElement deserialized) {

        JsonObject studio = deserialized.getAsJsonObject();
        if (studio.has("opening_hours")) {
            JsonObject openingHours = studio.get("opening_hours").getAsJsonObject();
            boolean isOpenNow = openingHours.get("open_now").getAsBoolean();
            source.setOpenNow(isOpenNow);
        }
        if (studio.has("photos")) {
            JsonArray photos = studio.get("photos").getAsJsonArray();
            if (photos.size() > 0) {
                // google places search returns at most 1 photo
                String photoReference = photos.get(0).getAsJsonObject().get("photo_reference")
                        .getAsString();
                source.setPhotoReference(photoReference);
            }
        }

        JsonObject geometry = studio.get("geometry").getAsJsonObject();
        JsonObject location = geometry.get("location").getAsJsonObject();
        LatLng latLng = new LatLng(location.get("lat").getAsDouble(),
                location.get("lng").getAsDouble());
        source.setLocation(latLng);
    }
}
