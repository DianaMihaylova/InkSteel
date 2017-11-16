package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import java.util.LinkedList;

public class ChatListFragment extends Fragment implements Listeners.ChatListClickListener,
        DatabaseManager.ChatRoomListener {

    private DatabaseManager.ChatManager mManager;
    private ChatListAdapter mAdapter;
    private LinkedList<ChatRoom> mChatRooms;

    public ChatListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);


        mManager = DatabaseManager.getChatManager();
        mChatRooms = new LinkedList<>();
        FloatingActionButton fab = view.findViewById(R.id.chat_list_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).replaceFragment(new FriendsFragment());
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.chat_list_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatListAdapter(this, mChatRooms);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        mManager.registerChatRoomsListener(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mManager.unregisterChatRoomsListener();
    }

    @Override
    public void onChatItemClick(int position) {
        ((HomeActivity) getActivity()).replaceFragment(ChatFragment
                .newInstance(mChatRooms.get(position).getEmail()));
    }

    @Override
    public void onChatRoomAdded(ChatRoom chatRoom) {
        mChatRooms.add(chatRoom);
        mAdapter.notifyItemInserted(mChatRooms.size() - 1);
    }

    @Override
    public void onChatRoomModified(ChatRoom chatRoom) {
        mAdapter.notifyItemChanged(mChatRooms.indexOf(chatRoom));
    }

    @Override
    public void onChatRoomLoaded() {
        mChatRooms.addAll(mManager.getChatRooms());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String message) {

    }
}