package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.FriendAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners.FriendClickListener;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements FriendClickListener, DatabaseManager.UsersListener {

    private ArrayList<User> mFriends;
    private DatabaseManager.UsersManager mManager;
    private FriendAdapter mAdapter;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.friend_rv);

        mManager = DatabaseManager.getUsersManager();
        mFriends = mManager.getFriends(this);
        if (mFriends == null)
            mFriends = new ArrayList<>();
        mAdapter = new FriendAdapter(getActivity(), mFriends, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onFriendClick(int position) {
        ProfileFragment fragment = new ProfileFragment();
        User friend = mFriends.get(position);
        Bundle bundle = new Bundle(1);
        bundle.putSerializable("friend", friend);
        fragment.setArguments(bundle);
        ((HomeActivity) getActivity()).replaceFragment(fragment);
    }

    @Override
    public void onUsersLoaded() {
        mFriends.addAll(mManager.getFriends(this));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String errorMessage) {

    }
}