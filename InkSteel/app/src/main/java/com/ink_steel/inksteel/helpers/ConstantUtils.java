package com.ink_steel.inksteel.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConstantUtils {

    public static final int LOGIN_BUTTON = 1;
    public static final int REGISTER_BUTTON = 2;
    public static final String LOGIN_EMAIL = "login_email";

    public static String EMAIL = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public static final String USER_NAME = "userName";
    public static final String USER_CITY = "userCity";
    public static final String USER_AGE = "userAge";
    public static final String USER_PROFILE_IMG = "userProfileImage";
    private static FirebaseStorage FIREBASE_STORAGE = FirebaseStorage.getInstance();
    public static StorageReference FIREBASE_STORAGE_REFERENCE = FIREBASE_STORAGE.getReference();
    private static final FirebaseFirestore FIRESTORE_REFERENCE = FirebaseFirestore.getInstance();
    public static final DocumentReference FIREBASE_USER_DOCUMENT_REFERENCE = FIRESTORE_REFERENCE
            .collection("users").document(EMAIL);
    public static final CollectionReference FIRESTORE_GALLERY_REFERNENCE = FirebaseFirestore.getInstance()
            .collection("users").document(ConstantUtils.EMAIL).collection("gallery");

}
