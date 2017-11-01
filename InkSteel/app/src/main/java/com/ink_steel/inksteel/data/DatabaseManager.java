package com.ink_steel.inksteel.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import com.ink_steel.inksteel.adapters.GalleryRecyclerViewAdapter;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;
import com.ink_steel.inksteel.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

public class DatabaseManager {

    public interface UserManagerListener {
        // already signed in or just signing in
        void onUserLogInError(String error);

        // new user
        void onUserSignUpError(String error);

        // user loaded
        void onUserInfoLoaded();
    }

    public interface UserInfoListener {
        void onUserInfoSaved();
    }

    // all users loaded
    public interface UsersListener {
        void onUsersLoaded();
    }

    // all friends loaded
    public interface FriendsListener {
        void onFriendsLoaded();
    }

    // post saved to database
    public interface PostSavedListener {
        void onPostSaved();
    }

    // posts added / changed
    public interface PostsListener {

        void onPostAdded(Post post);

        void onPostsLoaded();
    }

    // reactions added / changed
    public interface PostReactionsListener {
        void onPostReactionsChanged();

        void onReactionAdded(Reaction reaction);
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;
    private static DatabaseManager mDatabaseManager;
    private User mCurrentUser;

    private DatabaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public static DatabaseManager getInstance() {
        if (mDatabaseManager == null)
            mDatabaseManager = new DatabaseManager();
        return mDatabaseManager;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

//    ------------------------------------ User login/register ------------------------------------

    public void checkIfSignedIn(UserManagerListener listener) {
        if (mAuth.getCurrentUser() != null) {
            loadUserInfo(listener, mAuth.getCurrentUser().getEmail());
        }
    }

    private void loadUserInfo(final UserManagerListener listener, final String email) {
        final DocumentReference userReference = mFirestore.collection("users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists())
                        mCurrentUser = snapshot.toObject(User.class);
                    else {
                        mCurrentUser = new User(email, "", "", "", "");
                        userReference.set(mCurrentUser);
                    }
                    listener.onUserInfoLoaded();
                }
            }
        });
    }

    public void signUpUser(final UserManagerListener listener, final String email,
                           final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            loginUser(listener, email, password);
                        else if (task.getException() != null)
                            listener.onUserSignUpError(task.getException().getLocalizedMessage());
                        else listener.onUserSignUpError(null);
                    }
                });
    }

    public void loginUser(final UserManagerListener listener, final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadUserInfo(listener, email);
                        } else if (task.getException() != null) {
                            listener.onUserLogInError(task.getException().getLocalizedMessage());
                        } else
                            listener.onUserLogInError(null);
                    }
                });
    }

    public void updateUserInfo(UserInfoListener listener, Bitmap bitmap) {
        uploadImage(listener, bitmap);
    }

    private void uploadImage(final UserInfoListener listener, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        mStorage.child(mCurrentUser.getEmail() + "/profile.jpeg").putBytes(baos.toByteArray())
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Uri downloadUri = task.getResult().getDownloadUrl();
                        if (task.isSuccessful() && downloadUri != null) {
                            mCurrentUser.setProfileImage(downloadUri.toString());
                            mFirestore.collection("users").document(mCurrentUser.getEmail())
                                    .update("name", mCurrentUser.getName(),
                                            "age", mCurrentUser.getAge(),
                                            "country", mCurrentUser.getCity(),
                                            "profileImage", mCurrentUser.getProfileImage())
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

//    ------------------------------------ User gallery ------------------------------------

    public void saveImage(Uri uri, final GalleryRecyclerViewAdapter mAdapter) {
        mStorage.child(mCurrentUser.getEmail() + "/pics/"
                + new Date().getTime() + ".jpeg")
                .putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Uri downloadUri = task.getResult().getDownloadUrl();
                        if (task.isSuccessful() && downloadUri != null) {
                            mCurrentUser.getGallery().add(downloadUri.toString());
                            mFirestore.collection("users")
                                    .document(mCurrentUser.getEmail())
                                    .update("gallery", mCurrentUser.getGallery());
                            mAdapter.notifyDataSetChanged();
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
                                .document(mCurrentUser.getEmail())
                                .update("gallery", mCurrentUser.getGallery());
                    }
                });
    }

//    ------------------------------------ User explore ------------------------------------

    private HashMap<String, User> mUsers;
    private ArrayList<User> mFriends;

    public void loadUsers(final UsersListener listener) {

        if (mUsers == null) {
            mUsers = new HashMap<>();
        }

        mFirestore.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.exists()) {
                                    User user = snapshot.toObject(User.class);
                                    mUsers.put(user.getEmail(), user);
                                }
                            }
                            listener.onUsersLoaded();
                        }
                    }
                });
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(mUsers.values());
    }

    public void addLike(String email) {
        mCurrentUser.getLikes().add(email);
        mFirestore.collection("users")
                .document(mCurrentUser.getEmail())
                .update("likes", mCurrentUser.getLikes());
    }

    public void addFriend(String email) {
        mCurrentUser.getFriends().add(email);
        mFirestore.collection("users")
                .document(mCurrentUser.getEmail())
                .update("friends", mCurrentUser.getFriends());
    }

    public ArrayList<User> getUserFriends() {
        if (mFriends == null) {
            mFriends = new ArrayList<>();
            for (String email : mCurrentUser.getFriends()) {
                if (mUsers.containsKey(email)) {
                    mFriends.add(mUsers.get(email));
                }
            }
        }
        return mFriends;
    }



