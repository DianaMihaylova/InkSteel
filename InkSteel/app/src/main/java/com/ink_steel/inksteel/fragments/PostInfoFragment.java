package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.ReactionsAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class PostInfoFragment extends Fragment implements View.OnClickListener, DatabaseManager.PostReactionsListener {

    private boolean isCollapsed;
    private ReactionsAdapter mAdapter;
    private boolean isFullScreen;
    private TextView mLikeCount;
    private TextView mBlushCount;
    private TextView mDevilCount;
    private TextView mDazedCount;
    private ImageView mPostUserProfileImage;
    private TextView mReactionUserEmail;
    private ImageView mPostImage;
    private List<Reaction> mReactions;
    private RecyclerView mRecyclerView;
    private View mUserInfoView;
    private View mReactionsView;
    private ImageButton mCollapse;
    private ConstraintSet mCs;
    private ConstraintLayout mCl;
    private TextView mPostDescription;
    private Post mCurrentPost;
    private DatabaseManager mManager;
    private Picasso mPicasso;

    public PostInfoFragment() {
    }

    public static PostInfoFragment newInstance(String postId) {
        PostInfoFragment postInfoFragment = new PostInfoFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("postId", postId);
        postInfoFragment.setArguments(bundle);
        return postInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_info, container, false);

        mPostUserProfileImage = view.findViewById(R.id.post_info_user_picture);
        mPostImage = view.findViewById(R.id.post_info_post_image);
        mReactionUserEmail = view.findViewById(R.id.post_info_username);
        mRecyclerView = view.findViewById(R.id.post_info_recycler_view);
        mUserInfoView = view.findViewById(R.id.user_info);
        mPostDescription = mUserInfoView.findViewById(R.id.post_info_description);
        View next = view.findViewById(R.id.next);
        View prev = view.findViewById(R.id.previous);
        mReactionsView = view.findViewById(R.id.reactions_layout);
        mLikeCount = mReactionsView.findViewById(R.id.reaction_like_count);
        mBlushCount = mReactionsView.findViewById(R.id.reaction_blush_count);
        mDevilCount = mReactionsView.findViewById(R.id.reaction_devil_count);
        mDazedCount = mReactionsView.findViewById(R.id.reaction_dazed_count);
        ImageButton likeBtn = mReactionsView.findViewById(R.id.reaction_like);
        ImageButton blushBtn = mReactionsView.findViewById(R.id.reaction_blush);
        ImageButton devilBtn = mReactionsView.findViewById(R.id.reaction_devil);
        ImageButton dazedBtn = mReactionsView.findViewById(R.id.reaction_dazed);
        mCollapse = mReactionsView.findViewById(R.id.collapse_btn);

        View layoutContainer = getActivity().findViewById(R.id.activity_home_container);
        Snackbar.make(layoutContainer, "Loading...", Snackbar.LENGTH_SHORT).show();

        mCollapse.setImageResource(R.drawable.ic_expand_more);
        String postId = getArguments().getString("postId");
        mReactions = new LinkedList<>();
        isCollapsed = true;
        isFullScreen = false;
        mCs = new ConstraintSet();
        mCl = view.findViewById(R.id.post_cl);
        mPicasso = new Picasso.Builder(getActivity()).build();

        mManager = DatabaseManager.getInstance();
        mCurrentPost = mManager.getPost(getActivity(), this, postId);
        displayPost(true);

        mCollapse.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        likeBtn.setOnClickListener(this);
        blushBtn.setOnClickListener(this);
        devilBtn.setOnClickListener(this);
        dazedBtn.setOnClickListener(this);

        mPostImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleImageFullscreen();
                return true;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ReactionsAdapter(mReactions);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void toggleImageFullscreen() {
        if (!isCollapsed)
            toggleReactionsRecycler();

        if (isFullScreen) {
            mUserInfoView.setVisibility(View.GONE);
            mReactionsView.setVisibility(View.GONE);
        } else {
            mUserInfoView.setVisibility(View.VISIBLE);
            mReactionsView.setVisibility(View.VISIBLE);
        }
        isFullScreen = !isFullScreen;
    }

    private void toggleReactionsRecycler() {
        int weight;
        if (isCollapsed) {
            weight = 1;
            mCollapse.setImageResource(R.drawable.ic_expand_less);
        } else {
            weight = 0;
            mCollapse.setImageResource(R.drawable.ic_expand_more);
        }
        isCollapsed = !isCollapsed;
        mCs.clone(mCl);
        mCs.setVerticalWeight(mRecyclerView.getId(), weight);
        mCs.applyTo(mCl);
    }


    private void displayPost(boolean isInitial) {
        if (mCurrentPost != null) {

            mReactionUserEmail.setText(mCurrentPost.getUserEmail());

            mPicasso.load(mCurrentPost.getUrlProfileImage())
                    .placeholder(R.drawable.placeholder_posts)
                    .into(mPostUserProfileImage);
            mPicasso.load(mCurrentPost.getUrlImage())
                    .placeholder(R.drawable.placeholder_posts)
                    .into(mPostImage);

            String description = mCurrentPost.getDescription();
            if (!description.isEmpty()) {
                mPostDescription.setVisibility(View.VISIBLE);
                mPostDescription.setText(mCurrentPost.getDescription());
            } else {
                mPostDescription.setVisibility(View.INVISIBLE);
            }
            updateReactions();

            if (!isInitial)
                clearRecyclerView();
        }
    }

    private void clearRecyclerView() {
        mRecyclerView.getRecycledViewPool().clear();
        mReactions.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void updateReactions() {

        mLikeCount.setText(String.valueOf(mCurrentPost.getReactions().get(0)));
        mBlushCount.setText(String.valueOf(mCurrentPost.getReactions().get(1)));
        mDevilCount.setText(String.valueOf(mCurrentPost.getReactions().get(2)));
        mDazedCount.setText(String.valueOf(mCurrentPost.getReactions().get(3)));
    }

    @Override
    public void onClick(View v) {
        String reaction = "";
        switch (v.getId()) {
            case R.id.reaction_like:
                reaction = getString(R.string.like);
                break;
            case R.id.reaction_blush:
                reaction = getString(R.string.blush);
                break;
            case R.id.reaction_devil:
                reaction = getString(R.string.devil);
                break;
            case R.id.reaction_dazed:
                reaction = getString(R.string.dazed);
                break;
            case R.id.collapse_btn:
                toggleReactionsRecycler();
                break;
            case R.id.next: {
                Post post = mManager.getNextPost(getActivity(), this);
                if (post != null) {
                    mCurrentPost = post;
                    displayPost(false);
                }
            }
                break;
            case R.id.previous: {
                Post post = mManager.getPreviousPost(getActivity(), this);
                if (post != null) {
                    mCurrentPost = post;
                    displayPost(false);
                }
            }
                break;
        }
        if (!reaction.isEmpty())
            mManager.saveUserReaction(reaction);
    }

    @Override
    public void onPostReactionsChanged() {
        updateReactions();
    }

    @Override
    public void onReactionAdded(Reaction reaction) {
        mReactions.add(0, reaction);
        mAdapter.notifyItemInserted(0);
        mRecyclerView.scrollToPosition(0);
    }
}