package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.ChatListAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;


public class ChatListFragment extends Fragment implements Listeners.ChatListClickListener, DatabaseManager.ChatRoomsLoadedListener {

    private DatabaseManager mManager;
    private ChatListAdapter mAdapter;

    public ChatListFragment() {
    }

    private ArrayList<ChatRoom> mChatRooms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        mManager = DatabaseManager.getInstance();
        mManager.loadChatRooms(this);

        FloatingActionButton fab = view.findViewById(R.id.chat_list_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).replaceFragment(new FriendsFragment());
            }
        });

        mChatRooms = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.chat_list_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mAdapter = new ChatListAdapter(this, mChatRooms, mManager.getCurrentUser().getEmail());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onChatItemClick(int position) {
        ((HomeActivity) getActivity()).replaceFragment(ChatFragment
                .newInstance(mChatRooms.get(position)
                        .getOtherUserEmail(mManager.getCurrentUser().getEmail())));
    }

    @Override
    public void onChatRoomsLoaded() {
        if (mChatRooms == null) {
            mChatRooms = new ArrayList<>();
        }
        mChatRooms.clear();
        mChatRooms.addAll(mManager.getChatRooms());
        mAdapter.notifyDataSetChanged();
    }
}
