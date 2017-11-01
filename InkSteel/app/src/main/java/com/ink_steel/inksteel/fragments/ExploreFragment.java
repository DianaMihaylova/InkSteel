package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
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

public class ExploreFragment extends Fragment implements DatabaseManager.UsersListener {

    private DatabaseManager mManager;
    private SwipeDeck cardStack;
    private ExploreAdapter adapter;
    private User mCurrentUser;
    private ArrayList<User> mUsers;

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mManager = DatabaseManager.getInstance();
        mCurrentUser = mManager.getCurrentUser();
        mUsers = new ArrayList<>();

        onUsersLoaded();
        cardStack = view.findViewById(R.id.swipe_deck);

        adapter = new ExploreAdapter(mUsers, getActivity());
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Toast.makeText(getActivity(), "U N L I K E", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cardSwipedRight(int position) {
                Toast.makeText(getActivity(), "L I K E", Toast.LENGTH_SHORT).show();
                String likesEmail = mUsers.get(position).getEmail();
                mManager.addLike(likesEmail);
                for (int i = 0; i < mCurrentUser.getLikedBy().size(); i++) {
                    String likedByEmail = mCurrentUser.getLikedBy().get(i);
                    if (likedByEmail.equals(likesEmail)) {
                        mManager.addFriend(likesEmail);
                    }
                }
            }

            @Override
            public void cardsDepleted() {
                Toast.makeText(getActivity(), "No more cards!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cardActionDown() {
            }

            @Override
            public void cardActionUp() {
            }
        });


        Button againBtn = view.findViewById(R.id.btn_again);
        againBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUsersLoaded();
                adapter = new ExploreAdapter(mUsers, getActivity());
                cardStack.setAdapter(adapter);
            }
        });

        return view;
    }


    @Override
    public void onUsersLoaded() {
        mUsers.clear();
        mManager.loadUsers(this);
        mUsers.addAll(mManager.getUsers());
    }
}