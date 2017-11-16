package com.ink_steel.inksteel.data;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ink_steel.inksteel.helpers.RetrofitStudios;
import com.ink_steel.inksteel.helpers.StudioTypeAdapterFactory;
import com.ink_steel.inksteel.model.ChatRoom;
import com.ink_steel.inksteel.model.Message;
import com.ink_steel.inksteel.model.Post;
import com.ink_steel.inksteel.model.Post.ReactionType;
import com.ink_steel.inksteel.model.Reaction;
import com.ink_steel.inksteel.model.Studio;
import com.ink_steel.inksteel.model.StudiosResponse;
import com.ink_steel.inksteel.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Singleton class for managing database operations.
 * Contains inner classes, which specialize in different aspects of the operations.
 */
public class DatabaseManager {
    // Instances of singleton classes
    private static DatabaseManager mDatabaseManager;
    private static UserManager mUserManager;
    private static PostsManager mPostsManager;
    private static StudiosManager mStudiosManager;
    private static UsersManager mUsersManager;
    private static ChatManager mChatManager;
    private static GalleryManager mGalleryManager;

    // Instances of Firebase related classes
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mFirebaseStorage;

    // Fields, containing the current user and his references in the database
    private User mCurrentUser;
    private DocumentReference mCurrentUserReference;
    private StorageReference mCurrentUserStorageReference;

    // Private constructor, which initializes the Firebase fields
    private DatabaseManager() {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance().getReference();
    }

    /*
     * Logically first to call, so creates the instance of DatabaseManager
     *
     * @return instance of UserManager inner class
     */
    public static UserManager getUserManager() {
        if (mDatabaseManager == null)
            mDatabaseManager = new DatabaseManager();
        if (mUserManager == null)
            mUserManager = mDatabaseManager.new UserManager();
        return mUserManager;
    }

    /*
     * @return instance of PostsManager
     */
    public static PostsManager getPostsManager() {
        if (mPostsManager == null)
            mPostsManager = mDatabaseManager.new PostsManager();
        return mPostsManager;
    }

    /*
     * @return instance of StudiosManager
     */
    public static StudiosManager getStudiosManager() {
        if (mStudiosManager == null)
            mStudiosManager = mDatabaseManager.new StudiosManager();
        return mStudiosManager;
    }

    /*
     * @return instance of UsersManager
     */
    public static UsersManager getUsersManager() {
        if (mUsersManager == null)
            mUsersManager = mDatabaseManager.new UsersManager();
        return mUsersManager;
    }

    public static ChatManager getChatManager() {
        if (mChatManager == null)
            mChatManager = mDatabaseManager.new ChatManager();
        return mChatManager;
    }

    public static GalleryManager getGalleryManager() {
        if (mGalleryManager == null)
            mGalleryManager = mDatabaseManager.new GalleryManager();
        return mGalleryManager;
    }

    /*
                 * Interface, which notifies when the information for the current user
                 * is fetched from the database
                 */
    public interface OnUserInfoLoadedListener {
        /*
         * Method, that gets called when the fetching is done
         * Called from {@link UserManager#loadUserInfo()}
         */
        void onUserInfoLoaded();

        /*
         * Method, that gets called when the user's information wasn't able
         * to be fetched from the database
         * Called from {@link UserManager#loadUserInfo()}
         */
        void onError(String message);
    }

    /*
     * Interface, which notifies the result when the user
     * tries to log in or register
     */
    public interface OnUserLoginListener {
        /*
         * Method, that gets called when the user has successfully
         * logged in
         * Called from {@link UserManager#loadUserInfo()}
         */
        void onUserLoggedIn(boolean isNewUser);

        /*
         * Method, that gets called when the user wasn't able to log in
         * @param message, explaining the error
         * Called from {@link UserManager#createLoginErrorMessage()}
         */
        void onError(String message);
    }

    /*
     * Interface, which notifies the result when the user
     * tries to change his information
     * from {@link com.ink_steel.inksteel.fragments.AddPostFragment}
     */
    public interface UserInfoUpdatedListener {
        /*
         * Method, that gets called when the user's information has been
         * successfully updated
         * Called from {@link UserManager#updateUserInfo()}
         */
        void onUserInfoUpdated();

        /*
         * Method, that gets called when the user's information wasn't
         * able to be saved
         * @param message, explaining the error
         * Called from {@link UserManager#updateUserInfo()}
         */
        void onError(String message);
    }

    /*
     * Interface, which notifies when posts are added or loaded from history
     */
    public interface PostsListener {

