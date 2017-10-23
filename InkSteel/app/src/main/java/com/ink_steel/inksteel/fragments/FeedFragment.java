package com.ink_steel.inksteel.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.PostsAdapter;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.AddPostActivity;
import com.ink_steel.inksteel.model.Post;

import java.util.LinkedList;

public class FeedFragment extends Fragment {

    private LinkedList<Post> mPosts;
    private PostsAdapter mAdapter;

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feed_rv);

        mPosts = new LinkedList<>();
        mAdapter = new PostsAdapter(getContext(), mPosts);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        FirebaseFirestore.getInstance().collection("posts")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots,
                                        FirebaseFirestoreException e) {

                        for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                            Post post = new Post(snapshot.getString("user"),
                                    snapshot.getDate("time"),
                                    Uri.parse(snapshot.getString("userProfileImage")),
                                    Uri.parse(snapshot.getString("postPic")));
                            if (!mPosts.contains(post)) {
                                mPosts.add(0, post);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

}

