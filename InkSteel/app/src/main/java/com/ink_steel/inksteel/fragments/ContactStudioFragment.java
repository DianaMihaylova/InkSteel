package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.StudiosAdapter;
import com.ink_steel.inksteel.model.Studio;

import java.util.ArrayList;
import java.util.List;

public class ContactStudioFragment extends Fragment {


    public ContactStudioFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_studio, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.studios_rv);
        List<Studio> studios = new ArrayList<>();
        studios.add(new Studio("Name", 2.3f, R.drawable.tattoo));
        studios.add(new Studio("Studio", 4, R.drawable.tattoo3));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new StudiosAdapter(getActivity(), studios));

        return v;
    }

}
