package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.PostsAdapter;
import com.ink_steel.inksteel.data.FirebaseManager;
import com.ink_steel.inksteel.helpers.OnPostClickListener;
import com.ink_steel.inksteel.model.Post;

import java.util.LinkedList;
import java.util.List;

public class FeedFragment extends Fragment implements OnPostClickListener, FirebaseManager.PostsListener {

    private List<Post> mPosts;
    private PostsAdapter mAdapter;
    private FirebaseManager.PostsManager mManager;

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.feed_rv);

        mPosts = new LinkedList<>();
        mAdapter = new PostsAdapter(getActivity().getApplicationContext(), mPosts, this);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new AddPostFragment());
            }
        });

        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        mManager = firebaseManager.getPostsManager(this);

        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mManager.registerPostsListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager.unregisterPostsListener();
    }

    @Override
    public void onPostClickListener(int position) {
        ((HomeActivity) getActivity())
                .replaceFragment(PostFullFragment.newInstance(mPosts.get(position).getPostId()));
    }

    @Override
    public void onPostsAdded(Post post) {
        if (!mPosts.contains(post)) {
            if (mPosts.size() != 0)
                mPosts.add(0, post);
            else
                mPosts.add(post);
            mAdapter.notifyDataSetChanged();
        }
    }
}

