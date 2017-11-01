package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.StudiosAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners.StudioClickListener;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragment;
import com.ink_steel.inksteel.helpers.StudiosQueryTask;
import com.ink_steel.inksteel.model.Studio;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;
import java.util.List;

public class StudiosFragment extends Fragment implements StudiosQueryTask.StudiosListener,
        StudioClickListener {

    private List<Studio> mStudios;
    private StudiosAdapter mAdapter;
    private OnReplaceFragment mListener;

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
        View v = inflater.inflate(R.layout.fragment_contact_studio, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.studios_rv);
        mStudios = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new StudiosAdapter(this, mStudios);
        recyclerView.setAdapter(mAdapter);

        DatabaseManager manager = DatabaseManager.getInstance();

        User user = manager.getCurrentUser();

        StudiosQueryTask task = new StudiosQueryTask(this);
        task.execute();

        return v;
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
}
