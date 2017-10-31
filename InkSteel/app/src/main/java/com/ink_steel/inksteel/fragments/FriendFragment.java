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
import com.ink_steel.inksteel.helpers.IOnFriendClickListener;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;

public class FriendFragment extends Fragment implements IOnFriendClickListener, DatabaseManager.UsersListener {

    public static ArrayList<User> mUsers;
    private FriendAdapter mAdapter;
    private DatabaseManager mManager;

    public FriendFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.friend_rv);

        mManager = DatabaseManager.getInstance();
        mUsers = new ArrayList<>();
        mAdapter = new FriendAdapter(getActivity(), mUsers, this);

        onUsersLoaded();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onFriendClickListener(int position) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        ((HomeActivity) getActivity()).replaceFragment(fragment);
    }

    @Override
    public void onUsersLoaded() {
        mUsers.clear();
        mManager.loadUsers(this);
        mUsers.addAll(mManager.getUsers());
        mAdapter.notifyDataSetChanged();
    }
}
