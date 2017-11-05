package com.ink_steel.inksteel.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.StudiosAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragmentListener;
import com.ink_steel.inksteel.helpers.Listeners.StudioClickListener;
import com.ink_steel.inksteel.helpers.PermissionUtil;
import com.ink_steel.inksteel.helpers.StudiosQueryTask;
import com.ink_steel.inksteel.model.Studio;

import java.util.ArrayList;
import java.util.List;

import static com.ink_steel.inksteel.helpers.PermissionUtil.PermissionType;

public class StudiosFragment extends Fragment implements StudiosQueryTask.StudiosListener,
        StudioClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List<Studio> mStudios;
    private StudiosAdapter mAdapter;
    private OnReplaceFragmentListener mListener;
    private GoogleApiClient mClient;
    private DatabaseManager mManager;
    private GeoDataClient mGeoDataClient;
    private Location location;

    public StudiosFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof HomeActivity) {
            mListener = (HomeActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_studios, container, false);

        setUserVisibleHint(false);
        RecyclerView recyclerView = v.findViewById(R.id.studios_rv);
        mStudios = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new StudiosAdapter(this, mStudios);
        recyclerView.setAdapter(mAdapter);

        mManager = DatabaseManager.getInstance();

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        return v;
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
    public void onStudioLoaded(Studio studio) {
        mStudios.add(studio);
        mAdapter.notifyDataSetChanged();
        mManager.getStudioInfoById(studio.getPlaceId(), mGeoDataClient);
    }

    @Override
    public void onStudiosLoaded() {
        mStudios.addAll(mManager.getStudios());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoStudios() {
        Toast.makeText(getActivity(), R.string.no_studios, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStudioClick(int position) {
        mListener.replaceFragment(StudioInfoFragment.newInstance(mStudios.get(position).getPlaceId()));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.requestPermission(this, PermissionType.LOCATION);
        } else {
            getStudios(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PermissionUtil.PERMISSION_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStudios(true);
            } else {
                getStudios(false);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    private void getStudios(boolean hasPermission) {
        if (hasPermission) {
            FusedLocationProviderClient client =
                    LocationServices.getFusedLocationProviderClient(getActivity());
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task1) {
                    location = task1.getResult();
                    if (location == null) {
                        mManager.getNearbyProfileCityStudios(StudiosFragment.this, mManager
                                .getCurrentUser().getCity());
                    } else {
                        mManager.getNearbyStudios(StudiosFragment.this, location);
                    }
                }
            });
        } else {
            mManager.getNearbyProfileCityStudios(StudiosFragment.this, mManager
                    .getCurrentUser().getCity());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}