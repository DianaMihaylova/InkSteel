package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;


public class ChatListFragment extends Fragment {

    public ChatListFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        DatabaseManager manager = DatabaseManager.getInstance();
        ArrayList<User> friends = manager.getUserFriends();

        RecyclerView recyclerView = view.findViewById(R.id.chat_list_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        return view;
    }
}
