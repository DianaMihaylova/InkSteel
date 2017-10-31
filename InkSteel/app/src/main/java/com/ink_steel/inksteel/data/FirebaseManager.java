package com.ink_steel.inksteel.data;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class FirebaseManager {

    private static PostsManager mPostsManager;
    private static FirebaseManager mFirebaseManager;
    private static PostManager mPostManager;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;

    private FirebaseManager() {
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseManager getInstance() {
        if (mFirebaseManager == null)
            mFirebaseManager = new FirebaseManager();
        return mFirebaseManager;
    }

    public PostsManager getPostsManager(PostsListener listener) {
        if (mPostsManager == null)
            mPostsManager = new PostsManager(listener);
        else if (mPostsManager.mPostsListener != listener)
            mPostsManager.mPostsListener = listener;
        return mPostsManager;
    }

    public PostManager getPostManager(String postId, PostListener listener) {
        if (mPostManager == null)
            mPostManager = new PostManager(postId, listener);
        else if (mPostManager.mPostListener != listener)
            mPostManager.mPostListener = listener;
        return mPostManager;
    }

    public interface PostsListener {
        void onPostsAdded(Post post);
    }

    public interface PostListener {
        void onUserReactionChange(Post post);

        void onReactionsChanged(Reaction reaction);
    }

    public class PostsManager {

        int postIndex = 0;
        private HashMap<String, Integer> mPostsId;
        private LinkedList<Post> mPosts;
        private ListenerRegistration mPostsListenerRegistration;
        private PostsListener mPostsListener;
        private boolean isInitial = true;

        private PostsManager(PostsListener listener) {
            mPostsId = new HashMap<>();
            mPosts = new LinkedList<>();
            mPostsListener = listener;
        }

        private Post getPostById(String id) {
            return mPosts.get(mPostsId.get(id));
        }

        private Post getPreviousPost(String currentPostId) {
            int nextPos = mPostsId.get(currentPostId) + 1;
            return mPosts.size() == nextPos ? null : mPosts.get(nextPos);
        }

        private Post getNextPost(String currentPostId) {
            int prevPos = mPostsId.get(currentPostId) - 1;
            return prevPos == -1 ? null : mPosts.get(prevPos);
        }

        public void registerPostsListener() {
            Query postsQuery = mFirestore.collection("posts")
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
                                        mPostsId.put(post.getPostId(), postIndex);
                                        if (isInitial)
                                            mPosts.add(post);
                                        else {
                                            mPosts.add(0, post);
                                        }
                                        postIndex++;
                                        mPostsListener.onPostsAdded(post);
                                    }
                                }
                            }
                            isInitial = false;
                        }
                    });
        }

        public void unregisterPostsListener() {
            if (mPostsListenerRegistration != null)
                mPostsListenerRegistration.remove();
        }
    }

    public class PostManager {

        private ListenerRegistration mPostListenerRegistration;
        private ListenerRegistration mReactionsListener;
        private PostListener mPostListener;

        private String mCurrentPostId;
        private Post currentPost;
        private String reaction;
        private int reactionCount;

        private DocumentReference mPostReference;
        private CollectionReference mReactionsReference;

        private PostManager(String postId, PostListener listener) {
            mPostListener = listener;
            mCurrentPostId = postId;
            mPostReference = mFirestore.collection("posts").document(mCurrentPostId);
            mReactionsReference = mPostReference.collection("reactions");
            hasUserReacted(HomeActivity.userEmail);
        }

        private void hasUserReacted(String userEmail) {
            mReactionsReference.document(userEmail).get()
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

        public Post getPost() {
            return mPostsManager.getPostById(mCurrentPostId);
        }

        public Post getNextPost() {
            Post post = mPostsManager.getNextPost(mCurrentPostId);
            if (post != null)
                setCurrentPost(post);

            return post;
        }

        private void setCurrentPost(Post post) {
            mCurrentPostId = post.getPostId();
            unregisterReactionsListener();
            unregisterReactionsListener();
            mPostReference = mFirestore.collection("posts").document(mCurrentPostId);
            mReactionsReference = mPostReference.collection("reactions");
            registerPostListener();
            registerReactionsListener();
            hasUserReacted(HomeActivity.userEmail);
        }

        public void registerPostListener() {
            mPostListenerRegistration = mFirestore.collection("posts")
                    .whereEqualTo("postId", mCurrentPostId)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null)
                                return;
                            DocumentChange change = documentSnapshots.getDocumentChanges().get(0);
                            currentPost = change.getDocument().toObject(Post.class);
                            switch (change.getType()) {
                                case ADDED:
                                case MODIFIED:
                                    mPostListener.onUserReactionChange(currentPost);
                                    break;
                            }
                        }
                    });
        }

        public void registerReactionsListener() {
            mReactionsListener = mPostReference.collection("history").orderBy("time").limit(20)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    Reaction reaction = change.getDocument()
                                            .toObject(Reaction.class);
                                    mPostListener.onReactionsChanged(reaction);
                                }
                            }
                        }
                    });
        }

        public void unregisterReactionsListener() {
            mReactionsListener.remove();
        }

        public Post getPreviousPost() {
            Post post = mPostsManager.getPreviousPost(mCurrentPostId);
            if (post != null)
                setCurrentPost(post);

            return post;
        }

        public void unregisterPostListener() {
            if (mPostListenerRegistration != null)
                mPostListenerRegistration.remove();
        }

        public void saveUserReaction(String newReaction) {

            if (newReaction.isEmpty())
                return;

            String reactionX;

            boolean isInitial = false;

            if (reaction != null) {
                if (reaction.equals(newReaction))
                    return;
                reactionX = "reaction" +
                        reaction.substring(0, 1).toUpperCase() + reaction.substring(1);
                mPostReference.update(reactionX, --reactionCount);
                mPostReference.collection("reactions").document(HomeActivity.userEmail)
                        .update("reactionType", newReaction, "time",
                                new Date().getTime(), "initial", false);
            } else {
                isInitial = true;
                Reaction saveReaction = new Reaction(HomeActivity.userEmail, newReaction,
                        true, new Date().getTime());

                mPostReference.collection("reactions").document(HomeActivity.userEmail)
                        .set(saveReaction);
            }

            setCurrentReaction(newReaction);
            reactionX = "reaction" +
                    reaction.substring(0, 1).toUpperCase() + reaction.substring(1);
            Reaction reactionData = new Reaction(HomeActivity.userEmail, reaction,
                    isInitial, new Date().getTime());

            mPostReference.update(reactionX, ++reactionCount);
            mPostReference.collection("history").document().set(reactionData);
        }
    }
}
