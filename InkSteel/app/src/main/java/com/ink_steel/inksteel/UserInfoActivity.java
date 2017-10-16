package com.ink_steel.inksteel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    public static final String USER_NAME = "userName";
    public static final String USER_CITY = "userCity";
    public static final String USER_AGE = "userAge";

    private EditText userName, age, city;
    private Button cancelBtn, saveBtn;
    private ImageView imageView;
    private DocumentReference saveInfo = FirebaseFirestore.getInstance().collection("users").
            document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mRefStrorage = storage.getReference();
    private UploadTask uploadTask;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userName = (EditText) findViewById(R.id.user_name);
        age = (EditText) findViewById(R.id.user_age);
        city = (EditText) findViewById(R.id.user_city);
        imageView = (ImageView) findViewById(R.id.user_profile_img);
        cancelBtn = (Button) findViewById(R.id.button_cancel);
        saveBtn = (Button) findViewById(R.id.button_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String userCity = city.getText().toString();
                String userAge = age.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put(USER_NAME, username);
                data.put(USER_CITY, userCity);
                data.put(USER_AGE, userAge);
                saveInfo.set(data);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        saveInfo.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    userName.setText(documentSnapshot.getString(USER_NAME));
                    city.setText(documentSnapshot.getString(USER_CITY));
                    age.setText(documentSnapshot.getString(USER_AGE));
                }
            }
        });
    }

    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            Picasso.with(this).load(selectedImage).into(imageView);
            uploadImage();
        }
    }

    private void uploadImage() {
        StorageReference spaceRef = mRefStrorage.child("images/space.jpg");
        uploadTask = spaceRef.putFile(selectedImage);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserInfoActivity.this, "Ready!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
