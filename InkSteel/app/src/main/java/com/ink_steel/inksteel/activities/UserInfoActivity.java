package com.ink_steel.inksteel.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.model.CurrentUser;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class UserInfoActivity extends AppCompatActivity {

    private EditText userName, age, city;
    private ImageView imageView;
    private CurrentUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userName = findViewById(R.id.user_name);
        age = findViewById(R.id.user_age);
        city = findViewById(R.id.user_city);
        imageView = findViewById(R.id.profile_picture);
        Button cancelBtn = findViewById(R.id.button_cancel);
        Button saveBtn = findViewById(R.id.button_save);

        mCurrentUser = CurrentUser.getInstance();

        Intent intent = getIntent();
        if (intent.hasExtra("isNewUser")) {
            if (intent.getBooleanExtra("isNewUser", false))
                cancelBtn.setVisibility(View.INVISIBLE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfoData();
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

    private void saveUserInfoData() {

        String userNameText = userName.getText().toString();

        if (!userNameText.isEmpty()) {

            Map<String, Object> data = new HashMap<>();
            data.put(ConstantUtils.USER_NAME, userNameText);
            data.put(ConstantUtils.USER_CITY, city.getText().toString());
            data.put(ConstantUtils.USER_AGE, age.getText().toString());
            data.put(ConstantUtils.USER_PROFILE_IMG, mCurrentUser.getUserProfilePicture());

            ConstantUtils.FIREBASE_USER_DOCUMENT_REFERENCE.set(data);

            Intent i = new Intent(UserInfoActivity.this, HomeActivity.class);
            startActivity(i);
        } else {
            userName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConstantUtils.FIREBASE_USER_DOCUMENT_REFERENCE.addSnapshotListener(this,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            userName.setText(mCurrentUser.getUserName());
                            city.setText(mCurrentUser.getUserCity());
                            age.setText(mCurrentUser.getUserAge());
                            loadImage();
                        }
                    }
                });
    }

    private void loadImage() {
        Picasso.with(UserInfoActivity.this)
                .load(mCurrentUser.getUserProfilePicture())
                .transform(new CropCircleTransformation())
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            uploadImage();
        }
    }

    private void uploadImage() {
        StorageReference spaceRef = ConstantUtils.FIREBASE_STORAGE_REFERENCE
                .child(ConstantUtils.USER_EMAIL + "/profile.jpg");
        UploadTask uploadTask = spaceRef.putFile(Uri.parse(mCurrentUser.getUserProfilePicture()));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserInfoActivity.this, "Ready!", Toast.LENGTH_SHORT).show();
                loadImage();
            }
        });
    }
}