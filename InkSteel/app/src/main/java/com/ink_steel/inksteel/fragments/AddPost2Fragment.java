package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.ImageDownloader;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPost2Fragment extends Fragment {

    private DocumentReference mPostReference;

    public AddPost2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post_2, container, false);

        final CropImageView cropImageView = (CropImageView)
                view.findViewById(R.id.add_post_crop_image_view);

        final Button cropButton = (Button) view.findViewById(R.id.add_post_crop_btn);

        final String mId = getArguments().getString("id");

        mPostReference = FirebaseFirestore
                .getInstance()
                .collection("posts")
                .document(mId);
//        mPostReference
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot snapshot = task.getResult();
//                    String imageUrl = snapshot.getString("postImage");
//                    Toast.makeText(getActivity(), imageUrl, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


//        Picasso.with(getActivity().getApplicationContext())
//                .load(AddPost1Fragment.largeBitmap)
//                .into(new ImageDownloader("postPic", cropImageView));

        cropImageView.setImageBitmap(AddPost1Fragment.largeBitmap);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        cropImageView.setAspectRatio(4, 3);
        cropImageView.setScaleType(CropImageView.ScaleType.CENTER_INSIDE);

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap thumbnail = cropImageView.getCroppedImage();

                SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy", Locale.UK);
                final String date = format.format(new Date());

                StorageReference reference = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child("posts/" + date + "/" + mId + "/thumbnail.jpeg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                reference
                        .putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mPostReference.update("postImageThumbnail",
                                        taskSnapshot.getDownloadUrl().toString());
                            }
                        });
            }
        });

        return view;
    }
}
