package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
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

    public static FullScreenImageFragment newInstance(int position, User u) {
        Bundle bundle = new Bundle();
        bundle.putInt("image", position);
        bundle.putSerializable("friend", u);
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        int position = getArguments().getInt("image");
        User friend = (User) getArguments().getSerializable("friend");

        User mCurrentUser = DatabaseManager.getInstance().getCurrentUser();

        HorizontalInfiniteCycleViewPager viewPager = view.findViewById(R.id.view_pager_horizontal_cycle);
        FullScreenViewAdapter fullScreenViewAdapter;
        if (friend == null) {
            fullScreenViewAdapter = new FullScreenViewAdapter(getActivity(), mCurrentUser.getGallery());
        } else {
            fullScreenViewAdapter = new FullScreenViewAdapter(getActivity(), friend.getGallery());
        }
        viewPager.setAdapter(fullScreenViewAdapter);
        viewPager.setCurrentItem(position);

        return view;
    }
}