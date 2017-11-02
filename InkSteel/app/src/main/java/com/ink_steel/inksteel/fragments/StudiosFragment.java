package com.ink_steel.inksteel.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.StudiosAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragment;
import com.ink_steel.inksteel.helpers.Listeners.StudioClickListener;
import com.ink_steel.inksteel.helpers.StudiosQueryTask;
import com.ink_steel.inksteel.model.Studio;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;
import java.util.List;

public class StudiosFragment extends Fragment implements StudiosQueryTask.StudiosListener,
        StudioClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List<Studio> mStudios;
    private StudiosAdapter mAdapter;
    private OnReplaceFragment mListener;
    private GoogleApiClient mClient;

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

        RecyclerView recyclerView = v.findViewById(R.id.studios_rv);
        mStudios = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new StudiosAdapter(this, mStudios);
        recyclerView.setAdapter(mAdapter);

        DatabaseManager manager = DatabaseManager.getInstance();

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        User user = manager.getCurrentUser();


        return v;
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
    public void onStudioLoaded(Studio studio) {
        mStudios.add(studio);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStudioClick(int position) {
        mListener.replaceFragment(StudioInfoFragment.newInstance(mStudios.get(position).getPlaceId()));
    }

    Location location;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getActivity());

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task1) {
                    location = task1.getResult();
                    if (location == null) {
                        location = new Location("");
                        location.setLatitude(42.698334);
                        location.setLongitude(23.319941);
                    }

                    StudiosQueryTask task = new StudiosQueryTask(StudiosFragment.this,
                            location);
                    task.execute();
                }
            });
        } catch (SecurityException e) {
            Log.d("omg", "omg");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
