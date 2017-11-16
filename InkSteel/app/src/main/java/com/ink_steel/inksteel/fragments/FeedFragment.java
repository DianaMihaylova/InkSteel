package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.PostsAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.helpers.Listeners.PostClickListener;
import com.ink_steel.inksteel.model.Post;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedFragment extends Fragment implements PostClickListener, DatabaseManager.PostsListener {

    @BindView(R.id.fragment_feed_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_feed_fab)
    FloatingActionButton mFab;
    @BindView(R.id.fargment_feed_new_posts_btn)
    Button mNewPostsButton;
    boolean isInfoOn;
    private List<Post> mPosts;
    private PostsAdapter mAdapter;
    private Listeners.OnReplaceFragmentListener mReplaceFragmentListener;
    private DatabaseManager.PostsManager mManager;
    private int mTotalItemCount;
    private int mLastVisibleItem;
    private boolean isLoading;

    public FeedFragment() {
    }

    @OnClick(R.id.fragment_feed_fab)
    public void addPost() {
        mReplaceFragmentListener.replaceFragment(new AddPostFragment());
    }

    @OnClick(R.id.fargment_feed_new_posts_btn)
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
        mNewPostsButton.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listeners.OnReplaceFragmentListener) {
            mReplaceFragmentListener = (Listeners.OnReplaceFragmentListener) context;
        } else {
            Log.e(FeedFragment.class.getSimpleName(), "Activity not implementing " +
                    "OnReplaceFragmentListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);

        mManager = DatabaseManager.getPostsManager();
        mPosts = new LinkedList<>();
        mAdapter = new PostsAdapter(mPosts, this);
        mNewPostsButton.setVisibility(View.GONE);

        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = layoutManager.getItemCount();
                mLastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!isLoading && mTotalItemCount <= (mLastVisibleItem + 3)) {
                    if (mManager != null) {
                        mManager.loadMorePosts(FeedFragment.this);
                        isLoading = true;
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onPostClick(int position) {
        isInfoOn = true;
        mReplaceFragmentListener
                .replaceFragment(PostInfoFragment.newInstance(mPosts.get(position).getCreatedAt()));
    }

    @Override
    public void onPostsEnding() {
//        mManager.loadMorePosts(this);
    }

    @Override
    public void onStart() {
        if (!isInfoOn) {
            mManager.registerPostsListener(this);
        }
        mPosts.addAll(mManager.getPosts());
        mAdapter.notifyDataSetChanged();
        Log.d("omg", "onStart" + mPosts.toString());
        super.onStart();
    }

    @Override
    public void onStop() {
        if (!isInfoOn) {
            mManager.unregisterPostsListener();
            isLoading = false;
            mPosts.clear();
        }
        super.onStop();
    }

    @Override
    public void onPostAdded(Post post, boolean isInitialLoad) {
        if (isInitialLoad) {
            mPosts.add(post);
            mAdapter.notifyItemInserted(mPosts.size() - 1);
        } else {
            mPosts.add(0, post);
            mAdapter.notifyItemInserted(0);

            if (mLastVisibleItem - 2 > 0)
                mNewPostsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMorePostsLoaded(Post post) {
        mPosts.add(post);
        mAdapter.notifyItemInserted(mPosts.size() - 1);
        isLoading = false;
    }

    @Override
    public void onError(String errorMessage) {

    }

}
