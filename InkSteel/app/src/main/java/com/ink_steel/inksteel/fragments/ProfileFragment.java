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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.ChatActivity;
import com.ink_steel.inksteel.activities.GalleryActivity;
import com.ink_steel.inksteel.activities.UserInfoActivity;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.helpers.CurrentUser;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment {

    private ImageView imageView;
    private TextView username, email, age, city;
    private CurrentUser mCurrentUser;
//    private DocumentReference saveInfo = FirebaseFirestore.getInstance().collection("users").
//            document(mCurrentUser);

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mCurrentUser = CurrentUser.getInstance();

        imageView = (ImageView) view.findViewById(R.id.profile);
        username = (TextView) view.findViewById(R.id.user_name);
        email = (TextView) view.findViewById(R.id.user_email);
        age = (TextView) view.findViewById(R.id.user_age);
        city = (TextView) view.findViewById(R.id.user_city);
        Button galleryBtn = (Button) view.findViewById(R.id.btn_gallery);
        Button messageBtn = (Button) view.findViewById(R.id.btn_msg);
        Button editProfileBtn = (Button) view.findViewById(R.id.btn_edit_profile);

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DocumentReference saveInfo = FirebaseFirestore.getInstance().collection("users").
                document(mCurrentUser.getUserEmail());
        saveInfo.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    String emailStr = "Email: " + mCurrentUser.getUserEmail();
                    String usernameStr = "Username: " + mCurrentUser.getUserName();
                    String cityStr = "City: " + mCurrentUser.getUserCity();
                    String ageStr = "Age: " + mCurrentUser.getUserAge();
                    email.setText(emailStr);
                    username.setText(usernameStr);
                    city.setText(cityStr);
                    age.setText(ageStr);

                    Uri imageDownloadUrl = Uri.parse(documentSnapshot.getString(ConstantUtils.USER_PROFILE_IMG));
                    mCurrentUser.setUserProfilePicture(imageDownloadUrl.toString());
                    Picasso.with(getActivity())
                            .load(imageDownloadUrl)
                            .transform(new CropCircleTransformation())
                            .into(imageView);
                }
            }
        });
    }
}
