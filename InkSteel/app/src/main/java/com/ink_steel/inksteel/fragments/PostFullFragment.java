package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
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
import com.ink_steel.inksteel.data.FirebaseManager;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class PostFullFragment extends Fragment implements FirebaseManager.PostListener,
        View.OnClickListener {

    boolean isCollapsed;
    private ReactionsAdapter mAdapter;
    private boolean isFullScreen;
    private TextView mLikeCount;
    private TextView mBlushCount;
    private TextView mDevilCount;
    private TextView mDazedCount;
    private ImageView mPostUserProfileImage;
    private TextView mReactionUserEmail;
    private ImageView mPostImage;
    private FirebaseManager.PostManager mManager;
    private List<Reaction> mReactions;
    private RecyclerView mRecyclerView;
    private View mUserInfoView;
    private View mReactionsView;
    private ImageButton mCollapse;
    private ConstraintSet mCs;
    private ConstraintLayout mCl;
    private TextView mPostDescription;

    public PostFullFragment() {
    }

    public static PostFullFragment newInstance(String postId) {
        PostFullFragment postInfoFragment = new PostFullFragment();
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

        mCollapse.setImageResource(R.drawable.ic_expand_more);
        String postId = getArguments().getString("postId");
        mReactions = new LinkedList<>();
        isCollapsed = true;
        isFullScreen = false;
        mCs = new ConstraintSet();
        mCl = view.findViewById(R.id.post_cl);

        mManager = FirebaseManager.getInstance().getPostManager(postId, this);
        displayPost(mManager.getPost(), true);

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

    private void displayPost(Post post, boolean isInitial) {
        if (post != null) {
            mReactionUserEmail.setText(post.getUserEmail());
            Picasso.with(getActivity()).load(post.getUrlProfileImage())
                    .into(mPostUserProfileImage);
            Picasso.with(getActivity()).load(post.getUrlImage())
                    .into(mPostImage);
            mPostDescription.setText(post.getDescription());
            updateReactions(post);
            if (!isInitial)
                clearRecyclerView();
        }
    }

    private void clearRecyclerView() {
        mRecyclerView.getRecycledViewPool().clear();
        mReactions.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void updateReactions(Post post) {

        mLikeCount.setText(String.valueOf(post.getReactionLike()));
        mBlushCount.setText(String.valueOf(post.getReactionBlush()));
        mDevilCount.setText(String.valueOf(post.getReactionDevil()));
        mDazedCount.setText(String.valueOf(post.getReactionDazed()));
    }

    @Override
    public void onStart() {
        super.onStart();
        mManager.registerPostListener();
        mManager.registerReactionsListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        mManager.unregisterPostListener();
        mManager.unregisterReactionsListener();
    }

    @Override
    public void onUserReactionChange(Post post) {
        updateReactions(post);
    }

    @Override
    public void onReactionsChanged(Reaction reaction) {
        mReactions.add(0, reaction);
        mAdapter.notifyItemInserted(0);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        String reaction = "";
        switch (v.getId()) {
            case R.id.reaction_like:
                reaction = "like";
                break;
            case R.id.reaction_blush:
                reaction = "blush";
                break;
            case R.id.reaction_devil:
                reaction = "devil";
                break;
            case R.id.reaction_dazed:
                reaction = "dazed";
                break;
            case R.id.collapse_btn:
                toggleReactionsRecycler();
                break;
            case R.id.next:
                displayPost(mManager.getNextPost(), false);
                break;
            case R.id.previous:
                displayPost(mManager.getPreviousPost(), false);
                break;
        }
        if (!reaction.isEmpty())
            mManager.saveUserReaction(reaction);
    }
}
