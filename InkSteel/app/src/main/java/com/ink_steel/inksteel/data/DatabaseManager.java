package com.ink_steel.inksteel.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
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
import com.ink_steel.inksteel.helpers.StudiosQueryTask;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Reaction;
import com.ink_steel.inksteel.model.Studio;
import com.ink_steel.inksteel.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

public class DatabaseManager implements StudiosQueryTask.StudiosListener {

    private static final String DEFAULT_PROFILE_PICTURE = "https://firebasestorage.googleapis.com/" +
            "v0/b/inksteel-7911e.appspot.com/o/default.jpg?alt=media&token=2a0f4edc-81e5-" +
            "-9558-015e18b8b1ff";
    private static DatabaseManager mDatabaseManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;
    private User mCurrentUser;
    //    -- Users --
    private HashMap<String, User> mUsers;
    private HashMap<String, User> exploreUsers;
    private HashMap<String, User> mFriends;
    private String thumbnailDownloadUrl;
    private String imageDownloadUrl;
    //    -- Post --
    private Post currentPost;
    private ListenerRegistration mPostListenerRegistration;
    private ListenerRegistration mReactionsListenerRegistration;
    private HashMap<String, Integer> reactions = new HashMap<>();
    private TreeSet<Post> mPosts = new TreeSet<>();
    private String reaction;
    private int reactionCount;
    //    -- Chat --
    private Activity mActivity;
    private boolean isInitialLoad = true;
    private ListenerRegistration chatRoomsRegistration;
    private ListenerRegistration chatRegistration;
    private TreeSet<ChatRoom> mUserChatRooms;
    private boolean isChatRoomsInitialLoad;
    private ArrayList<Message> chatMessages;
    private boolean isChatMessagesInitialLoad;
    //    -- Studios --
    private HashMap<String, Studio> mStudios;
    private StudiosQueryTask.StudiosListener mListener;

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

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

//    ------------------------------------ Login/register ------------------------------------

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
                    if (snapshot.exists()) {
                        mCurrentUser = snapshot.toObject(User.class);
                    } else {
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

    public void signOut() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
    }

    public void updateUserInfo(final UserInfoListener listener, Bitmap bitmap) {
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
                            updateUserInfo(listener);
                        }
                    }
                });
    }

    public void updateUserInfo(final UserInfoListener listener) {
        String defaultProfileImage = mCurrentUser.getProfileImage();
        if (defaultProfileImage == null || defaultProfileImage.isEmpty()) {
            mCurrentUser.setProfileImage(DEFAULT_PROFILE_PICTURE);
        }
        mFirestore.collection("users").document(mCurrentUser.getEmail())
                .update("name", mCurrentUser.getName(),
                        "age", mCurrentUser.getAge(),
                        "city", mCurrentUser.getCity(),
                        "profileImage", mCurrentUser.getProfileImage())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onUserInfoSaved();
                    }
                });
    }

//    ------------------------------------ Gallery ------------------------------------

    public void saveImage(Uri uri, final GalleryImageAddListener listener) {
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
                            listener.onGalleryImageAdded();
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

//    ------------------------------------ Explore ------------------------------------

    private void loadUsers() {
        loadUsers(null);
    }

    private void loadUsers(final UsersListener listener) {
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
                            if (listener != null)
                                getExploreUsers(listener);
                        }
                    }
                });
    }

    public void loadExplore(UsersListener listener) {
        if (mUsers == null) {
            loadUsers(listener);
        } else {
            getExploreUsers(listener);
        }
    }

    private void getExploreUsers(UsersListener listener) {
        exploreUsers = new HashMap<>(mUsers);
        exploreUsers.keySet().remove(mCurrentUser.getEmail());
        exploreUsers.keySet().removeAll(mCurrentUser.getFriends());
        exploreUsers.keySet().removeAll(mCurrentUser.getLiked());
        listener.onUsersLoaded();
    }

    public ArrayList<User> getExploreUsers() {
        return new ArrayList<>(exploreUsers.values());
    }

    public void addLike(String email) {
        mCurrentUser.getLiked().add(email);
        User user = mUsers.get(email);
        mFirestore.collection("users")
                .document(mCurrentUser.getEmail())
                .update("liked", mCurrentUser.getLiked());
        if (user.getLiked().contains(mCurrentUser.getEmail())) {
            addFriend(email);
        }
    }

    private void addFriend(String email) {
        mCurrentUser.getFriends().add(email);
        mFirestore.collection("users")
                .document(mCurrentUser.getEmail())
                .update("friends", mCurrentUser.getFriends());
        User user = mUsers.get(email);
        user.getFriends().add(mCurrentUser.getEmail());
        mFirestore.collection("users")
                .document(email)
                .update("friends", user.getFriends());
    }

    public void loadFriends(UsersListener listener) {
        if (mUsers == null) {
            loadUsers();
        }
        mFriends = new HashMap<>();
        for (String email : mCurrentUser.getFriends()) {
            if (mUsers.containsKey(email)) {
                User u = mUsers.get(email);
                mFriends.put(u.getEmail(), u);
            }
        }
        listener.onUsersLoaded();
    }

    public ArrayList<User> getFriends() {
        return new ArrayList<>(mFriends.values());
    }

