package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.Studio;
import com.squareup.picasso.Picasso;

public class StudioInfoFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, DatabaseManager.StudioListener {

    private GoogleApiClient mClient;
    private TextView mName;
    private TextView mAddress;
    private ImageButton mNumber;
    private ImageButton mWebsite;
    private RatingBar mRating;
    private Studio mStudio;
    private DatabaseManager mManager;
    private MapFragment mMapFragment;

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
        View view = inflater.inflate(R.layout.fragment_studio_text_info,
                container, false);

        String mId = getArguments().getString("studioId");

        mName = view.findViewById(R.id.studio_text_title);
        mAddress = view.findViewById(R.id.studio_text_address);
        mNumber = view.findViewById(R.id.studio_text_phone);
        mWebsite = view.findViewById(R.id.studio_text_website);
        mRating = view.findViewById(R.id.studio_text_rating);

        ImageView imageView = view.findViewById(R.id.studio_text_image);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mManager = DatabaseManager.getInstance();
        mStudio = mManager.getStudioById(mId);
        String url = mStudio.getImageUrl();
        if (url != null)
            Picasso.with(getActivity())
                .load(mStudio.getImageUrl())
                .into(imageView);
        else imageView.setImageResource(R.drawable.placeholder);
        mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        MapFragment mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);
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
        GeoDataClient geoDataClient = Places.getGeoDataClient(getActivity(), null);
        mManager.getStudioInfoById(mStudio.getPlaceId(), geoDataClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        LatLng position = mStudio.getGooglePlace().getLatLng();
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(mStudio.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
    }

    @Override
    public void onStudioInfoLoaded(Studio studio) {
        mStudio = studio;
        displayInfo();
    }

    private void displayInfo() {
        mName.setText(mStudio.getGooglePlace().getName());
        mAddress.setText(mStudio.getGooglePlace().getAddress());

        mNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(getString(R.string.tel) + mStudio.getGooglePlace().getPhoneNumber()));
            }
        });

        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri website = mStudio.getGooglePlace().getWebsiteUri();
                if (website != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(website);
                    startActivity(i);
                } else {
                    mWebsite.setImageResource(R.drawable.ic_earth_off_black_48dp);
                    mWebsite.setEnabled(false);
                }
            }
        });

        mRating.setRating(mStudio.getRating());
    }
}