        /*
         * Method, that gets called when new posts are added
         * Called from {@link PostsManager#registerPostsListener()}
         *
         * @param post Post object, representing the newly added post
         * @param isNewPost boolean, indicating whether the post
         * is added after the initial posts load
         */
        void onPostAdded(Post post, boolean isNewPost);

        /*
         * Method, that gets called when more posts are loaded from history
         * Called from {@link PostsManager#loadMorePosts()}
         *
         * @param post Post object, representing the newly added post
         */
        void onMorePostsLoaded(Post post);

        /*
         * Method, that gets called when there's an error
         * fetching posts
         * Called from {@link PostsManager#registerPostsListener()}
         *
         * @param message, explaining the error
         */
        void onError(String errorMessage);
    }

    /*
     * Interface, which notifies when error occurs while saving post to database
     */
    public interface OnPostSavedErrorListener {

        /*
         * Method, that gets called when there's an error when
         * saving post
         * Called from {@link PostsManager#savePostToFirestore()}
         *
         * @param message, explaining the error
         */
        void onError(String message);
    }

    public interface PostListener {
        void onPostChanged(Post post);

        void onReactionAdded(Reaction reaction);

        void onError(String errorMessage);
    }

    public interface StudioInfoListener {
        void onStudioInfoLoaded();

        void onStudioLoaded(Studio source);
    }

    public interface UsersListener {
        void onUsersLoaded();

        void onError(String errorMessage);
    }

    public interface ChatRoomListener {
        void onChatRoomAdded(ChatRoom chatRooms);

        void onChatRoomModified(ChatRoom chatRoom);

        void onChatRoomLoaded();

        void onError(String message);
    }

    public interface ChatListener {

        void onMessageAdded(Message message, boolean isNew);

//        void onMoreMessagesLoaded();
    }

    public interface GalleryImageAddListener {
        void onGalleryImageAdded();
    }

    public interface NotificationManagerListener {
        void onUnseenMessage(ChatRoom chatRoom, List<Message> messages);
    }

