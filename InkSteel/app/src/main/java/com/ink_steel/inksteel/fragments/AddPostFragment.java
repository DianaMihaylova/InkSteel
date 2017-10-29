package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.model.Post;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment {

    public static final int REQUEST_CODE = 1;
    private CropImageView mCropImageView;
    private Button mAddImageButton;
    private EditText mDescriptionEditText;

    private Bitmap imageBitmap;
    private Bitmap thumbnailBitmap;
    private StorageReference mReference;

    public AddPostFragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                imageBitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), uri);
                mCropImageView.setImageBitmap(imageBitmap);
                mCropImageView.setAspectRatio(4, 3);


                mAddImageButton.setVisibility(View.GONE);
                mDescriptionEditText.setVisibility(View.VISIBLE);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        mCropImageView = view.findViewById(R.id.crop_iv);
        mAddImageButton = view.findViewById(R.id.add_image_b);
        Button cancelButton = view.findViewById(R.id.cancel_b);
        Button saveButton = view.findViewById(R.id.save_b);
        mDescriptionEditText = view.findViewById(R.id.description_et);

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        REQUEST_CODE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbnailBitmap = mCropImageView.getCroppedImage();
                saveImages();
                ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbnailBitmap = null;
                imageBitmap = null;
                ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
            }
        });

        return view;
    }

    private void saveImages() {
        mReference = FirebaseStorage.getInstance().getReference()
                .child("posts/" + UUID.randomUUID());

        byte[] data = getBitmapBytes(thumbnailBitmap);
        mReference.child("thumbnail.jpeg").putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        byte[] data2 = getBitmapBytes(imageBitmap);
                        if (taskSnapshot.getDownloadUrl() != null) {
                            mReference.child("image.jpeg").putBytes(data2)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot2) {
                                            if (taskSnapshot2.getDownloadUrl() != null) {
                                                savePost(taskSnapshot.getDownloadUrl().toString(),
                                                        taskSnapshot2.getDownloadUrl().toString(),
                                                        mDescriptionEditText.getText().toString());
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void savePost(String thumbnailDownloadUrl, String imageDownloadUrl, String description) {
        DocumentReference reference =
                FirebaseFirestore.getInstance().collection("posts").document();

        Post post = new Post(reference.getId(), HomeActivity.userEmail, new Date().getTime(),
                HomeActivity.userImageUrl, imageDownloadUrl, thumbnailDownloadUrl, description);

        reference.set(post);

    }

    private byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
