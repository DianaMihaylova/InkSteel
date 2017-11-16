package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudioInfoFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, DatabaseManager.StudioInfoListener {


    private static final String STUDIO_ID = "studioId";
    @BindView(R.id.fragment_studio_info_rb)
    RatingBar mRatingBar;
    @BindView(R.id.fragment_studio_info_name_tv)
    TextView mNameTv;
    @BindView(R.id.fragment_studio_info_iv)
    ImageView mStudioImage;
    @BindView(R.id.fragment_studio_info_address_tv)
    TextView mAddressTv;
    @BindView(R.id.fragment_studio_info_phone_btn)
    ImageButton mPhoneBtn;
    @BindView(R.id.fragment_studio_info_website_btn)
    ImageButton mWebsiteBtn;
    private GoogleApiClient mClient;
    private Studio mStudio;
    private DatabaseManager.StudiosManager mManager;
    private GoogleMap mMap;
    public StudioInfoFragment() {
    }

    public static StudioInfoFragment newInstance(String studioId) {
        StudioInfoFragment studioInfoFragment = new StudioInfoFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(STUDIO_ID, studioId);
        studioInfoFragment.setArguments(bundle);
        return studioInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studio_info, container, false);
        ButterKnife.bind(this, view);
        String mId = getArguments().getString(STUDIO_ID);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mManager = DatabaseManager.getStudiosManager();

        mStudio = mManager.getStudioById(mId);

        String url = mStudio.getPhotoUrl();

        if (url != null) Picasso.with(getActivity())
                .load(url)
                .into(mStudioImage);
        else mStudioImage.setImageResource(R.drawable.placeholder);

        MapFragment mMapFragment = (MapFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_studio_info_map);
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
//        mManager.loadStudioInfoById(mStudio.getPlaceId(), geoDataClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng position = mStudio.getLocation();
        showMarker(position);
    }

    private void showMarker(LatLng position) {
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(mStudio.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
    }

    private void displayInfo() {
//        mNameTv.setText(mStudio.getGooglePlace().getName());
//        mAddressTv.setText(mStudio.getGooglePlace().getAddress());

//        mPhoneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
////                intent.setData(Uri.parse(getString(R.string.tel) + mStudio.getGooglePlace().getPhoneNumber()));
//            }
//        });

//        mWebsiteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri website = mStudio.getGooglePlace().getWebsiteUri();
//                if (website != null) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(website);
//                    startActivity(i);
//                } else {
//                    mWebsiteBtn.setImageResource(R.drawable.ic_earth_off_black_48dp);
//                    mWebsiteBtn.setEnabled(false);
//                }
//            }
//        });

        mRatingBar.setRating(mStudio.getRating());
    }

    @Override
    public void onStudioInfoLoaded() {
//        displayInfo();
        showMarker(mStudio.getLocation());
    }

    @Override
    public void onStudioLoaded(Studio source) {

    }
}