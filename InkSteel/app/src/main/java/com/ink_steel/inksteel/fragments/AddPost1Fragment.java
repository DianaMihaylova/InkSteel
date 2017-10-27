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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.ConstantUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddPost1Fragment extends Fragment {

    public static Bitmap largeBitmap;

    private ImageView mImageView;
    private Button mAddImage;
    private EditText mEditText;
    private String mId;
    private boolean isInitialUpload;

    public AddPost1Fragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                        uri);
                largeBitmap = bitmap;
                mImageView.setImageBitmap(bitmap);
                mAddImage.setVisibility(View.GONE);
                mEditText.setVisibility(View.VISIBLE);
                saveImage(bitmap);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post_1, container, false);

        mId = getArguments().getString("id");

        mAddImage = (Button) view.findViewById(R.id.add_post_add_image_btn);
        mImageView = (ImageView) view.findViewById(R.id.add_post_image);
        mEditText = (EditText) view.findViewById(R.id.add_post_image_caption_et);

        isInitialUpload = true;

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mAddImage.setOnClickListener(new View.OnClickListener() {
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                1);
    }

    private void saveImage(Bitmap bitmap) {
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy", Locale.UK);
        final String date = format.format(new Date());

        StorageReference reference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("posts/" + date + "/" + mId + "/image.jpeg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        reference
                .putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DocumentReference postReference = FirebaseFirestore
                                .getInstance()
                                .collection("posts")
                                .document(mId);
                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        if (isInitialUpload) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("user", currEmail);
                            data.put("userProfileImage", ConstantUtils.PROFILE_IMAGE_URI.toString());
                            data.put("postImage", downloadUrl);
                            data.put("postImageThumbnail", "");
                            data.put("time", new Date().getTime());
                            data.put("postId", postReference.getId());
                            data.put("like", 0);
                            data.put("blush", 0);
                            data.put("devil", 0);
                            data.put("dazed", 0);

                            postReference
                                    .set(data);

                            isInitialUpload = false;
                        } else {
                            postReference.update("postImage", downloadUrl);
                        }
                    }
                });

    }
}
