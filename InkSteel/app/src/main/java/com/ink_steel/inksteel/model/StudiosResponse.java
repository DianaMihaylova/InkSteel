package com.ink_steel.inksteel.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StudiosResponse {

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;

    @SerializedName("results")
    @Expose
    private ArrayList<Studio> studios;

    @SerializedName("html_attributions")
    @Expose
    private ArrayList<Object> htmlAttributions;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public ArrayList<Studio> getStudios() {
        return studios;
    }

    public void setStudios(ArrayList<Studio> studios) {
        this.studios = studios;
    }

    public ArrayList<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(ArrayList<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }
}
