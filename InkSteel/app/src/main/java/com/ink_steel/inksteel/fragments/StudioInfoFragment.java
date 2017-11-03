package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.Studio;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudioInfoFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String mId;
    private GoogleApiClient mClient;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private int mI;
    private ArrayList<String> mStrings;

    public StudioInfoFragment() {
    }

    public static StudioInfoFragment newInstance(String studioId) {
        StudioInfoFragment studioInfoFragment = new StudioInfoFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("studioId", studioId);
        studioInfoFragment.setArguments(bundle);
        return studioInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studio_info, container, false);

        mId = getArguments().getString("studioId");

        final ImageView imageView = view.findViewById(R.id.studio_image);
        mViewPager = view.findViewById(R.id.view_pager);
        mTabLayout = view.findViewById(R.id.tabs);
        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        DatabaseManager manager = DatabaseManager.getInstance();
        final Studio studio = manager.getStudioById(mId);
        Picasso.with(getActivity())
                .load(studio.getImageUrl())
                .into(imageView);

        return view;
    }


    @Override
    public void onStart() {
        mClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mViewPager.setAdapter(new StudioPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
//        mTabLayout.addTab(mTabLayout.newTab());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class StudioPagerAdapter extends FragmentPagerAdapter {

        private static final int FRAGMENT_COUNT = 2;

        StudioPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return StudioInformationFragment.newInstance(mId);
            return GoogleMapFragment.newInstance(mId);
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Info";
            return "Map";
        }
    }

}
