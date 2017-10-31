package com.ink_steel.inksteel.data;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ink_steel.inksteel.model.User;

public class FirebaseManager2 {

    FirebaseFirestore mFirestore;
    User mCurrentUser;
    StorageReference mStorage;
    private static FirebaseManager2 mManager;

    private FirebaseManager2() {
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    static FirebaseManager2 getInstance() {
        if (mManager == null)
            mManager = new FirebaseManager2();
        return mManager;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

}
