package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.Studio;


public class StudioInformationFragment extends Fragment implements DatabaseManager.StudioListener {

    private TextView mName;
    private TextView mAddress;
    private TextView mNumber;
    private TextView mWebsite;
    private TextView mRating;
    private DatabaseManager mManager;

    public StudioInformationFragment() {
    }

    public static StudioInformationFragment newInstance(String placeId) {
        StudioInformationFragment studioInfoFragment = new StudioInformationFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("placeId", placeId);
        studioInfoFragment.setArguments(bundle);
        return studioInfoFragment;
    }

    private Studio mStudio;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_studio_information, container,
                false);

        mName = view.findViewById(R.id.studio_info_name);
        mAddress = view.findViewById(R.id.studio_info_address);
        mNumber = view.findViewById(R.id.studio_info_phone);
        mWebsite = view.findViewById(R.id.studio_info_website);
        mRating = view.findViewById(R.id.studio_info_rating);

        String studioId = getArguments().getString("placeId");
        mManager = DatabaseManager.getInstance();
        GeoDataClient geoDataClient = Places.getGeoDataClient(getActivity(), null);
        mManager.getStudioInfoById(studioId, geoDataClient, this);


        return view;
    }

    private void displayStudioInfo() {
        mName.setText(mStudio.getName());
        mAddress.setText(mStudio.getGooglePlace().getAddress());
        mNumber.setText(mStudio.getGooglePlace().getPhoneNumber());
        Uri website = mStudio.getGooglePlace().getWebsiteUri();
        if (website != null) {
            mWebsite.setText(website.toString());
        } else {
            mWebsite.setText("No website provided");
        }
        mRating.setText(String.valueOf(mStudio.getRating()));
    }

    @Override
    public void onStudioInfoLoaded(Studio studio) {
        mManager.setCurrentStudio(studio);
        mStudio = studio;
        displayStudioInfo();
    }
}
