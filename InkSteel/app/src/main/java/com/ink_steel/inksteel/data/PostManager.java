package com.ink_steel.inksteel.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.model.Post;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

public class PostManager {

    private static PostManager mPostManager;
    private static FirebaseManager2 mManager;
    private String thumbnailDownloadUrl;
    private String imageDownloadUrl;

    private PostManager() {
        mManager = FirebaseManager2.getInstance();
    }

    public static PostManager getInstance() {
        if (mPostManager == null)
            mPostManager = new PostManager();
        return mPostManager;
    }

    public void savePost(final PostManagerListener listener, Bitmap thumbnail, final Uri image,
                         final String description) {
        final StorageReference reference = mManager.mStorage.child("posts/" + UUID.randomUUID());
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

    private void savePostToFirestore(final PostManagerListener listener, String description) {
        DocumentReference reference = mManager.mFirestore.collection("posts").document();
        Post post = new Post(reference.getId(), mManager.mCurrentUser.getEmail(),
                new Date().getTime(), mManager.mCurrentUser.getProfileImage(), imageDownloadUrl,
                thumbnailDownloadUrl, description);
        reference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    listener.onPostSaved();
            }
        });
    }

    public interface PostManagerListener {
        void onPostSaved();
    }

}
