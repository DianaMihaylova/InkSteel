package com.ink_steel.inksteel.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CurrentUser {

    private static CurrentUser currentUser = null;
    private String userEmail;
    private String userName;
    private String userAge;
    private String userCity;
    private String userProfilePicture;

    private CurrentUser(String email) {
        FirebaseFirestore.getInstance().collection("users").document(email)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot,
                                        FirebaseFirestoreException e) {
                        userAge = documentSnapshot.getString("userAge");
                        userName = documentSnapshot.getString("userName");
                        userCity = documentSnapshot.getString("userCity");
                        userProfilePicture = documentSnapshot.getString("userProfileImage");
                    }
                });
    }

    public static CurrentUser getInstance() {
        if (currentUser == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                currentUser = new CurrentUser(user.getEmail());
            }
        }
        return currentUser;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }
}
