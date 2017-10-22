package com.ink_steel.inksteel.helpers;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConstantUtils {

    public static final int LOGIN_BUTTON = 1;
    public static final int REGISTER_BUTTON = 2;
    public static final String LOGIN_EMAIL = "login_email";
    public static final String USER_NAME = "userName";
    public static final String USER_CITY = "userCity";
    public static final String USER_AGE = "userAge";
    public static final String USER_PROFILE_IMG = "userProfileImage";
    private static final FirebaseFirestore FIRESTORE_REFERENCE = FirebaseFirestore.getInstance();
    public static String EMAIL = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public static final DocumentReference FIREBASE_USER_DOCUMENT_REFERENCE = FIRESTORE_REFERENCE
            .collection("users").document(EMAIL);
    public static Uri PROFILE_IMAGE_URI;
    private static FirebaseStorage FIREBASE_STORAGE = FirebaseStorage.getInstance();
    public static StorageReference FIREBASE_STORAGE_REFERENCE = FIREBASE_STORAGE.getReference();

    static {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(ConstantUtils.EMAIL)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        PROFILE_IMAGE_URI = Uri.parse(documentSnapshot.getString(USER_PROFILE_IMG));
                    }
                });
    }
}
