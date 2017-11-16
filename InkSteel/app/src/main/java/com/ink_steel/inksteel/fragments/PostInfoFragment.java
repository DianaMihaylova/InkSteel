package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatTextView;
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
import com.ink_steel.inksteel.model.Post.ReactionType;
import com.ink_steel.inksteel.model.Reaction;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PostInfoFragment extends Fragment implements DatabaseManager.PostListener {

    @BindView(R.id.post_info_user_picture)
    ImageView mPostsUserProfilePictureIv;
    @BindView(R.id.post_info_username)
    TextView mPostsUserUsernameTv;
    @BindView(R.id.post_info_description)
    AppCompatTextView mPostDescription;
    @BindView(R.id.fragment_post_info_post_iv)
    ImageView mPostImageIv;
    @BindView(R.id.fragment_post_info_prev_v)
    View mPrevV;
    @BindView(R.id.fragment_post_info_next_v)
    View mNextV;
    @BindView(R.id.collapse_btn)
    ImageButton mCollapseBtn;
    @BindView(R.id.reaction_like)
    ImageButton mReactionLike;
    @BindView(R.id.reaction_blush)
    ImageButton mReactionBlush;
    @BindView(R.id.reaction_devil)
    ImageButton mReactionDevil;
    @BindView(R.id.reaction_dazed)
    ImageButton mReactionDazed;
    @BindView(R.id.reaction_like_count)
    TextView mReactionLikeCount;
    @BindView(R.id.reaction_blush_count)
    TextView mReactionBlushCount;
    @BindView(R.id.reaction_devil_count)
    TextView mReactionDevilCount;
    @BindView(R.id.reaction_dazed_count)
    TextView mReactionDazedCount;
    @BindView(R.id.fragment_post_info_reactions_rv)
    RecyclerView mReactionsRv;
    @BindView(R.id.post_cl)
    ConstraintLayout mPostCl;
    Unbinder unbinder;
    private Post mPost;
    private DatabaseManager.PostsManager mManager;
    private boolean isCollapsed;
    private ConstraintSet mSet;
    private LinkedList<Reaction> mReactions;
    private ReactionsAdapter mAdapter;

    public PostInfoFragment() {
    }

    public static PostInfoFragment newInstance(long createdAt) {
        PostInfoFragment postInfoFragment = new PostInfoFragment();
        Bundle bundle = new Bundle(1);
        bundle.putLong("createdAt", createdAt);
        postInfoFragment.setArguments(bundle);
        return postInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        mManager = DatabaseManager.getPostsManager();
        mReactions = new LinkedList<>();
        mPost = mManager.getPostByCreatedAt(getArguments().getLong("createdAt"), this);
        if (mPost == null) {
            getActivity().onBackPressed();
        }
        isCollapsed = true;
        displayPost();
        mSet = new ConstraintSet();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mReactionsRv.setLayoutManager(layoutManager);
        mAdapter = new ReactionsAdapter(mReactions);
        mReactionsRv.setAdapter(mAdapter);

        return view;
    }

    private void displayPost() {
        Context context = getActivity();
        Picasso.with(context).load(mPost.getUrlProfileImage()).into(mPostsUserProfilePictureIv);
        mPostsUserUsernameTv.setText(mPost.getUserEmail());
        if (mPost.getDescription().isEmpty()) mPostDescription.setVisibility(View.INVISIBLE);
        else mPostDescription.setText(mPost.getDescription());
        Picasso.with(context).load(mPost.getUrlImage()).into(mPostImageIv);
        displayReactions();
    }

    private void displayReactions() {
        mReactionLikeCount.setText(String.valueOf(mPost.getReactionCount(ReactionType.LIKE)));
        mReactionBlushCount.setText(String.valueOf(mPost.getReactionCount(ReactionType.BLUSH)));
        mReactionDevilCount.setText(String.valueOf(mPost.getReactionCount(ReactionType.DEVIL)));
        mReactionDazedCount.setText(String.valueOf(mPost.getReactionCount(ReactionType.DAZED)));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        mManager.unregisterPostListener();
    }

    @OnClick({R.id.fragment_post_info_prev_v, R.id.fragment_post_info_next_v, R.id.collapse_btn,
            R.id.reaction_like, R.id.reaction_blush, R.id.reaction_devil, R.id.reaction_dazed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_post_info_prev_v:
                navigatePost(false);
                break;
            case R.id.fragment_post_info_next_v:
                navigatePost(true);
                break;
            case R.id.collapse_btn:
                toggleReactionsRv();
                break;
            case R.id.reaction_like:
                mManager.react(ReactionType.LIKE);
                break;
            case R.id.reaction_blush:
                mManager.react(ReactionType.BLUSH);
                break;
            case R.id.reaction_devil:
                mManager.react(ReactionType.DEVIL);
                break;
            case R.id.reaction_dazed:
                mManager.react(ReactionType.DAZED);
                break;
        }
    }

    private void navigatePost(boolean isNext) {
        Post post;
        if (isNext) post = mManager.getNextPost();
        else post = mManager.getPreviousPost();
        if (post != null) {
            mPost = post;
            mReactions.clear();
            displayPost();
        }
    }

    private void toggleReactionsRv() {
        int weight;
        if (isCollapsed) {
            weight = 1;
            mCollapseBtn.setImageResource(R.drawable.ic_expand_less);
        } else {
            weight = 0;
            mCollapseBtn.setImageResource(R.drawable.ic_expand_more);
        }
        mSet.clone(mPostCl);
        mSet.setVerticalWeight(mReactionsRv.getId(), weight);
        mSet.applyTo(mPostCl);
        isCollapsed = !isCollapsed;
    }

    @Override
    public void onPostChanged(Post post) {
        mPost = post;
        displayReactions();
    }

    @Override
    public void onReactionAdded(Reaction reaction) {

        mReactions.add(0, reaction);
        mAdapter.notifyItemInserted(0);
        mReactionsRv.scrollToPosition(0);

    }

    @Override
    public void onError(String errorMessage) {

    }
}