//    ------------------------------------ Posts ------------------------------------

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

    private void unregisterPostListeners() {
        if (mReactionsListenerRegistration != null)
            mReactionsListenerRegistration.remove();
        if (mPostListenerRegistration != null)
            mPostListenerRegistration.remove();
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

    // get the post's reaction's count real time
    private void registerPostListener(final PostReactionsListener listener) {
        mPostListenerRegistration = mFirestore.collection("posts")
                .document(currentPost.getPostId())
                .addSnapshotListener(mActivity, new EventListener<DocumentSnapshot>() {
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
                .orderBy("time").addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
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

    private void setCurrentReaction(String reactionType) {
        reaction = reactionType;
        reactionCount = currentPost.getReactionCount(reaction);
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

    public ArrayList<Post> getPosts() {
        return new ArrayList<>(mPosts);
    }

    // get all posts real time
    public void registerPostsListener(final PostsListener listener) {
        Query postsQuery = mFirestore.collection("posts")
                .orderBy("createdAt");
        postsQuery.addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
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

//    ------------------------------------ Chat ------------------------------------

    // create chat room for current user and given user's email
    public void createChatRoom(String email, ChatRoomCreatedListener listener) {
        DocumentReference reference = mFirestore.collection("chatRooms").document();
        Message message = new Message(mCurrentUser.getName(), "Chat room created!",
                new Date().getTime());
        User user = mUsers.get(email);
        ChatRoom chatRoom1 = new ChatRoom(reference.getId(), email, user.getProfileImage(),
                user.getName(), message.getMessage(), message.getTime(), message.getUserName(),
                false);
        ChatRoom chatRoom2 = new ChatRoom(reference.getId(), mCurrentUser.getEmail(),
                mCurrentUser.getProfileImage(), mCurrentUser.getName(), message.getMessage(),
                message.getTime(), message.getUserName(), false);
        reference.collection("messages").add(message);

        mFirestore.collection("users").document(email).collection("chatRooms")
                .document(reference.getId()).set(chatRoom2);

        mFirestore.collection("users").document(mCurrentUser.getEmail())
                .collection("chatRooms").document(reference.getId()).set(chatRoom1);

        listener.onChatRoomCreated(chatRoom1);
    }

    public ArrayList<ChatRoom> getUserChatRooms() {
        return new ArrayList<>(mUserChatRooms);
    }

    public void getUserChatRooms(final UserChatRoomsListener listener) {
        if (mUserChatRooms == null) {
            isChatRoomsInitialLoad = true;
            mUserChatRooms = new TreeSet<>();
        }
        chatRoomsRegistration = mFirestore.collection("users")
                .document(mCurrentUser.getEmail()).collection("chatRooms")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null)
                    return;
                for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                    ChatRoom chatRoom = change.getDocument().toObject(ChatRoom.class);
                    switch (change.getType()) {
                        case ADDED:
                            mUserChatRooms.add(change.getDocument().toObject(ChatRoom.class));
//                            break;
//                        case MODIFIED:
                            if (mUserChatRooms.contains(chatRoom))
                                mUserChatRooms.remove(chatRoom);
                            mUserChatRooms.add(chatRoom);
                            listener.onChatRoomChanged(chatRoom);
                            break;
                    }
                }
                if (isChatRoomsInitialLoad) {
                    listener.onChatRoomsLoaded();
                    isChatRoomsInitialLoad = false;
                }
            }
                });
    }


    public void getChatMessagesById(String chatId, final ChatListener listener) {

        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
            isChatMessagesInitialLoad = true;
        }
        chatRegistration = mFirestore.collection("chatRooms").document(chatId)
                .collection("messages").orderBy("time")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null)
                            return;
                for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Message message = change.getDocument().toObject(Message.class);
                        chatMessages.add(message);
                        if (!isChatMessagesInitialLoad) {
                            listener.onMessageAdded(message);
                        }
                    }
                }
                        if (isChatMessagesInitialLoad) {
                    listener.onMessagesLoaded();
                            isChatMessagesInitialLoad = false;
                }
            }
        });
    }

    public void addMessage(Message message, ChatRoom chatRoom) {
        mFirestore.collection("chatRooms").document(chatRoom.getChatId())
                .collection("messages").add(message);
        boolean seen = mCurrentUser.getName().equals(message.getUserName());
        mFirestore.collection("users").document(mCurrentUser.getEmail())
                .collection("chatRooms").document(chatRoom.getChatId())
                .update("lastMessage", message.getMessage(),
                        "lastMessageTime", message.getTime(),
                        "lastMessageSender", mCurrentUser.getEmail(),
                        "seen", seen);
        mFirestore.collection("users").document(chatRoom.getEmail())
                .collection("chatRooms").document(chatRoom.getChatId())
                .update("lastMessage", message.getMessage(),
                        "lastMessageTime", message.getTime(),
                        "lastMessageSender", mCurrentUser.getEmail(),
                        "seen", !seen);
    }

    public ChatRoom isChatRoomCreated(String email) {
        for (ChatRoom chatRoom : mUserChatRooms) {
            if (chatRoom.getEmail().equals(email)) {
                return chatRoom;
            }
        }
        return null;
    }

    public List<Message> getChatMessages() {
        return Collections.unmodifiableList(chatMessages);
    }

    public void unregisterChatRoomsListener() {
        if (chatRoomsRegistration != null) {
            chatRoomsRegistration.remove();
            isChatRoomsInitialLoad = true;
        }
    }

    public void unregisterChatListener() {
        if (chatRegistration != null) {
            chatRegistration.remove();
            isChatMessagesInitialLoad = true;
            chatMessages.clear();
        }
    }

    public interface ChatListener {
        // new message
        void onMessageAdded(Message message);

        // history message
        void onMessagesLoaded();
    }

    public interface UserChatRoomsListener {
        // all chatRooms loaded
        void onChatRoomsLoaded();

        // chatRoom is changed
        void onChatRoomChanged(ChatRoom a);
    }

    public interface UserManagerListener {
        // already signed in or just signing in
        void onUserLogInError(String error);
        // new user
        void onUserSignUpError(String error);
        // user loaded
        void onUserInfoLoaded();
    }

    public interface UserInfoListener {
        // after registration or when change information
        void onUserInfoSaved();
    }

    public interface UsersListener {
        // all users loaded
        void onUsersLoaded();
    }

    public interface GalleryImageAddListener {
        // when added a new image in gallery
        void onGalleryImageAdded();
    }

    public interface PostSavedListener {
        // post saved to database
        void onPostSaved();
    }

    public interface PostsListener {
        // posts added
        void onPostAdded(Post post);

        // posts changed
        void onPostsLoaded();
    }

    public interface PostReactionsListener {
        // reactions changed
        void onPostReactionsChanged();

        // reactions added
        void onReactionAdded(Reaction reaction);
    }

    public interface ChatRoomCreatedListener {
        // created chatRoom
        void onChatRoomCreated(ChatRoom chatRoom);
    }

    public interface StudioListener {
        // studio information loaded
        void onStudioInfoLoaded(Studio studio);
    }

