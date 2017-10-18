package com.ink_steel.inksteel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.ink_steel.inksteel.ConstantUtils.EMAIL;
import static com.ink_steel.inksteel.ConstantUtils.FIREBASE_STORAGE_REFERENCE;
import static com.ink_steel.inksteel.ConstantUtils.FIREBASE_USER_DOCUMENT_REFERENCE;
import static com.ink_steel.inksteel.ConstantUtils.FIRESTORE_FRIENDS_REFERNENCE;
import static com.ink_steel.inksteel.ConstantUtils.FIRESTORE_REFERENCE;
import static com.ink_steel.inksteel.ConstantUtils.USER_AGE;
import static com.ink_steel.inksteel.ConstantUtils.USER_CITY;
import static com.ink_steel.inksteel.ConstantUtils.USER_NAME;
import static com.ink_steel.inksteel.ConstantUtils.USER_PROFILE_IMG;

;

public class UserInfoActivity extends AppCompatActivity {


    private static final String DEFAULT_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/inksteel-7911e.appspot.com/o/default.jpg?alt=media&token=2a0f4edc-81e5-40a2-9558-015e18b8b1ff";

    private EditText userName, age, city;
    private ImageView imageView;
    private Uri mImgDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userName = (EditText) findViewById(R.id.user_name);
        age = (EditText) findViewById(R.id.user_age);
        city = (EditText) findViewById(R.id.user_city);
        imageView = (ImageView) findViewById(R.id.profile_picture);
        Button cancelBtn = (Button) findViewById(R.id.button_cancel);
        Button saveBtn = (Button) findViewById(R.id.button_save);

        mImgDownload = Uri.parse(DEFAULT_IMG_URL);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String userCity = city.getText().toString();
                String userAge = age.getText().toString();
                String userProfilePictureUrl = mImgDownload.toString();

                Map<String, Object> data = new HashMap<>();
                data.put(USER_NAME, username);
                data.put(USER_CITY, userCity);
                data.put(USER_AGE, userAge);
                data.put(USER_PROFILE_IMG, userProfilePictureUrl);

                FIREBASE_USER_DOCUMENT_REFERENCE.set(data);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserInfoActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setFixAspectRatio(true)
                        .start(UserInfoActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FIREBASE_USER_DOCUMENT_REFERENCE.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    userName.setText(documentSnapshot.getString(USER_NAME));
                    city.setText(documentSnapshot.getString(USER_CITY));
                    age.setText(documentSnapshot.getString(USER_AGE));

                    if (documentSnapshot.contains(USER_PROFILE_IMG)) {
                        mImgDownload = Uri.parse(documentSnapshot.getString(USER_PROFILE_IMG));
                        loadImage();
                    }
                }
            }
        });
    }

    private void loadImage() {
        Picasso.with(UserInfoActivity.this)
                .load(mImgDownload)
                .transform(new CropCircleTransformation())
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            mImgDownload = CropImage.getActivityResult(data).getUri();
            uploadImage();
        }
    }

    private void uploadImage() {
        StorageReference spaceRef = FIREBASE_STORAGE_REFERENCE.child(EMAIL + "/profile.jpg");
        UploadTask uploadTask = spaceRef.putFile(mImgDownload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserInfoActivity.this, "Ready!", Toast.LENGTH_SHORT).show();
                mImgDownload = taskSnapshot.getDownloadUrl();
                loadImage();
            }
        });
    }
}
