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
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.PostsAdapter;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.OnPostClickListener;
import com.ink_steel.inksteel.model.Post;

import java.util.LinkedList;

public class FeedFragment extends Fragment implements OnPostClickListener {

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
        mAdapter = new PostsAdapter(getActivity().getApplicationContext(), mPosts, this);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddPostActivity.class);
//                startActivity(intent);
                ((HomeActivity) getActivity()).replaceFragment(new AddPostFragment());
            }
        });

        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    /*
    data.put("user", currEmail);
                    data.put("userProfileImage", ConstantUtils.PROFILE_IMAGE_URI.toString());
                    data.put("postImage", downloadUrl);
                    data.put("postImageThumbnail", "");
                    data.put("time", new Date().getTime());
                    data.put("like", 0);
                    data.put("blush", 0);
                    data.put("devil", 0);
                    data.put("dazed", 0);
     */

    @Override
    public void onStart() {
        super.onStart();

        FirebaseFirestore.getInstance().collection("posts")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots,
                                        FirebaseFirestoreException e) {
                        for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Post post = new Post(
                                        snapshot.getId(),
                                        snapshot.getString("user"),
                                        snapshot.getLong("time"),
                                        snapshot.getString("userProfileImage"),
                                        snapshot.getString("postPic"),
                                        snapshot.getString("postImageThumbnail"));

                                if (!mPosts.contains(post)) {
                                    mPosts.add(0, post);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onPostClickListener(int position) {

        Toast.makeText(getActivity(), "From FeedFragment", Toast.LENGTH_SHORT).show();
        ((HomeActivity) getActivity())
                .replaceFragment(PostFullFragment.newInstance(mPosts.get(position).getPostId()));
    }
}