//    ------------------------------------ Studios ------------------------------------

    public Studio getStudioById(String id) {
        return mStudios.get(id);
    }

    public void getNearbyStudios(StudiosQueryTask.StudiosListener listener, Location location) {
        if (mStudios == null) {
            mStudios = new HashMap<>();
            mListener = listener;
            StudiosQueryTask task = new StudiosQueryTask(this, location);
            task.execute();
        } else {
            listener.onStudiosLoaded();
        }
    }

    public void getNearbyProfileCityStudios(StudiosQueryTask.StudiosListener listener, String location) {
        if (mStudios == null) {
            mStudios = new HashMap<>();
            mListener = listener;
            StudiosQueryTask task = new StudiosQueryTask(this, location);
            task.execute();
        } else {
            listener.onStudiosLoaded();
        }
    }

    public ArrayList<Studio> getStudios() {
        return new ArrayList<>(mStudios.values());
    }

    public void getStudioInfoById(final String studioId, GeoDataClient client, final StudioListener listener) {
        final Studio studio = mStudios.get(studioId);
        if (studio.getGooglePlace() != null) {
            listener.onStudioInfoLoaded(studio);
        } else {
            client.getPlaceById(studioId)
                    .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                            if (task.isSuccessful()) {
                                studio.setGooglePlace(task.getResult().get(0));
                                if (listener != null)
                                    listener.onStudioInfoLoaded(studio);
                            }
                        }
                    });
        }
    }

    public void getStudioInfoById(final String studioId, GeoDataClient client) {
        getStudioInfoById(studioId, client, null);
    }
    @Override
    public void onStudioLoaded(Studio studio) {
        mStudios.put(studio.getPlaceId(), studio);
        if (mListener != null)
            mListener.onStudioLoaded(studio);
    }

    @Override
    public void onStudiosLoaded() {
        mListener.onStudiosLoaded();
    }

    @Override
    public void onNoStudios() {
        mListener.onNoStudios();
    }
}