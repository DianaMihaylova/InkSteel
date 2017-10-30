package com.ink_steel.inksteel.data;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ink_steel.inksteel.model.User;

public class FirebaseManager2 {

    protected FirebaseFirestore mFirestore;
    protected User mCurrentUser;
    protected StorageReference mStorage;

    protected FirebaseManager2() {
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

}
