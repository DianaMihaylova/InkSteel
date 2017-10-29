package com.ink_steel.inksteel.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.FullScreenViewAdapter;

public class FullScreenImageFragment extends Fragment {

    public FullScreenImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        int position = getArguments().getInt("image");

        HorizontalInfiniteCycleViewPager viewPager = (HorizontalInfiniteCycleViewPager)
                view.findViewById(R.id.view_pager_horizontal_cycle);
        FullScreenViewAdapter fullScreenViewAdapter = new FullScreenViewAdapter(getActivity().getApplicationContext());
        viewPager.setAdapter(fullScreenViewAdapter);
        viewPager.setCurrentItem(position);

        return view;
    }
}