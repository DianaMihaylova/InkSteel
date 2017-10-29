package com.ink_steel.inksteel.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.adapters.GalleryRecyclerViewAdapter;
import com.ink_steel.inksteel.helpers.ConstantUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentUser {

    private static CurrentUser currentUser;
    private String userEmail;
    private String userName;
    private String userAge;
    private String userCity;
    private String userProfilePicture;
    private ArrayList<Uri> images;
    private ArrayList<User> likes;

    private CurrentUser(String email) {
        DocumentReference userInfo = FirebaseFirestore.getInstance().collection("users").document(email);
        addInfoUserData(userInfo);
        addUserImages();
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

    public List<Uri> getImages() {
        return Collections.unmodifiableList(images);
    }

    private void addInfoUserData(DocumentReference userInfo) {
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                userEmail = ConstantUtils.USER_EMAIL;
                userName = documentSnapshot.getString(ConstantUtils.USER_NAME);
                userAge = documentSnapshot.getString(ConstantUtils.USER_AGE);
                userCity = documentSnapshot.getString(ConstantUtils.USER_CITY);
                userProfilePicture = documentSnapshot.getString(ConstantUtils.USER_PROFILE_IMG);
            }
        });
    }

    private void addUserImages() {
        images = new ArrayList<>();
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                images.add(Uri.parse(document.getString("picture")));
                            }
                        }
                    }
                });
    }

    public void refreshUserImages(final GalleryRecyclerViewAdapter mAdapter) {
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            images.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                images.add(Uri.parse(document.getString("picture")));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