//    ------------------------------------ Posts ------------------------------------

    private String thumbnailDownloadUrl;
    private String imageDownloadUrl;

    public void savePost(final PostSavedListener listener, Bitmap thumbnail,
                         final Uri image, final String description) {
        final StorageReference reference = mStorage.child("posts/" + UUID.randomUUID());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        reference.child("thumbnail.jpeg").putBytes(baos.toByteArray())
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                            thumbnailDownloadUrl = task.getResult().getDownloadUrl().toString();
                            if (imageDownloadUrl != null)
                                savePostToFirestore(listener, description);
                        }
                    }
                });
        reference.child("image.jpeg").putFile(image)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                            imageDownloadUrl = task.getResult().getDownloadUrl().toString();
                            if (thumbnailDownloadUrl != null)
                                savePostToFirestore(listener, description);
                        }
                    }
                });

    }

    private void savePostToFirestore(final PostSavedListener listener,
                                     String description) {
        DocumentReference reference = mFirestore.collection("posts").document();
        Post post = new Post(reference.getId(), mCurrentUser.getEmail(),
                new Date().getTime(), mCurrentUser.getProfileImage(), imageDownloadUrl,
                thumbnailDownloadUrl, description);
        reference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    listener.onPostSaved();
            }
        });
    }

    //    -- Post --
    private Post currentPost;
    private ListenerRegistration mPostListenerRegistration;
    private ListenerRegistration mReactionsListenerRegistration;
    private HashMap<String, Integer> reactions = new HashMap<>();
    private TreeSet<Post> mPosts = new TreeSet<>();
    private String reaction;
    private int reactionCount;
    private ListenerRegistration mPostsListenerRegistration;
    private boolean isInitialLoad = true;

    public Post getPost(PostReactionsListener listener, String postId) {
        boolean found = false;
        for (Post post : mPosts) {
            if (post.getPostId().equals(postId)) {
                setPost(listener, post);
                found = true;
                break;
            }
        }
        return found ? currentPost : null;
    }

    private void setPost(PostReactionsListener listener, Post post) {
        currentPost = post;
        unregisterPostListeners();
        reaction = null;
        reactionCount = 0;
        hasUserReacted();
        registerPostListener(listener);
        registerReactionsListener(listener);
    }

    public Post getNextPost(PostReactionsListener listener) {
        Post post = mPosts.higher(currentPost);
        if (post == null)
            return null;
        setPost(listener, post);
        return post;
    }

    public Post getPreviousPost(PostReactionsListener listener) {
        Post post = mPosts.lower(currentPost);
        if (post == null)
            return null;
        setPost(listener, post);
        return post;
    }

    private void unregisterPostListeners() {
        if (mReactionsListenerRegistration != null)
            mReactionsListenerRegistration.remove();
        if (mPostListenerRegistration != null)
            mPostListenerRegistration.remove();
    }

    public TreeSet<Post> getPosts() {
        return mPosts;
    }

    // get the post's reaction's count real time
    private void registerPostListener(final PostReactionsListener listener) {
        mPostListenerRegistration = mFirestore.collection("posts")
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
    private void registerReactionsListener(final PostReactionsListener listener) {
        mReactionsListenerRegistration = mFirestore
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
        mFirestore.collection("posts").document(currentPost.getPostId())
                .collection("reactions").document(mCurrentUser.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.exists()) {
                            setCurrentReaction(snapshot.getString("reactionType"));
                            Log.d("reaction", reaction + " --" + currentPost.getPostId());
                        }
                    }
                });
    }

    private void setCurrentReaction(String reactionType) {
        reaction = reactionType;
        reactionCount = currentPost.getReactionCount(reaction);
    }

    // get all posts real time
    public void registerPostsListener(final PostsListener listener) {
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
                            DocumentSnapshot snapshot = change.getDocument();
                            if (snapshot.exists()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    Post post = snapshot.toObject(Post.class);
                                    mPosts.add(post);
                                    if (!isInitialLoad) {
                                        listener.onPostAdded(post);
                                    }
                                }
                            }
                        }

                        if (isInitialLoad) {
                            listener.onPostsLoaded();
                            isInitialLoad = false;
                        }

                    }
                });
    }

    public void unregisterPostsListener() {
        if (mPostsListenerRegistration != null)
            mPostsListenerRegistration.remove();
    }

    public void saveUserReaction(String newReaction) {

        if (newReaction.isEmpty())
            return;

        if (reactions.isEmpty()) {
            reactions.put("like", 0);
            reactions.put("blush", 1);
            reactions.put("devil", 2);
            reactions.put("dazed", 3);
        }

        DocumentReference postReference = mFirestore
                .collection("posts").document(currentPost.getPostId());
        String email = mCurrentUser.getEmail();

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

}
