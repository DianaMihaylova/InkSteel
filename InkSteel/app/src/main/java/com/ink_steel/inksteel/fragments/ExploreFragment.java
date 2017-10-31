package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.ExploreAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.User;
import com.tmall.ultraviewpager.UltraViewPager;

import java.util.ArrayList;

public class ExploreFragment extends Fragment implements DatabaseManager.UsersListener {

    private ArrayList<User> users;
    private ExploreAdapter mAdapter;
    private DatabaseManager mManager;
    private User mCurrentUser;

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        Switch autoScroll = view.findViewById(R.id.switch_auto_scroll);
        Button likeBtn = view.findViewById(R.id.btn_like);

        final UltraViewPager ultraViewPager = view.findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.VERTICAL);
        ultraViewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        ultraViewPager.setInfiniteLoop(true);

        users = new ArrayList<>();
        mManager = DatabaseManager.getInstance();
        mCurrentUser = mManager.getCurrentUser();

        mAdapter = new ExploreAdapter(getActivity(), users);
        ultraViewPager.setAdapter(mAdapter);

        autoScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ultraViewPager.setAutoScroll(5000);
                } else {
                    ultraViewPager.disableAutoScroll();
                }
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.loadUsers(ExploreFragment.this);
                onUsersLoaded();
                Toast.makeText(getActivity(), "You like it!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onUsersLoaded() {
        users.clear();
        users.addAll(mManager.getUsers());
        mAdapter.notifyDataSetChanged();
    }
}