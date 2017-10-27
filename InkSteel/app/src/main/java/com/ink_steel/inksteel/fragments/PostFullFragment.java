package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.ReactionsAdapter;
import com.ink_steel.inksteel.model.Reaction;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PostFullFragment extends Fragment {

    private ReactionsAdapter mAdapter;
    private boolean mIsFirstLoad;
    private DocumentReference mThisPostRef;
    private int like, blush, devil, dazed;
    private String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private boolean hasReacted;
    private String currentReaction = "";
    private int currentReactionCount;

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
        final View view = inflater.inflate(R.layout.post_info_fragment, container, false);

        final ImageView postUserProfileImage = (ImageView) view.findViewById(R.id.post_info_user_picture);
        final ImageView postImage = (ImageView) view.findViewById(R.id.post_info_post_image);
        final TextView reactionUserEmail = (TextView) view.findViewById(R.id.post_info_username);
        final TextView reactionText = (TextView) view.findViewById(R.id.post_reaction_text);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.post_info_recycler_view);

        View reactionsView = view.findViewById(R.id.reactions_layout);
        final TextView likeCount = (TextView) reactionsView.findViewById(R.id.reaction_like_count);
        final TextView blushCount = (TextView) reactionsView.findViewById(R.id.reaction_blush_count);
        final TextView devilCount = (TextView) reactionsView.findViewById(R.id.reaction_devil_count);
        final TextView dazedCount = (TextView) reactionsView.findViewById(R.id.reaction_dazed_count);
        ImageButton likeBtn = (ImageButton) reactionsView.findViewById(R.id.reaction_like);
        ImageButton blushBtn = (ImageButton) reactionsView.findViewById(R.id.reaction_blush);
        final ImageButton devilBtn = (ImageButton) reactionsView.findViewById(R.id.reaction_devil);
        ImageButton dazedBtn = (ImageButton) reactionsView.findViewById(R.id.reaction_dazed);

        String postId = getArguments().getString("postId");
        mIsFirstLoad = true;
        hasReacted = false;
        final List<Reaction> reactions = new LinkedList<>();

        // 1st - Acquire post info to display = Initial loading
        mThisPostRef = FirebaseFirestore.getInstance()
                .document("posts/" + postId);

        mThisPostRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {

                if (mIsFirstLoad) {
                    reactionUserEmail.setText(snapshot.getString("user"));
                    Picasso.with(getActivity()).load(snapshot.getString("userProfileImage"))
                            .into(postUserProfileImage);
                    Picasso.with(getActivity()).load(snapshot.getString("postImage"))
                            .into(postImage);
                    mIsFirstLoad = false;
                }
                like = snapshot.getLong("like").intValue();
                blush = snapshot.getLong("blush").intValue();
                devil = snapshot.getLong("devil").intValue();
                dazed = snapshot.getLong("dazed").intValue();

                likeCount.setText(String.valueOf(like));
                blushCount.setText(String.valueOf(blush));
                devilCount.setText(String.valueOf(devil));
                dazedCount.setText(String.valueOf(dazed));
            }
        });

        mThisPostRef.collection("reactions").document(userEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                hasReacted = true;
                                setCurrentReaction(task.getResult().getString("type"));
                            }
                        }
                    }
                });

        mThisPostRef.collection("history").orderBy("time").limit(20)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots,
                                        FirebaseFirestoreException e) {
                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            String user = change.getDocument().getString("user");
                            String reactionType = change.getDocument().getString("reaction");
                            boolean isInitial = change.getDocument().getBoolean("isInitial");
                            Reaction reaction = new Reaction(user, reactionType, isInitial);
                            switch (change.getType()) {
                                case MODIFIED:
                                    if (reactions.contains(reaction))
                                        reactions.remove(reaction);
                                case ADDED:
                                    reactions.add(0, reaction);
                                    mAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });

        // 2nd - add reaction to the database
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserReaction("like");
            }
        });

        blushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserReaction("blush");
            }
        });

        devilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserReaction("devil");
            }
        });

        dazedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserReaction("dazed");
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ReactionsAdapter(reactions);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void setCurrentReaction(String reaction) {
        currentReaction = reaction;
        switch (currentReaction) {
            case "like":
                currentReactionCount = like;
                break;
            case "blush":
                currentReactionCount = blush;
                break;
            case "devil":
                currentReactionCount = devil;
                break;
            case "dazed":
                currentReactionCount = dazed;
                break;
        }
    }

    private void updateUserReaction(String reaction) {

        Map<String, Object> reactionData = new HashMap<>();
        reactionData.put("reaction", currentReaction);
        reactionData.put("time", new Date().getTime());
        reactionData.put("isInitial", !hasReacted);
        reactionData.put("user", userEmail);

        if (reaction.equals(""))
            return;
        if (hasReacted) {
            if (!currentReaction.equals(reaction)) {
                mThisPostRef.update(currentReaction, --currentReactionCount);
                setCurrentReaction(reaction);
            } else {
                return;
            }
            mThisPostRef.collection("reactions").document(userEmail).update("type", reaction);
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("type", reaction);
            data.put("time", new Date().getTime());
            mThisPostRef.collection("reactions").document(userEmail).set(data);
            hasReacted = true;
        }

        mThisPostRef.collection("reactions").document(userEmail)
                .update("time", new Date().getTime());
        mThisPostRef.update(reaction, ++currentReactionCount);

        mThisPostRef.collection("history").document().set(reactionData);
    }
}
