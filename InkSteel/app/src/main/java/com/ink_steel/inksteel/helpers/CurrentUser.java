package com.ink_steel.inksteel.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CurrentUser {

    private static final String DEFAULT_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/inksteel-" +
            "7911e.appspot.com/o/default.jpg?alt=media&token=2a0f4edc-81e5-40a2-9558-015e18b8b1ff";
    private static CurrentUser currentUser = null;
    private String userEmail;
    private String userName;
    private String userAge;
    private String userCity;
    private String userProfilePicture;

    private CurrentUser() {
        this.userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        addInfoUserData();
    }

    public static CurrentUser getInstance() {
        if (currentUser == null) {
            currentUser = new CurrentUser();
        }
        return currentUser;
    }


    public static CurrentUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(CurrentUser currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserProfilePicture() {
        return userProfilePicture == null ? DEFAULT_IMG_URL : userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    private void addInfoUserData() {
        DocumentReference userInfo = FirebaseFirestore.getInstance().collection("users").
                document(ConstantUtils.EMAIL);
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                userName = documentSnapshot.getString(ConstantUtils.USER_NAME);
                userAge = documentSnapshot.getString(ConstantUtils.USER_AGE);
                userCity = documentSnapshot.getString(ConstantUtils.USER_CITY);
                userProfilePicture = documentSnapshot.getString(ConstantUtils.USER_PROFILE_IMG);
            }
        });
    }
}