package com.ink_steel.inksteel.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;
import com.ink_steel.inksteel.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class FirebaseManager {

    private static PostsManager mPostsManager;
    private static FirebaseManager mFirebaseManager;
    private static UserManager mUserManager;
    private static PostManager mPostManager;
    private static GalleryManager mGalleryManager;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;
    private User currentUser;

    private FirebaseManager() {
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mUserManager = new UserManager();
    }

    public static FirebaseManager getInstance() {
        if (mFirebaseManager == null)
            mFirebaseManager = new FirebaseManager();
        return mFirebaseManager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public UserManager getUserManager() {
        if (mUserManager == null)
            mUserManager = new UserManager();
        return mUserManager;
    }

    public GalleryManager getGalleryManager() {
        if (mGalleryManager == null)
            mGalleryManager = new GalleryManager();
        return mGalleryManager;
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

    public interface UsersListener {
        void onUsersLoaded();
    }

    public interface CurrentUserInfoListener {
        void onInfoLoaded(boolean isNewUser);
    }

    public interface OnSaveUserInfo {
        void onUserInfoSaved();
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

    public class GalleryManager {

        public void saveImage(Uri uri) {
            mStorage.child(currentUser.getEmail() + "/pics/"
                    + new Date().getTime() + ".jpeg")
                    .putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Uri downloadUri = task.getResult().getDownloadUrl();
                            if (task.isSuccessful() && downloadUri != null) {
                                currentUser.getGallery().add(downloadUri.toString());
                                mFirestore.collection("users")
                                        .document(currentUser.getEmail())
                                        .update("gallery", currentUser.getGallery());
                            }
                        }
                    });
        }

        public void removeImage(final String imageUrl) {
            FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFirestore.collection("users")
                                    .document(mUserManager.getCurrentUser().getEmail())
                                    .update("gallery", mUserManager.getCurrentUser().getGallery());
                        }
                    });
        }

    }

    public class UserManager {

        private ArrayList<User> users;

        private UserManager() {
            users = new ArrayList<>();
        }

        public User getCurrentUser() {
            return currentUser;
        }

        public void loadUsers(final UsersListener listener) {
            mFirestore.collection("users").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            for (DocumentSnapshot snapshot : documentSnapshots.getDocuments()) {
                                if (snapshot.exists()) {
                                    users.add(snapshot.toObject(User.class));
                                }
                            }
                            listener.onUsersLoaded();
                        }
                    });
        }

        public ArrayList<User> getUsers() {
            return users;
        }

        public void updateUserInfo(OnSaveUserInfo listener, Bitmap bitmap) {
            uploadImage(listener, bitmap);
        }

        private void uploadImage(final OnSaveUserInfo listener, Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            mStorage.child(currentUser.getEmail() + "/profile.jpeg").putBytes(baos.toByteArray())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Uri downloadUri = task.getResult().getDownloadUrl();
                            if (task.isSuccessful() && downloadUri != null) {
                                currentUser.setProfileImage(downloadUri.toString());
                                mFirestore.collection("users").document(currentUser.getEmail())
                                        .update("name", currentUser.getName(),
                                                "age", currentUser.getAge(),
                                                "city", currentUser.getCity(),
                                                "profileImage", currentUser.getProfileImage())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                listener.onUserInfoSaved();
                                            }
                                        });
                            }
                        }
                    });
        }

        public void loadUserInfo(final CurrentUserInfoListener listener, String userEmail,
                                 final boolean isNewUser) {
            DocumentReference userReference = mFirestore.collection("users").document(userEmail);
            if (isNewUser) {
                currentUser = new User(userEmail, "", "", "", "");
                userReference.set(currentUser);
                listener.onInfoLoaded(true);
            } else {
                userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            currentUser = documentSnapshot.toObject(User.class);
                            listener.onInfoLoaded(false);
                        }
                    }
                });
            }
        }
    }
}
