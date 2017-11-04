package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.Studio;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {

    private com.google.android.gms.maps.MapFragment mMapFragment;
    private GoogleMap mMap;
    private Studio mStudio;
    private String mId;

    public GoogleMapFragment() {
    }

    public static GoogleMapFragment newInstance(String studioId) {
        GoogleMapFragment googleMapFragment = new GoogleMapFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("studioId", studioId);
        googleMapFragment.setArguments(bundle);
        return googleMapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mId = getArguments().getString("studioId");


        mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

//        Toast.makeText(getActivity(), "OMGMOGM", Toast.LENGTH_SHORT).show();
//        mMapFragment.getMapAsync(this);

        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseManager manager = DatabaseManager.getInstance();
        mStudio = manager.getStudioById(mId);
        LatLng position = mStudio.getGooglePlace().getLatLng();
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(mStudio.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
    }
}
