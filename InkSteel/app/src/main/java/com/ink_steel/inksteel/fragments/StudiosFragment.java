package com.ink_steel.inksteel.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.PermissionHelper;
import com.ink_steel.inksteel.helpers.PermissionHelper.PermissionType;
import com.ink_steel.inksteel.model.Studio;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudiosFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DatabaseManager.StudioInfoListener {

    @BindView(R.id.fragment_studios_btn)
    FloatingActionButton mMapBtn;

    private GoogleApiClient mClient;
    private DatabaseManager.StudiosManager mManager;
    private boolean isListView;

    private StudiosListFragment mStudiosListFragment;
    private StudiosMapFragment mStudiosMapFragment;

    public StudiosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_studios, container, false);
        ButterKnife.bind(this, view);

        setUserVisibleHint(false);
        isListView = true;
        mManager = DatabaseManager.getStudiosManager();

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mStudiosListFragment = new StudiosListFragment();
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_studios_container, mStudiosListFragment)
                .commit();

        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListView) {
                    FragmentManager manager = getChildFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.fragment_studios_container, mStudiosMapFragment)
                            .commit();
                    mMapBtn.setImageResource(R.drawable.ic_list_black_24dp);
                    isListView = false;
                } else {
                    FragmentManager manager = getChildFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.fragment_studios_container, mStudiosListFragment)
                            .commit();
                    mMapBtn.setImageResource(R.drawable.ic_map_black_36dp);
                    isListView = true;
                }
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (isVisibleToUser) {
                mClient.connect();
            } else {
                mClient.disconnect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getStudios();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_COARSE_LOCATION) {
            mManager.clearStudios();
            getStudios();
        }
    }

    private void getStudios() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient client =
                    LocationServices.getFusedLocationProviderClient(getActivity());
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        mManager.getNearbyStudios(StudiosFragment.this, location);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mStudiosMapFragment = StudiosMapFragment.newInstance(latLng);
                    }
                }
            });
        } else {
            mManager.getNearbyStudios(StudiosFragment.this, null);
            mStudiosMapFragment = StudiosMapFragment.newInstance(null);
            PermissionHelper.requestPermission(this, PermissionType.LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onStudioInfoLoaded() {
        mMapBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStudioLoaded(Studio source) {
        mStudiosListFragment.addStudio(source);
    }
}