package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.StudiosAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragmentListener;
import com.ink_steel.inksteel.model.Studio;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudiosListFragment extends Fragment implements Listeners.StudioClickListener {

    @BindView(R.id.fragment_studios_rv)
    RecyclerView mRecyclerView;
    private StudiosAdapter mAdapter;
    private OnReplaceFragmentListener mListener;
    private ArrayList<Studio> mStudios;

    public StudiosListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            mListener = (OnReplaceFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.child_fragment_studios_list, container,
                false);
        ButterKnife.bind(this, view);
        mStudios = new ArrayList<>();
        DatabaseManager.StudiosManager manager = DatabaseManager.getStudiosManager();
        if (manager.getStudios() != null)
            mStudios.addAll(manager.getStudios());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new StudiosAdapter(this, mStudios);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStudioClick(int position) {
        mListener.replaceFragment(
                StudioInfoFragment.newInstance(mStudios.get(position).getPlaceId()));
    }

    public void addStudio(final Studio studio) {
        mStudios.add(studio);
        mAdapter.notifyItemInserted(mStudios.size() - 1);
    }
}
