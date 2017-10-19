package com.ink_steel.inksteel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.Post;
import com.ink_steel.inksteel.PostsAdapter;
import com.ink_steel.inksteel.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FeedFragment extends Fragment {


    public FeedFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feed_rv);

        ArrayList<Post> posts = new ArrayList<>();
        try {
            posts.add(new Post("user1@gmail.com", System.currentTimeMillis(),
                    new URL("https://i.pinimg.com/736x/03/5d/9e/035d9ee5c531a63269a106d6daa87af0.jpg"),
                    Post.PostType.IMAGE));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        PostsAdapter adapter = new PostsAdapter(getContext(), posts);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
