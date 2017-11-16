package com.ink_steel.inksteel.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.daprlabs.cardstack.SwipeDeck;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.ExploreAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;


public class ExploreFragment extends Fragment implements SwipeDeck.SwipeEventCallback, DatabaseManager.UsersListener {

    private DatabaseManager.UsersManager mManager;
    private Toast mToast;
    private ArrayList<User> mUsers;
    private ExploreAdapter mExploreAdapter;

    @SuppressLint("ShowToast")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mManager = DatabaseManager.getUsersManager();
        mManager.getUsers(this);
        SwipeDeck cardStack = view.findViewById(R.id.swipe_deck);
        mUsers = new ArrayList<>();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        cardStack.setEventCallback(this);
        mExploreAdapter = new ExploreAdapter(mUsers, getActivity());
        cardStack.setAdapter(mExploreAdapter);
        Button againBtn = view.findViewById(R.id.btn_again);
        againBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.getUsers(ExploreFragment.this);
            }
        });

        return view;
    }

    @Override
    public void cardSwipedLeft(int position) {
        mToast.setText("U N L I K E");
        mToast.show();
    }

    @Override
    public void cardSwipedRight(int position) {
        mToast.setText("L I K E");
        mToast.show();
        mManager.like(mUsers.get(position).getEmail());
    }

    @Override
    public void cardsDepleted() {
        mToast.setText("No more cards for users!");
        mToast.show();
    }

    @Override
    public void cardActionDown() {

    }

    @Override
    public void cardActionUp() {

    }

    @Override
    public void onUsersLoaded() {
        mUsers.addAll(mManager.getExploreUsers());
        mExploreAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String errorMessage) {

    }
}
