package com.ink_steel.inksteel.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.helpers.ImageDownloader;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.ink_steel.inksteel.helpers.ConstantUtils.USER_PROFILE_IMG;

public class AddPostActivity extends AppCompatActivity {

    public static final String DOCUMENT_POST_DATE = String.valueOf(new Date());
    public static final int PERMISSION_REQUEST_CODE = 77;

    private CropImageView mCropImageView;
    private View rootView;
    private Button mImageFromGalleryBtn;
    private Uri mImageUri;
    private Button mPostImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        final EditText imageUrl = (EditText) findViewById(R.id.post_image_url_et);
        Button imageUrlButton = (Button) findViewById(R.id.post_download_image_btn);
        mImageFromGalleryBtn = (Button) findViewById(R.id.post_add_from_gallery_btn);
        mCropImageView = (CropImageView) findViewById(R.id.post_crop_iv);
        mPostImageBtn = (Button) findViewById(R.id.post_post_btn);
        rootView = findViewById(R.id.add_post_root);

        imageUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUri = Uri.parse(imageUrl.getText().toString());
                if (!mImageUri.toString().isEmpty()) {
                    displayImage();
                } else {
                    imageUrl.setError("Invalid URL!");
                }
            }
        });

        mPostImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] data = getCroppedImage();
                saveImageToStorage(data);

                Intent intent = new Intent(AddPostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        mImageFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(AddPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    CropImage.startPickImageActivity(AddPostActivity.this);
                    Toast.makeText(AddPostActivity.this, "Choosing Image",
                            Toast.LENGTH_SHORT).show();
                    Log.d("ImageChooser", "choosing image when permission is granted");
                }
            }
        });
    }

    private void saveImageToStorage(byte[] data) {
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(new Date());
        final StorageReference spaceRef = ConstantUtils.FIREBASE_STORAGE_REFERENCE
                .child("posts/" + date + "/" + new Date() + ".jpeg");
        spaceRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        savePostToDatabase(taskSnapshot, date);
                    }
                });
    }

    private void savePostToDatabase(final UploadTask.TaskSnapshot taskSnapshot, String date) {
        if (taskSnapshot.getDownloadUrl() != null) {
            DocumentReference postReference = FirebaseFirestore.getInstance()
                    .collection("posts")
                    .document(DOCUMENT_POST_DATE);

            final Map<String, Object> data = new HashMap<>();

//            FirebaseFirestore.getInstance()
//                    .collection("users")
//                    .document(ConstantUtils.EMAIL)
//                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(DocumentSnapshot documentSnapshot,
//                                            FirebaseFirestoreException e) {
//                            if (documentSnapshot.exists()) {
                                data.put("time", new Date());
                                data.put("user", ConstantUtils.EMAIL);
                                data.put("postPic", taskSnapshot.getDownloadUrl().toString());
            data.put("userProfileImage", ConstantUtils.PROFILE_IMAGE_URI.toString());
//                            }
//                        }
//                    });
            postReference.set(data);
        }
    }

    private byte[] getCroppedImage() {
        Bitmap bitmap = mCropImageView.getCroppedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(AddPostActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void displayImage() {
        Log.d("ImageChooser", mImageUri.toString());
        Picasso.with(AddPostActivity.this)
                .load(mImageUri)
                .into(new ImageDownloader("postPic", mCropImageView));
        mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        mCropImageView.setAspectRatio(4, 3);
        mCropImageView.setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
        mPostImageBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == RESULT_OK) {
            Log.d("ImageChooser", "Image chosen: " + CropImage.getPickImageResultUri(this, data));
            mImageUri = CropImage.getPickImageResultUri(this, data);
            displayImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(AddPostActivity.this);
            } else {
                final Snackbar permissionSnackbar = Snackbar.make(rootView,
                        "Permission not granted. Can't choose image from gallery",
                        Snackbar.LENGTH_LONG);
                permissionSnackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestStoragePermission();
                    }
                }).show();
            }
        }
    }


}
