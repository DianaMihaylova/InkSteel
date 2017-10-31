package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.PostsAdapter;
import com.ink_steel.inksteel.data.PostsManager;
import com.ink_steel.inksteel.helpers.OnPostClickListener;
import com.ink_steel.inksteel.model.Post;

import java.util.LinkedList;
import java.util.List;

public class FeedFragment extends Fragment implements OnPostClickListener, PostsManager.PostsManagerListener {

    private List<Post> mPosts;
    private PostsAdapter mAdapter;
    private PostsManager mManager;

    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        Log.d("Lifecycle", this.getClass().getSimpleName() + " onCreateView");

        RecyclerView recyclerView = view.findViewById(R.id.feed_rv);

        mPosts = new LinkedList<>();
        mAdapter = new PostsAdapter(getActivity().getApplicationContext(), mPosts, this);

        mManager = PostsManager.getInstance();
        mManager.registerPostsListener(this);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new AddPostFragment());
            }
        });

        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
//        mPosts.addAll(mManager.getPosts());
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onStart");
        super.onStart();
    }

    @Override
    public void onPostClickListener(int position) {
        ((HomeActivity) getActivity())
                .replaceFragment(PostInfoFragment.newInstance(mPosts.get(position).getPostId()));
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

    @Override
    public void onPostChanged(Post post) {
        mAdapter.notifyItemChanged(mPosts.indexOf(post));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onDetach");
    }
}

