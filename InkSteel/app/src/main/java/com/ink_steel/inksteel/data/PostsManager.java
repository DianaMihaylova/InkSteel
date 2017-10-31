package com.ink_steel.inksteel.data;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class PostsManager {

    private static PostsManager mPostsManager;
    private static FirebaseManager2 mManager;
    private Post currentPost;
    private int postIndex = 0;
    private HashMap<String, Integer> mPostsId;
    private LinkedList<Post> mPosts;
    private ListenerRegistration mPostListenerRegistration;
    private ListenerRegistration mReactionsListenerRegistration;
    private HashMap<String, Integer> reactions;
    private String reaction;
    private int reactionCount;
    private ListenerRegistration mPostsListenerRegistration;

    private PostsManager() {
        mManager = FirebaseManager2.getInstance();
        mPostsId = new HashMap<>();
        mPosts = new LinkedList<>();
        reactions = new HashMap<>();
        reactions.put("like", 0);
        reactions.put("blush", 1);
        reactions.put("devil", 2);
        reactions.put("dazed", 3);
    }

    public static PostsManager getInstance() {
        if (mPostsManager == null)
            mPostsManager = new PostsManager();
        return mPostsManager;
    }

    public LinkedList<Post> getPosts() {
        return mPosts;
    }

    public Post getPreviousPost(PostListener listener) {
        int nextPos = mPostsId.get(currentPost.getPostId()) + 1;
        if (mPosts.size() != nextPos)
            return getPost(listener, mPosts.get(nextPos).getPostId());
        return null;
    }

    public Post getPost(PostListener listener, String postId) {
        currentPost = mPosts.get(mPostsId.get(postId));
        unregisterPostListeners();
        reaction = null;
        reactionCount = 0;
        hasUserReacted();
        registerPostListener(listener);
        registerReactionsListener(listener);
        return currentPost;
    }

    private void unregisterPostListeners() {
        if (mReactionsListenerRegistration != null)
            mReactionsListenerRegistration.remove();
        if (mPostListenerRegistration != null)
            mPostListenerRegistration.remove();
    }

    // get the post's reaction's count real time
    private void registerPostListener(final PostListener listener) {
        mPostListenerRegistration = mManager.mFirestore.collection("posts")
                .document(currentPost.getPostId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                        if (e != null)
                            return;
                        if (snapshot.exists()) {
                            currentPost.getReactions().clear();
                            currentPost.getReactions()
                                    .addAll(snapshot.toObject(Post.class).getReactions());
                            listener.onPostReactionsChanged();
                        }
                    }
                });
    }

    // get the post's reaction real time changes
    private void registerReactionsListener(final PostListener listener) {
        mReactionsListenerRegistration = mManager.mFirestore
                .collection("posts").document(currentPost.getPostId()).collection("history")
                .orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots,
                                        FirebaseFirestoreException e) {
                        if (e != null)
                            return;
                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                listener.onReactionAdded(change.getDocument()
                                        .toObject(Reaction.class));
                            }
                        }
                    }
                });
    }

    private void hasUserReacted() {
        mManager.mFirestore.collection("posts").document(currentPost.getPostId())
                .collection("reactions").document(mManager.mCurrentUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.exists())
                            setCurrentReaction(snapshot.getString("reactionType"));
                    }
                });
    }

    private void setCurrentReaction(String reactionType) {
        reaction = reactionType;
        reactionCount = currentPost.getReactionCount(reaction);
    }

    public Post getNextPost(PostListener listener) {
        int prevPost = mPostsId.get(currentPost.getPostId()) - 1;
        if (prevPost != -1)
            return getPost(listener, mPosts.get(prevPost).getPostId());
        return null;
    }

    // get all posts real time
    public void registerPostsListener(final PostsManagerListener listener) {
        Query postsQuery = mManager.mFirestore.collection("posts")
                .orderBy("createdAt");
        mPostsListenerRegistration = postsQuery
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots,
                                        FirebaseFirestoreException e) {
                        if (e != null)
                            return;

                        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                DocumentSnapshot snapshot = change.getDocument();
                                if (snapshot.exists()) {
                                    Post post = snapshot.toObject(Post.class);
//                                    if (!mPostsId.containsKey(post.getPostId())) {
                                    mPostsId.put(post.getPostId(), postIndex);
                                    mPosts.add(post);
                                    postIndex++;
                                    listener.onPostsAdded(post);
//                                    }
                                }
                            } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                                listener.onPostChanged(change.getDocument().toObject(Post.class));
                            }
                        }
                    }
                });
    }

    public void saveUserReaction(String newReaction) {

        if (newReaction.isEmpty())
            return;

        DocumentReference postReference = mManager.mFirestore
                .collection("posts").document(currentPost.getPostId());
        String email = mManager.mCurrentUser.getEmail();

        boolean isInitial = false;

        if (reaction != null) {
            if (reaction.equals(newReaction))
                return;

            int index = reactions.get(reaction);
            currentPost.getReactions().set(index, --reactionCount);
            postReference.update("reactions", currentPost.getReactions());
            postReference.collection("reactions").document(email)
                    .update("reactionType", newReaction, "time",
                            new Date().getTime(), "initial", false);
        } else {
            isInitial = true;
            Reaction saveReaction = new Reaction(email, newReaction,
                    true, new Date().getTime());

            postReference.collection("reactions").document(email)
                    .set(saveReaction);
        }

        setCurrentReaction(newReaction);
        Reaction reactionData = new Reaction(email, reaction,
                isInitial, new Date().getTime());

        int index = reactions.get(reaction);
        currentPost.getReactions().set(index, ++reactionCount);
        postReference.update("reactions", currentPost.getReactions());
        postReference.collection("history").document().set(reactionData);
    }

    public interface PostsManagerListener {
        void onPostsAdded(Post post);

        void onPostChanged(Post post);
    }

    public interface PostListener {
        void onPostReactionsChanged();

        void onReactionAdded(Reaction reaction);
    }
}
