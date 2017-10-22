package com.ink_steel.inksteel.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class AddPostActivity extends AppCompatActivity {

    public static final String DOCUMENT_POST_DATE = String.valueOf(new Date());
    private EditText imageUrl;
    private CropImageView mCropImageView;
    private Uri mImageUri;

    public AddPostActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imageUrl = (EditText) findViewById(R.id.post_image_url_et);
        Button imageUrlButton = (Button) findViewById(R.id.post_download_image_btn);
        Button imageFromGalleryBtn = (Button) findViewById(R.id.post_add_from_gallery_btn);
        mCropImageView = (CropImageView) findViewById(R.id.post_crop_iv);
        Button postImageBtn = (Button) findViewById(R.id.post_post_btn);

        imageUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageUrl.getText().toString().equals("")) {
                    displayCropImage();
                } else
                    imageUrl.setError("Invalid URL!");
            }
        });

        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] data = getCroppedImage();
                saveImageToStorage(data);

                Intent intent = new Intent(AddPostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        imageFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(AddPostActivity.this);
            }
        });
    }

    private void displayCropImage() {
        Picasso.with(AddPostActivity.this)
                .load(imageUrl.getText().toString())
                .into(new ImageDownloader("postPic", mCropImageView));
        mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        mCropImageView.setAspectRatio(4, 3);
        mCropImageView.setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
    }

    private void saveImageToStorage(byte[] data) {
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(new Date());
        final StorageReference spaceRef = ConstantUtils.FIREBASE_STORAGE_REFERENCE
                .child("posts/" + date + "/" + new Date() + ".jpeg");
        UploadTask uploadTask = spaceRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                savePostToDatabase(taskSnapshot, date);
            }
        });
    }

    private void savePostToDatabase(UploadTask.TaskSnapshot taskSnapshot, String date) {
        DocumentReference postReference = FirebaseFirestore.getInstance()
                .collection("posts")
                .document(date)
                .collection("posts")
                .document(DOCUMENT_POST_DATE);

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date());
        data.put("user", ConstantUtils.EMAIL);
        data.put("userProfileImage", ConstantUtils.PROFILE_IMAGE_URI.toString());
        if (taskSnapshot.getDownloadUrl() != null)
            data.put("postPic", taskSnapshot.getDownloadUrl().toString());

        postReference.set(data);
    }

    private byte[] getCroppedImage() {
        Bitmap bitmap = mCropImageView.getCroppedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == RESULT_OK) {

            mImageUri = CropImage.getPickImageResultUri(this, data);

            if ( Build.VERSION.SDK_INT >= 23
                    && CropImage.isReadExternalStoragePermissionsRequired(this, mImageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                displayImageFromGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if(mImageUri != null && grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayCropImage();
            }
        } else {
            Toast.makeText(this, "Need permission to get the image from gallery",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayImageFromGallery() {
        Picasso.with(AddPostActivity.this)
                .load(mImageUri)
                .into(new ImageDownloader("postPic", mCropImageView));
        mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        mCropImageView.setAspectRatio(4, 3);
        mCropImageView.setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
    }
}
