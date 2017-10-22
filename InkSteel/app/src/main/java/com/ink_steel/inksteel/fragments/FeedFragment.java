package com.ink_steel.inksteel.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.PostsAdapter;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.AddPostActivity;
import com.ink_steel.inksteel.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

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
        updatePosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePosts();
    }

    private void updatePosts() {
        FirebaseFirestore.getInstance().collection("posts")
                .document(new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(new Date()))
                .collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mPosts.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                mPosts.add(0, new Post(document.getString("user"),
                                        document.getDate("time"),
                                        Uri.parse(document.getString("userProfileImage")),
                                        Uri.parse(document.getString("postPic"))));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
