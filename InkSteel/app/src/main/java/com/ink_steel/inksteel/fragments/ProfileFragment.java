package com.ink_steel.inksteel.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.UserInfoActivity;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment {

    private ImageView imageView;
    private TextView username, email, age, city;
    private Button galeryBtn, messageBtn, editProfileBtn;

    private DocumentReference saveInfo = FirebaseFirestore.getInstance().collection("users").
            document(UserInfoActivity.EMAIL);

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = (ImageView) view.findViewById(R.id.profile);
        username = (TextView) view.findViewById(R.id.user_name);
        email = (TextView) view.findViewById(R.id.user_email);
        age = (TextView) view.findViewById(R.id.user_age);
        city = (TextView) view.findViewById(R.id.user_city);
        galeryBtn = (Button) view.findViewById(R.id.btn_gallery);
        messageBtn = (Button) view.findViewById(R.id.btn_msg);
        editProfileBtn = (Button) view.findViewById(R.id.btn_edit_profile);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        saveInfo.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    email.setText("Email: " + FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                    username.setText("Username: " + documentSnapshot.getString(UserInfoActivity.USER_NAME));
                    city.setText("City: " + documentSnapshot.getString(UserInfoActivity.USER_CITY));
                    age.setText("Age: " + documentSnapshot.getString(UserInfoActivity.USER_AGE));

                    Uri imageDownloadUrl = Uri.parse(documentSnapshot
                            .getString(UserInfoActivity.USER_PROFILE_IMG));

                    Picasso.with(getActivity())
                            .load(imageDownloadUrl)
                            .transform(new CropCircleTransformation())
                            .into(imageView);
                }
            }
        });
    }
}
