package com.ink_steel.inksteel.model;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post {

    private String user;
    private Date date;
    private Uri profileUri;
    private Uri imageUrl;

    public Post(String user, Date date, Uri profileUri, Uri imageUrl) {
        this.user = user;
        this.date = date;
        this.imageUrl = imageUrl;
        this.profileUri = profileUri;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        String today = new SimpleDateFormat("dd MMMM",
                Locale.US).format(new Date());
        if (today.equals(new SimpleDateFormat("dd MMMM",
                Locale.US).format(date)))
            return new SimpleDateFormat("h:mm",
                    Locale.US).format(date) + ", Today";

        return new SimpleDateFormat("h:mm, dd MMMM", Locale.US).format(date);
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public Uri getProfileUri() {
        return profileUri;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Post))
            return false;

        Post other = (Post) obj;

        if (!user.equals(other.user))
            return false;

        if (!date.equals(other.date))
            return false;

        if (!profileUri.equals(other.profileUri))
            return false;

        return profileUri.equals(other.profileUri);

    }
}
