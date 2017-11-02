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
    private ExploreAdapter mAdapter;
    private User mCurrentUser;
    private ArrayList<User> mUsers;
    private Toast mToast;

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mManager = DatabaseManager.getInstance();
        mCurrentUser = mManager.getCurrentUser();
        mUsers = new ArrayList<>();
        mManager.loadUsers(this);
        cardStack = view.findViewById(R.id.swipe_deck);
        mAdapter = new ExploreAdapter(mUsers, getActivity());
        onUsersLoaded();
        setCardStackEvent();
        cardStack.setAdapter(mAdapter);

        Button againBtn = view.findViewById(R.id.btn_again);
        againBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUsersLoaded();
                mAdapter = new ExploreAdapter(mUsers, getActivity());
                cardStack.setAdapter(mAdapter);
            }
        });

        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        return view;
    }

    public void setCardStackEvent() {
        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
//                Toast.makeText(getActivity(), "U N L I K E", Toast.LENGTH_SHORT).show();
                mToast.setText("Unlike");
                mToast.show();
            }

            @Override
            public void cardSwipedRight(int position) {
//                Toast.makeText(getActivity(), "L I K E", Toast.LENGTH_SHORT).show();
                mToast.setText("Like");
                mToast.show();
                User likedUser = mUsers.get(position);
                String likedEmail = likedUser.getEmail();
                mManager.addLike(likedEmail);
                if (likedUser.getLiked().contains(mCurrentUser.getEmail())) {
                    mManager.addFriend(likedEmail);
                }
            }

            @Override
            public void cardsDepleted() {
                mToast.setText("No more cards!");
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


    @Override
    public void onUsersLoaded() {
        mUsers.clear();
//        mUsers.add(new User("user1@gmail.com", "User1", "20", "Sofia",
//                "https://woman.hotnews.bg/static/uploads/gallery/141/main/" +
//                        "00000443668367_1543019770044_1071469371_1591302_2872592_n.jpg"));
//        mUsers.add(new User("user2@gmail.com", "User2", "20", "Sofia",
//                "http://div.bg/pictures/1454321_824_.jpg"));
//        mUsers.add(new User("user3@gmail.com", "User3", "20", "Sofia",
//                "http://znachenie-tattoo.ru/udata/files/0/385/xxxlarge-529" +
//                        "2328649163f0db844c5aaeff93dbd.jpg"));
//        mUsers.add(new User("user4@gmail.com", "User4", "20", "Sofia",
//                "http://danielhuscroft.com/wp-content/uploads/2017/07/rose-tattoo" +
//                        "-on-hip-8-512a8b3ac647b5dc9aaf2d5163310d61-jpg.jpg"));
//        mUsers.add(new User("user5@gmail.com", "User5", "20", "Sofia",
//                "https://i.pinimg.com/736x/63/2b/15/632b1509f14c586edf0dcd" +
//                        "175a3d8829--men-tribal-tattoos-tribal-shoulder-tattoos.jpg"));

        mUsers.addAll(mManager.getUsers());
//        mUsers = mManager.loadExplore(this);
        mAdapter.notifyDataSetChanged();
    }
}