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
import java.util.TreeSet;

public class ExploreFragment extends Fragment implements DatabaseManager.UsersListener {

    private DatabaseManager mManager;
    private SwipeDeck cardStack;
    private ExploreAdapter mAdapter;
    private ArrayList<User> mUsers;
    private Toast mToast;

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mManager = DatabaseManager.getInstance();
        cardStack = view.findViewById(R.id.swipe_deck);

        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        loadExpolre();
        setCardStackEvent();

        Button againBtn = view.findViewById(R.id.btn_again);
        againBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadExpolre();
            }
        });

        return view;
    }

    public void setCardStackEvent() {
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                mToast.setText("U N L I K E");
                mToast.show();
            }

            @Override
            public void cardSwipedRight(int position) {
                mToast.setText("L I K E");
                mToast.show();
                User likedUser = mUsers.get(position);
                String likedEmail = likedUser.getEmail();
                mManager.addLike(likedEmail);
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
        });
    }

    private void loadExpolre() {
        mUsers = new ArrayList<>();
        mAdapter = new ExploreAdapter(mUsers, getActivity());
        mManager.loadExplore(this);
        cardStack.setAdapter(mAdapter);

    }

    @Override
    public void onUsersLoaded() {
        mUsers.clear();
        mUsers.addAll(mManager.getExploreUsers());
        mAdapter.notifyDataSetChanged();
    }
}