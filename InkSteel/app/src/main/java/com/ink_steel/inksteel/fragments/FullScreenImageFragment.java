package com.ink_steel.inksteel.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.FullScreenViewAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.User;

public class FullScreenImageFragment extends Fragment {

    public FullScreenImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        int position = getArguments().getInt("image");
        int pos = getArguments().getInt("position");

        User user = DatabaseManager.getInstance().getCurrentUser();

        HorizontalInfiniteCycleViewPager viewPager = view.findViewById(R.id.view_pager_horizontal_cycle);
        FullScreenViewAdapter fullScreenViewAdapter = new FullScreenViewAdapter(getActivity(), user.getGallery());
        viewPager.setAdapter(fullScreenViewAdapter);
        viewPager.setCurrentItem(position);
        viewPager.setCurrentItem(pos);

        return view;
    }
}