    public static class NotificationsManager {
        public static void listenForNewMessages(final NotificationManagerListener listener) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || user.getEmail() == null)
                return;
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getEmail())
                    .collection("chatRooms")
                    .whereEqualTo("seen", false)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null)
                                return;

                            for (DocumentSnapshot snapshot : documentSnapshots) {
                                ChatRoom chatRoom = snapshot.toObject(ChatRoom.class);
                                getHistory(listener, chatRoom);
                            }
                        }
                    });
        }

        private static void getHistory(final NotificationManagerListener listener,
                                       final ChatRoom chatRoom) {
            final LinkedList<Message> messages = new LinkedList<>();
            FirebaseFirestore.getInstance()
                    .collection("chatRooms")
                    .document(chatRoom.getChatId())
                    .collection("messages")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(3)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Message message = snapshot.toObject(Message.class);
                                    messages.add(0, message);
                                }
                                listener.onUnseenMessage(chatRoom, messages);
                            }
                        }
                    });
        }

        public static void sendMessage(String chatId, String message, String receiverEmail) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || user.getEmail() == null)
                return;
            Message message1 = new Message(user.getEmail(), message, new Date().getTime(), false);
            DatabaseManager.mChatManager.addMessage(message1, chatId, receiverEmail);
        }

    }

    /*
     * Singleton class, that manages the operations connected to
     * logging in and signing up users, loading current user info after
     * successful log in, and updating the user's information
     */
    public class UserManager {

        // URL to the default profile image, which is displayed when the user hasn't chosen one
        private static final String DEFAULT_PROFILE_IMAGE = "https://firebasestorage.googleapis.com/" +
                "v0/b/inksteel-7911e.appspot.com/o/default.jpg?alt=media&token=2a0f4edc-81e5-" +
                "-9558-015e18b8b1ff";
        // Instance of FirebaseAuthenticaion, which is only used in this class
        private FirebaseAuth mFirebaseAuth;
        // Boolean that holds the information whether there is currently logged in user
        private boolean isUserSignedIn;
        // Boolean that holds the information whether the information about the user
        // is already loaded. If not, the listener gets called
        private boolean isInitialUserLoad;

        // Private constructor, which initializes the fields of UserManager
        private UserManager() {
            mFirebaseAuth = FirebaseAuth.getInstance();
            // Checks if there is already logged in user
            checkIfUserIsSignedIn();
            isInitialUserLoad = true;
        }

        /*
         * @return isUserSignedIn value
         */
        public boolean isUserSignedIn() {
            return isUserSignedIn;
        }

        /*
         * Method that checks is there's already logged in user
         * and sets the value of isUserSignedIn to true, if so
         */
        private void checkIfUserIsSignedIn() {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                mCurrentUserReference = mFirebaseFirestore.collection("users")
                        .document(user.getEmail());
                mCurrentUserStorageReference = mFirebaseStorage.child(user.getEmail());
                isUserSignedIn = true;
            }
        }

        /*
         * Method that loads the additional user information from Firebase
         * If it's the initial load, it calls the listener's onUserInfoLoaded() method
         * and sets the value of isInitialUserLoad to false,  because it listens for
         * changes in the user's information during the app's runtime
         *
         * @param OnUserInfoLoadedListener
         */
        public void loadUserInfo(final OnUserInfoLoadedListener listener) {
            mCurrentUserReference
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot,
                                            FirebaseFirestoreException e) {
                            // if there's an exception, return
                            if (e != null) {
                                listener.onError(e.getLocalizedMessage());
                                return;
                            }
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                mCurrentUser = documentSnapshot.toObject(User.class);
                                if (isInitialUserLoad) {
                                    listener.onUserInfoLoaded();
                                    isInitialUserLoad = false;
                                }
                            }
                        }
                    });
        }

        public void signOut() {
            mFirebaseAuth.signOut();
        }

        /*
         * Method that registers user and if the registration was successful,
         * the references to the current user's information are updated,
         * otherwise an error is returned
         *
         * @param listener OnUserInfoLoadedListener
         * @param email String for the email
         * @param password String for the password
         */
        public void registerUser(final OnUserLoginListener listener, String email, String password) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                setUserInfo(listener, task, true);
                            } else {
                                createLoginErrorMessage(listener, task);
                            }
                        }
                    });
        }

        /*
         * Method that logs in user and if the logging in was successful,
         * the references to the current user's information are updated,
         * otherwise an error is returned
         *
         * @param listener OnUserInfoLoadedListener
         * @param email String for the email
         * @param password String for the password
         */
        public void loginUser(final OnUserLoginListener listener, String email, String password) {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                setUserInfo(listener, task, false);
                            } else {
                                createLoginErrorMessage(listener, task);
                            }
                        }
                    });
        }

        /*
         * Method that sets the Firebase references according to the currently
         * logged in user
         *
         * @param listener OnUserInfoLoadedListener
         * @param task Task<AuthResult> that contains user information
         * @param isNewUser boolean that indicates whether the user has just been registered
         */
        private void setUserInfo(OnUserLoginListener listener, @NonNull Task<AuthResult> task,
                                 boolean isNewUser) {
            isUserSignedIn = true;
            FirebaseUser user = task.getResult().getUser();
            if (user.getEmail() != null) {
                mCurrentUserReference = mFirebaseFirestore.collection("users")
                        .document(user.getEmail());
                mCurrentUserStorageReference = mFirebaseStorage.child(user.getEmail());
                mCurrentUser = new User(user.getEmail());
            }
            listener.onUserLoggedIn(isNewUser);
        }

        /*
         * Helper method that creates the error messages from task reference
         *
         * @param listener OnUserInfoLoadedListener
         * @param task Task<AuthResult> that contains the error message
         */
        private void createLoginErrorMessage(OnUserLoginListener listener,
                                             @NonNull Task<AuthResult> task) {
            String errorMessage;
            if (task.getException() != null)
                errorMessage = task.getException().getLocalizedMessage();
            else errorMessage = "Unknown problem occur. Please try again!";
            listener.onError(errorMessage);
        }

        /*
         * Method that saves the information of the user, when changed
         * It saves the image to FirebaseStorage, and is successful, it
         * updates the user's information in the database
         *
         * @param listener OnUserInfoLoadedListener
         * @param bitmap Bitmap of the user's profile picture
         * @param isNewUser boolean that indicates whether the user is new and if there's
         * no image provided and the user is new, the default one should be set
        */
        public void updateUserInfo(final UserInfoUpdatedListener listener, Bitmap bitmap,
                                   final boolean isNewUser) {
            if (bitmap == null) {
                if (isNewUser) mCurrentUser.setProfileImage(DEFAULT_PROFILE_IMAGE);
                updateUserInfo(listener, isNewUser);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                mCurrentUserStorageReference.child("profile.jpeg").putBytes(baos.toByteArray())
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult().getDownloadUrl();
                                    if (downloadUri != null) {
                                        mCurrentUser.setProfileImage(downloadUri.toString());
                                        updateUserInfo(listener, isNewUser);
                                    } else {
                                        listener.onError("Problem saving image. " +
                                                "Please try again!");
                                    }
                                } else {
                                    String errorMessage;
                                    if (task.getException() != null)
                                        errorMessage = task.getException().getLocalizedMessage();
                                    else errorMessage = "Unknown error occur. Please try again!";
                                    listener.onError(errorMessage);
                                }
                            }
                        });
            }
        }

        /*
         * Method that updates the user information in the database,
         * after the image has been saved to the storage
         *
         * @param listener OnUserInfoLoadedListener
         * @param isNewUser boolean that indicates whether the user is new and if there's
         * no image provided and the user is new, the default one should be set
        */
        private void updateUserInfo(final UserInfoUpdatedListener listener, boolean isNewUser) {
            if (isNewUser) {
                mCurrentUserReference.set(mCurrentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    listener.onUserInfoUpdated();
                                } else {
                                    listener.onError("Problem updating information. " +
                                            "Please try again");
                                }
                            }
                        });
            } else {
                mCurrentUserReference.update(mCurrentUser.getUserInfo())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    listener.onUserInfoUpdated();
                                } else {
                                    listener.onError("Problem updating information. " +
                                            "Please try again");
                                }
                            }
                        });
            }
        }

        /*
         * @return User object, representing the current user
        */
        public User getCurrentUser() {
            return mCurrentUser;
        }
    }

    /*
     * Singleton class, that manages the operations connected to
     * loading and saving posts from database
     */
    public class PostsManager {

        // Reference to the posts's snapshot listener
        private ListenerRegistration mPostsListener;
        // Current post, displayed in PostInfoFragment
        private Map.Entry<Long, Post> mPost;
        // Collection of posts, order by creation time (key)
        private TreeMap<Long, Post> mPostsTree;

        private ArrayList<Long> loadedPostsId;
        // Variable that holds the creation time of last post, loaded from history
        // (they are not put in the collection)
        private long oldestPostLoaded;
        private PostListener mListener;
        private ListenerRegistration mReactionsListener;
        private long oldestReactionLoaded;
        private ListenerRegistration mPostListener;

        // Private constructor, which initializes the fields of PostsManager
        private PostsManager() {
            mPostsTree = new TreeMap<>();
            loadedPostsId = new ArrayList<>();
        }

        /*
         * Method, which fetches posts and listens for newly added
         * Assigns mPostsListener, which should be unregistered when
         * posts aren't on screen
         *
         * @param listener PostsListener
         */
        public void registerPostsListener(final PostsListener listener) {
            if (listener == null)
                return;
            long newestTime = 0L;
            if (mPostsTree.size() != 0) {
                newestTime = mPostsTree.lastKey();
            }
            mPostsListener = mFirebaseFirestore.collection("posts2")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(3)
                    .whereGreaterThan("createdAt", newestTime)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null)
                                listener.onError(e.getLocalizedMessage());
                            for (DocumentChange snapshot : documentSnapshots.getDocumentChanges()) {
                                if (snapshot.getType() == DocumentChange.Type.ADDED) {
                                    Post post = snapshot.getDocument().toObject(Post.class);
                                    loadedPostsId.add(post.getCreatedAt());
                                    mPostsTree.put(post.getCreatedAt(), post);
                                    listener.onPostAdded(post,
                                            mPostsTree.firstKey() == post.getCreatedAt());
                                    oldestPostLoaded = post.getCreatedAt();
                                }
                            }
                        }
                    });
        }

        /*
         * Method, which fetches posts from history
         *
         * @param listener PostsListener
         */
        public void loadMorePosts(final PostsListener listener) {
            if (listener == null)
                return;
            if (oldestPostLoaded == 0)
                oldestPostLoaded = mPostsTree.firstKey();
            mFirebaseFirestore.collection("posts2")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .whereLessThan("createdAt", oldestPostLoaded)
                    .limit(3)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    Post post = snapshot.toObject(Post.class);
                                    mPostsTree.put(post.getCreatedAt(), post);
                                    listener.onMorePostsLoaded(post);
                                    oldestPostLoaded = post.getCreatedAt();
                                }
                            }
                        }
                    });
        }

        /*
         * Method, which unregisters mPostsListener
         */
        public void unregisterPostsListener() {
            if (mPostsListener != null) {
                if (loadedPostsId != null && loadedPostsId.size() != 0) {
                    oldestPostLoaded = loadedPostsId.get(loadedPostsId.size() - 1);
                    mPostsTree.keySet().retainAll(loadedPostsId);
                }
                mPostsListener.remove();
            }
        }


        /*
         * Method, which returns a list of sorted Posts
         */
        public List<Post> getPosts() {
            return new LinkedList<>(mPostsTree.descendingMap().values());
        }

        /*
         * Method, which saves a post
         *
         * @param listener OnPostSavedErrorListener called when error occurs
         * @param thumbnail Bitmap of the post's thumbnail image
         * @param image Uri of the post's image
         * @param description String
         */
        public void savePost(final OnPostSavedErrorListener listener, final Bitmap thumbnail,
                             Uri image, String description) {
            StorageReference postRef = mFirebaseStorage.child("posts2/" + UUID.randomUUID());
            saveThumbnail(listener, postRef, getImageByteArray(thumbnail), image,
                    description);
        }

        /*
         * Helper method, which converts bitmap to byte array
         *
         * @param thumbnail Bitmap of the post's thumbnail image
         */
        @NonNull
        private byte[] getImageByteArray(Bitmap image) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }

        /*
         * Helper method, which saves the thumbnail to the storage
         *
         * @param listener OnPostSavedErrorListener called when error occurs
         * @param reference post's StorageReference
         * @param thumbnail byte[] of the post's thumbnail image
         * @param image Uri of the post's image
         * @param description String
         */
        private void saveThumbnail(final OnPostSavedErrorListener listener,
                                   final StorageReference reference, byte[] thumbnail,
                                   final Uri image, final String description) {
            reference.child("thumbnail.jpeg").putBytes(thumbnail)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                                saveImage(listener, reference,
                                        task.getResult().getDownloadUrl().toString(), image,
                                        description);
                            } else {
                                String errorMessage;
                                if (task.getException() != null)
                                    errorMessage = task.getException().getLocalizedMessage();
                                else errorMessage = "Unknown error occur. Please try again!";
                                listener.onError(errorMessage);
                            }
                        }
                    });
        }

        /*
         * Helper method, which saves the image to the storage
         *
         * @param listener OnPostSavedErrorListener called when error occurs
         * @param reference post's StorageReference
         * @param thumbnailUrl String of the post's thumbnail image download url
         * @param image Uri of the post's image
         * @param description String
         */
        private void saveImage(final OnPostSavedErrorListener listener,
                               final StorageReference reference, final String thumbnailUrl,
                               Uri image, final String description) {
            reference.child("image.jpeg").putFile(image)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                                savePostToFirestore(listener, reference, thumbnailUrl,
                                        task.getResult().getDownloadUrl().toString(), description);
                            } else {
                                String errorMessage;
                                if (task.getException() != null)
                                    errorMessage = task.getException().getLocalizedMessage();
                                else errorMessage = "Unknown error occur. Please try again!";
                                listener.onError(errorMessage);
                            }
                        }
                    });
        }

        /*
         * Helper method, which saves posts to the database
         *
         * @param listener OnPostSavedErrorListener called when error occurs
         * @param reference post's StorageReference
         * @param thumbnailUrl String of the post's thumbnail image download url
         * @param imageUrl String of the post's image download url
         * @param description String
         */
        private void savePostToFirestore(final OnPostSavedErrorListener listener,
                                         final StorageReference storageReference,
                                         String thumbnailUri, String imageUrl, String description) {
            DocumentReference reference = mFirebaseFirestore.collection("posts2").document();
            Post post = new Post(reference.getId(), mCurrentUser.getEmail(), new Date().getTime(),
                    mCurrentUser.getProfileImage(), imageUrl, thumbnailUri, description,
                    new ArrayList<Integer>());
            reference.set(post).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMessage = e.getLocalizedMessage();
                    if (errorMessage == null)
                        errorMessage = "Unknown error occur. Please try again!";
                    storageReference.delete();
                    listener.onError(errorMessage);
                }
            });
        }

        /*
         * Method, that returns Post object by given Id
         *
         * @param postId String, representing the id of the post
         * @return Post object
         */
        public Post getPostByCreatedAt(long createdAt, PostListener listener) {
            mListener = listener;
            unregisterPostListener();
            mPost = mPostsTree.ceilingEntry(createdAt);
            registerPostListener();
            registerReactionsListener();
            return mPost.getValue();
        }

        public Post getNextPost() {
            Map.Entry<Long, Post> post = mPostsTree.lowerEntry(mPost.getKey());
            if (post == null)
                return null;
            unregisterPostListener();
            mPost = post;
            registerPostListener();
            registerReactionsListener();
            return post.getValue();
        }

        public Post getPreviousPost() {
            Map.Entry<Long, Post> post = mPostsTree.higherEntry(mPost.getKey());
            if (post == null)
                return null;
            unregisterPostListener();
            mPost = post;
            registerPostListener();
            registerReactionsListener();
            return post.getValue();
        }

        public void react(final ReactionType type) {
            mFirebaseFirestore.collection("posts2")
                    .document(mPost.getValue().getPostId())
                    .collection("reactionsHistory")
                    .whereEqualTo("userEmail", mCurrentUser.getEmail())
                    .orderBy("time", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Reaction reaction;
                                if (task.getResult().getDocuments().size() == 0) {
                                    // user hasn't reacted
                                    reaction = new Reaction(mCurrentUser.getEmail(),
                                            type, true, new Date().getTime());
                                } else {
                                    reaction = new Reaction(mCurrentUser.getEmail(),
                                            type, false, new Date().getTime());
                                    Reaction reaction1 = task.getResult().getDocuments().get(0)
                                            .toObject(Reaction.class);
                                    if (reaction1.getType() == reaction.getType())
                                        return;
                                    mPost.getValue().removeReaction(reaction1.getType());
                                }
                                mPost.getValue().addReaction(type);
                                mFirebaseFirestore.collection("posts2").document(mPost.getValue()
                                        .getPostId())
                                        .update("reactions", mPost.getValue().getReactions());
                                mFirebaseFirestore.collection("posts2")
                                        .document(mPost.getValue().getPostId())
                                        .collection("reactionsHistory").add(reaction);

                                Log.d("reactions", "reacted " + reaction.getType());

                            } else if (task.getException() != null)
                                Log.d("reactions", "not successfull " + task.getException()
                                        .getLocalizedMessage());
                            else Log.d("reactions", "not successfull ");
                        }
                    });
        }

        private void registerReactionsListener() {
            if (mListener == null || mPost == null)
                return;
            mReactionsListener = mFirebaseFirestore.collection("posts2")
                    .document(mPost.getValue().getPostId())
                    .collection("reactionsHistory")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(5)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null)
                                mListener.onError(e.getLocalizedMessage());
                            for (DocumentChange documentChange :
                                    documentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    Reaction reaction = documentChange.getDocument()
                                            .toObject(Reaction.class);
                                    Log.d("reactions", "reaction added" + reaction.getType());
                                    mListener.onReactionAdded(reaction);
                                    if (oldestReactionLoaded == 0)
                                        oldestReactionLoaded = reaction.getTime();
                                }
                            }
                        }
                    });
        }

        private void registerPostListener() {
            mPostListener = mFirebaseFirestore.collection("posts2")
                    .document(mPost.getValue().getPostId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot,
                                            FirebaseFirestoreException e) {
                            if (e != null) {
                                mListener.onError(e.getLocalizedMessage());
                            }
                            if (documentSnapshot.exists()) {
                                Post post = documentSnapshot.toObject(Post.class);
                                Log.d("reactions", "reaction changed in post" + post.getReactions().toString());
                                mPostsTree.put(post.getCreatedAt(), post);
                                mListener.onPostChanged(post);
                            }
                        }
                    });
        }

        public void unregisterPostListener() {
            if (mPostListener != null)
                mPostListener.remove();
            mPost = null;
            if (mReactionsListener != null)
                mReactionsListener.remove();
            oldestReactionLoaded = 0;
        }

    }

    public class StudiosManager {
        private final Retrofit.Builder mRetrofitBuilder;
        private StudiosResponse mStudiosResponse;
        private LinkedHashMap<String, Studio> mStudios;

        private StudiosManager() {
            mStudios = new LinkedHashMap<>();

            mRetrofitBuilder = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/");
        }

        public void getNearbyStudios(final StudioInfoListener listener, Location location) {

            Gson gson = new GsonBuilder().registerTypeAdapterFactory
                    (new StudioTypeAdapterFactory())
                    .create();
            Retrofit retrofit = mRetrofitBuilder
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            RetrofitStudios service = retrofit.create(RetrofitStudios.class);

            Call<StudiosResponse> request;
            if (mStudios.size() == 0) {
                String locationString;
                if (location != null) {
                    locationString = location.getLatitude() + "," + location.getLongitude();
                    request = service.getNearbyStudios(locationString);
                } else {
                    locationString = "tattoos in " + mCurrentUser.getCity();
                    request = service.getStudiosByCity(locationString);

                }

                request.enqueue(new Callback<StudiosResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<StudiosResponse> call,
                                           @NonNull Response<StudiosResponse> response) {
                        mStudiosResponse = response.body();
                        if (mStudiosResponse == null)
                            return;
                        for (Studio studio : mStudiosResponse.getStudios()) {
                            mStudios.put(studio.getPlaceId(), studio);
                            getStudioInfo(listener, studio);
                        }
                        listener.onStudioInfoLoaded();
                        Log.d("query", response.toString());
                    }

                    @Override
                    public void onFailure(@NonNull Call<StudiosResponse> call,
                                          @NonNull Throwable t) {
                        Log.d("query", t.getLocalizedMessage());
                    }
                });
            } else listener.onStudioInfoLoaded();
        }

        private void getStudioInfo(final StudioInfoListener listener, final Studio studio) {
            Retrofit retrofit = mRetrofitBuilder.build();
            RetrofitStudios service = retrofit.create(RetrofitStudios.class);

            Call<ResponseBody> call = service.getStudioPhoto(studio.getPhotoReference());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            studio.setPhotoUrl(response.raw().request().url().toString());
                            listener.onStudioLoaded(studio);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.d("photo", t.getLocalizedMessage());
                }
            });
        }

        public List<Studio> getStudios() {
            return new ArrayList<>(mStudios.values());
        }

        public Studio getStudioById(String id) {
            return mStudios.get(id);
        }

        public void clearStudios() {
            mStudios.clear();
        }
    }

    public class GalleryManager {

        private GalleryManager() {
        }

        public User getCurrentUser() {
            return mCurrentUser;
        }

        public void saveImage(Uri uri, final GalleryImageAddListener listener) {
            mFirebaseStorage.child(mCurrentUser.getEmail() + "/pics/"
                    + new Date().getTime() + ".jpeg")
                    .putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Uri downloadUri = task.getResult().getDownloadUrl();
                            if (task.isSuccessful() && downloadUri != null) {
                                mCurrentUser.getGallery().add(downloadUri.toString());
                                mFirebaseFirestore.collection("users")
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
                            mFirebaseFirestore.collection("users")
                                    .document(mCurrentUser.getEmail())
                                    .update("gallery", mCurrentUser.getGallery());
                        }
                    });
        }
    }

    public class UsersManager {
        private HashMap<String, User> mExploreUsers;
        private HashMap<String, User> mFriends;

        private UsersManager() {
            mExploreUsers = new HashMap<>();
            mFriends = new HashMap<>();
        }

        public void getUsers(final UsersListener listener) {
            if (mCurrentUser == null)
                return;
            if (mExploreUsers != null && mExploreUsers.size() != 0) {
                listener.onUsersLoaded();
                return;
            }
            mFirebaseFirestore.collection("users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    User user = snapshot.toObject(User.class);
                                    if (mCurrentUser.getFriends().contains(user.getEmail())) {
                                        mFriends.put(user.getEmail(), user);
                                    } else if (!mCurrentUser.getEmail().equals(user.getEmail()) &&
                                            !mCurrentUser.getLiked().contains(user.getEmail())) {
                                        mExploreUsers.put(user.getEmail(), user);
                                    }
                                }
                                listener.onUsersLoaded();

                            } else {
                                String errorMessage;
                                if (task.getException() != null)
                                    errorMessage = task.getException().getLocalizedMessage();
                                else errorMessage = "Unknown Error";
                                listener.onError(errorMessage);
                            }

                        }
                    });
        }

        public void like(String userEmail) {
            User user = mExploreUsers.get(userEmail);
            mCurrentUser.getLiked().add(user.getEmail());
            mFirebaseFirestore.collection("users").document(mCurrentUser.getEmail())
                    .update("liked", mCurrentUser.getLiked());
            if (user.getLiked() != null
                    && user.getLiked().contains(mCurrentUser.getEmail())) {
                // make friends
                user.getFriends().add(mCurrentUser.getEmail());
                mFirebaseFirestore.collection("users").document(userEmail)
                        .update("friends", user.getFriends());
                mFriends.put(user.getEmail(), user);
                mCurrentUser.getFriends().add(user.getEmail());
                mFirebaseFirestore.collection("users").document(mCurrentUser.getEmail())
                        .update("friends", mCurrentUser.getFriends());
            }
            mExploreUsers.remove(userEmail);
        }

        public ArrayList<User> getFriends(UsersListener listener) {
            if (mFriends == null) {
                getUsers(listener);
                return null;
            }
            return new ArrayList<>(mFriends.values());
        }

        private User getFriend(String email) {
            return mFriends.get(email);
        }

        public ArrayList<User> getExploreUsers() {
            return new ArrayList<>(mExploreUsers.values());
        }
    }

    public class ChatManager {

        private HashMap<String, ChatRoom> mChatRooms;
        private ListenerRegistration mChatListener;
        private ChatRoom mChatRoom;
        private ListenerRegistration mChatRegistration;
        private long newestMessageTime;

        private ChatManager() {
            mChatRooms = new HashMap<>();
        }

        public void registerChatRoomsListener(final ChatRoomListener listener) {
            mChatListener = mFirebaseFirestore
                    .collection("users")
                    .document(mCurrentUser.getEmail())
                    .collection("chatRooms")
                    .orderBy("seen")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null) {
                                listener.onError(e.getLocalizedMessage());
                            }
                            for (DocumentChange documentChange :
                                    documentSnapshots.getDocumentChanges()) {
                                ChatRoom chatRoom = documentChange.getDocument()
                                        .toObject(ChatRoom.class);
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    mChatRooms.put(chatRoom.getEmail(), chatRoom);
                                    listener.onChatRoomAdded(chatRoom);
                                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    mChatRooms.get(chatRoom.getEmail()).setInfo(chatRoom);
                                    listener.onChatRoomModified(chatRoom);
                                }
                            }
                        }
                    });
        }

        public void getChatRoom(ChatRoomListener listener, String email) {
            mChatRoom = mChatRooms.get(email);
            if (mChatRoom == null) {
                createChatRoom(listener, email);
            } else {
                if (mChatRoom.getLastMessageSender().equals(email)) {
                    mChatRoom.makeSeen();

                    mFirebaseFirestore.collection("users").document(mCurrentUser.getEmail())
                            .collection("chatRooms").document(mChatRoom.getChatId())
                            .update("seen", true);

                    mFirebaseFirestore.collection("users").document(mChatRoom.getEmail())
                            .collection("chatRooms").document(mChatRoom.getChatId())
                            .update("seen", true);
                }
                listener.onChatRoomLoaded();
            }
        }

        private void createChatRoom(ChatRoomListener listener, String email) {

            DocumentReference chatRoomReference = mFirebaseFirestore.collection("chatRooms")
                    .document();

            long time = new Date().getTime();

            Message message = new Message(mCurrentUser.getEmail(), "Chat room created!",
                    time, false);

            chatRoomReference.collection("messages").document().set(message);

            User friend = mUsersManager.getFriend(email);
            ChatRoom chatRoom = new ChatRoom(chatRoomReference.getId(), email,
                    friend.getProfileImage(),
                    friend.getName(), "Chat room created!",
                    time, mCurrentUser.getEmail(), true);
            mFirebaseFirestore
                    .collection("users")
                    .document(mCurrentUser.getEmail())
                    .collection("chatRooms")
                    .document(chatRoomReference.getId())
                    .set(chatRoom);

            ChatRoom chatRoom1 = new ChatRoom(chatRoomReference.getId(), mCurrentUser.getEmail(),
                    mCurrentUser.getProfileImage(),
                    mCurrentUser.getName(), "Chat room created!",
                    time, mCurrentUser.getEmail(), false);

            mFirebaseFirestore
                    .collection("users")
                    .document(email)
                    .collection("chatRooms")
                    .document(chatRoomReference.getId())
                    .set(chatRoom1);

            mChatRoom = chatRoom;
            listener.onChatRoomLoaded();
        }

        public void unregisterChatRoomsListener() {
            if (mChatListener != null)
                mChatListener.remove();
        }

        public void registerChatRoomListener(final ChatListener listener) {
            mChatRegistration = mFirebaseFirestore
                    .collection("chatRooms")
                    .document(mChatRoom.getChatId())
                    .collection("messages")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .whereGreaterThan("time", newestMessageTime)
                    .limit(5)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            boolean isNew = false;
                            for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    Message message1 = change.getDocument().toObject(Message.class);
                                    if (newestMessageTime < message1.getTime()) {
                                        if (newestMessageTime != 0)
                                            isNew = true;
                                        newestMessageTime = message1.getTime();
                                    }

                                    if (!message1.isSeen()) {
                                        message1.setSeen(true);
                                        change.getDocument().getReference().update("seen", true);
                                    }
                                    listener.onMessageAdded(message1, isNew);
                                }
                            }
                        }
                    });
        }

        public void unregisterChatRoomListener() {
            if (mChatRegistration != null)
                mChatRegistration.remove();
            newestMessageTime = 0;
        }

        public ChatRoom getChatRoom() {
            return mChatRoom;
        }

        public User getCurrentUser() {
            return mCurrentUser;
        }

        public LinkedList<ChatRoom> getChatRooms() {
            return new LinkedList<>(mChatRooms.values());
        }


        public void addMessage(Message message, String chatId, String email) {
            mFirebaseFirestore.collection("chatRooms").document(chatId)
                    .collection("messages").add(message);
            mFirebaseFirestore.collection("users").document(mCurrentUser.getEmail())
                    .collection("chatRooms").document(chatId)
                    .update("lastMessage", message.getMessage(),
                            "lastMessageSender", message.getUserEmail(),
                            "lastMessageTime", message.getTime(),
                            "seen", true);
            mFirebaseFirestore.collection("users").document(email)
                    .collection("chatRooms").document(chatId)
                    .update("lastMessage", message.getMessage(),
                            "lastMessageSender", message.getUserEmail(),
                            "lastMessageTime", message.getTime(),
                            "seen", false);
        }
    }
}
