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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.model.User;

import java.io.ByteArrayOutputStream;

public class UserManager {

    private static UserManager mUserManager;
    private static FirebaseManager2 mManager;
    private FirebaseAuth mAuth;

    private UserManager() {
        mAuth = FirebaseAuth.getInstance();
        mManager = FirebaseManager2.getInstance();
    }

    public static UserManager getInstance() {
        if (mUserManager == null)
            mUserManager = new UserManager();
        return mUserManager;
    }

    public void checkIfSignedIn(UserManagerListener listener) {
        if (mAuth.getCurrentUser() != null) {
            loadUserInfo(listener, mAuth.getCurrentUser().getEmail());
        }
    }

    private void loadUserInfo(final UserManagerListener listener, final String email) {
        final DocumentReference userReference = mManager.mFirestore.collection("users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists())
                        mManager.mCurrentUser = snapshot.toObject(User.class);
                    else {
                        mManager.mCurrentUser = new User(email, "", "", "", "");
                        userReference.set(mManager.mCurrentUser);
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
        mManager.mStorage.child(mManager.mCurrentUser.getEmail() + "/profile.jpeg").putBytes(baos.toByteArray())
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Uri downloadUri = task.getResult().getDownloadUrl();
                        if (task.isSuccessful() && downloadUri != null) {
                            mManager.mCurrentUser.setProfileImage(downloadUri.toString());
                            mManager.mFirestore.collection("users")
                                    .document(mManager.mCurrentUser.getEmail())
                                    .update("name", mManager.mCurrentUser.getName(),
                                            "age", mManager.mCurrentUser.getAge(),
                                            "city", mManager.mCurrentUser.getCity(),
                                            "profileImage", mManager.mCurrentUser.getProfileImage())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            listener.onUserInfoSaved();
                                            updatePostsFromUser();
                                        }

                                    });
                        }
                    }
                });
    }

    private void updatePostsFromUser() {
        mManager.mFirestore.collection("posts")
                .whereEqualTo("userEmail", mManager.mCurrentUser.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot snapshot : documentSnapshots) {
                    snapshot.getReference()
                            .update("urlProfileImage", mManager.mCurrentUser.getProfileImage());
                }
            }
        });
    }

    public User getCurrentUser() {
        return mManager.mCurrentUser;
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
        void onUserInfoSaved();
    }

}
