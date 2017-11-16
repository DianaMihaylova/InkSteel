package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.model.Studio;
import com.squareup.picasso.Picasso;

public class StudiosMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private View mView;
    private ImageView mImageView;
    private TextView mTextView;
    private RatingBar mRatingBar;
    private LatLng mLocation;
    private Listeners.OnReplaceFragmentListener mListener;

    public StudiosMapFragment() {
    }


    public static StudiosMapFragment newInstance(LatLng location) {
        StudiosMapFragment mapFragment = new StudiosMapFragment();
        Bundle bundle = new Bundle(1);
        bundle.putParcelable("location", location);
        mapFragment.setArguments(bundle);
        return mapFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            mListener = (Listeners.OnReplaceFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.child_fragment_studio_map, container,
                false);
        LayoutInflater inflater1 = LayoutInflater.from(getActivity());
        mView = inflater1.inflate(R.layout.item_studio_marker_info, null);
        mImageView = mView.findViewById(R.id.iv);
        mTextView = mView.findViewById(R.id.name);
        mRatingBar = mView.findViewById(R.id.rating_bar);
        mLocation = getArguments().getParcelable("location");
        MapFragment mMapFragment = (MapFragment)
                getChildFragmentManager().findFragmentById(R.id.fragment_studio_map);
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                String snippet = marker.getSnippet();
                String[] strings = snippet.split(",");
                float rating = Float.parseFloat(strings[0]);

                Picasso.with(mView.getContext())
                        .load(strings[1])
                        .placeholder(R.drawable.placeholder)
                        .into(mImageView);
                mTextView.setText(marker.getTitle());
                mRatingBar.setRating(rating);
                return mView;
            }
        });

        for (Studio studio : DatabaseManager.getStudiosManager().getStudios()) {
            LatLng position = studio.getLocation();
            if (mLocation == null)
                mLocation = position;
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .snippet(String.valueOf(studio.getRating()) + "," + studio.getPhotoUrl() + "," + studio.getPlaceId())
                    .title(studio.getName()));
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 12));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        String[] snippet = marker.getSnippet().split(",");

        mListener.replaceFragment(
                StudioInfoFragment.newInstance(snippet[2]));
    }

}
