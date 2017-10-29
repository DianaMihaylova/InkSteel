package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.GalleryRecyclerViewAdapter;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.helpers.IOnGalleryImageLongClickListener;
import com.ink_steel.inksteel.model.CurrentUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment implements IOnGalleryImageLongClickListener {

    private static final int CHOOSE_IMAGE = 1;
    private CurrentUser mCurrentUser;
    private Uri mImgUrl;
    private GalleryRecyclerViewAdapter mAdapter;
    private boolean isAddOrRemovePicture;

    public GalleryFragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            mImgUrl = data.getData();
            uploadImageToStorage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mCurrentUser = CurrentUser.getInstance();
        isAddOrRemovePicture = false;

        FloatingActionButton fab = view.findViewById(R.id.btn_fab);

        RecyclerView mRecyclerView = view.findViewById(R.id.image_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new GalleryRecyclerViewAdapter(getActivity().getApplicationContext(), mCurrentUser.getImages(), this);
        mRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return view;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE);
    }

    private void uploadImageToStorage() {
        StorageReference spaceRef = ConstantUtils.FIREBASE_STORAGE_REFERENCE
                .child(ConstantUtils.USER_EMAIL + "/pics/" + new Date() + ".jpg");
        UploadTask uploadTask = spaceRef.putFile(mImgUrl);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveImageToDatabase(taskSnapshot);
                Toast.makeText(getActivity().getApplicationContext(), "Ready!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToDatabase(UploadTask.TaskSnapshot taskSnapshot) {
        Map<String, Object> galleryData = new HashMap<>();
        mImgUrl = taskSnapshot.getDownloadUrl();
        String userPictureUrl = mImgUrl.toString();
        galleryData.put("picture", userPictureUrl);
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE.add(galleryData);
        isAddOrRemovePicture = true;
        loadImagesArray();
    }

    public void loadImagesArray() {
        if (isAddOrRemovePicture) {
            mCurrentUser.refreshUserImages(mAdapter);
            mCurrentUser.getImages();
            isAddOrRemovePicture = false;
        } else {
            mCurrentUser.getImages();
        }
    }

    @Override
    public void onGalleryImageLongClickListener(int position, boolean isLongClick) {
        if (isLongClick) {
            equalPosition(position);
        } else {
            FullScreenImageFragment fragment = new FullScreenImageFragment();
            Bundle bundle = new Bundle(1);
            bundle.putInt("image", position);
            fragment.setArguments(bundle);
            ((HomeActivity) getActivity()).replaceFragment(fragment);
        }
    }

    private void equalPosition(final int position) {
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (("{picture=" + String.valueOf(mCurrentUser.getImages().get(position)) + "}")
                                        .equals(document.getData().toString())) {
                                    final DocumentReference docRef = ConstantUtils
                                            .FIRESTORE_GALLERY_REFERNENCE.document(document.getId());
                                    setAlert(docRef);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void setAlert(final DocumentReference docRef) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Do you want to delete the image?")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        docRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Image successfully " +
                                                "deleted!", Toast.LENGTH_SHORT).show();
                                        isAddOrRemovePicture = true;
                                        loadImagesArray();
                                    }
                                });
                    }
                });
        AlertDialog ad = alertBuilder.create();
        ad.setTitle("WARNING MESSAGE");
        ad.setIcon(R.drawable.warning_msg);
        ad.show();
    }
}