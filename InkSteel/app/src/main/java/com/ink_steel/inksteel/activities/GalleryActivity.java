package com.ink_steel.inksteel.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.GalleryRecyclerViewAdapter;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.helpers.IOnGalleryImageLongClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity implements IOnGalleryImageLongClickListener {

    private static final int PICK_IMAGE = 1;

    public static ArrayList<Uri> images;

    private Uri mImgDownload;
    private GalleryRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        images = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_fab);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new GalleryRecyclerViewAdapter(GalleryActivity.this, images, this);
        mRecyclerView.setAdapter(mAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImagesArray();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadImagesArray();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            mImgDownload = data.getData();
            uploadImageToStorage();
        }
    }

    private void uploadImageToStorage() {
        StorageReference spaceRef = ConstantUtils.FIREBASE_STORAGE_REFERENCE.child(ConstantUtils.EMAIL
                + "/pics/" + new Date() + ".jpg");
        UploadTask uploadTask = spaceRef.putFile(mImgDownload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveImageToDatabase(taskSnapshot);
                Toast.makeText(GalleryActivity.this, "Ready!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToDatabase(UploadTask.TaskSnapshot taskSnapshot) {
        Map<String, Object> galleryData = new HashMap<>();
        mImgDownload = taskSnapshot.getDownloadUrl();
        String userPictureUrl = mImgDownload.toString();
        galleryData.put("picture", userPictureUrl);
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE.add(galleryData);
        images.add(Uri.parse(userPictureUrl));
        loadImagesArray();
    }

    public void loadImagesArray() {
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

    @Override
    public void onGalleryImageLongClickListener(int position, boolean isLongClick) {
        if (isLongClick) {
            equalPosition(position);
        } else {
            Intent i = new Intent(GalleryActivity.this, FullScreenImageActivity.class);
            i.putExtra("image", position);
            startActivity(i);
        }

    }

    private void equalPosition(int pos) {
        final int position = pos;
        ConstantUtils.FIRESTORE_GALLERY_REFERNENCE.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (("{picture=" + String.valueOf(images.get(position)) + "}").equals
                                        (document.getData().toString())) {
                                    final DocumentReference docRef = ConstantUtils.FIRESTORE_GALLERY_REFERNENCE
                                            .document(document.getId());
                                    setAlert(docRef);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void setAlert(DocumentReference doc) {
        final DocumentReference docRef = doc;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
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
                                        Toast.makeText(GalleryActivity.this, "Image successfully " +
                                                "deleted!", Toast.LENGTH_SHORT).show();
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
