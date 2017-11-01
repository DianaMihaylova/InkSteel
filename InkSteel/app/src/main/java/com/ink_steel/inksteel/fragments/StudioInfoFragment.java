package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StudioInfoFragment extends Fragment {

    public StudioInfoFragment() {
    }

    public static StudioInfoFragment newInstance(String postId) {
        StudioInfoFragment studioInfoFragment = new StudioInfoFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("postId", postId);
        studioInfoFragment.setArguments(bundle);
        return studioInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {


        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
