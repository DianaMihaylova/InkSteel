package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.ChatActivity;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.data.FirebaseManager;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment implements FirebaseManager.CurrentUserInfoListener {

    private ImageView imageView;
    private TextView username, email, age, city;
    private User currentUser;
    private FirebaseManager.UserManager mManager;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.profile);
        username = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        age = view.findViewById(R.id.user_age);
        city = view.findViewById(R.id.user_city);
        Button galleryBtn = view.findViewById(R.id.btn_gallery);
        Button messageBtn = view.findViewById(R.id.btn_msg);
        Button editProfileBtn = view.findViewById(R.id.btn_edit_profile);

        mManager = FirebaseManager.getInstance().getUserManager();


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
                ((HomeActivity) getActivity()).replaceFragment(new GalleryFragment());
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new UserInfoFragment());
            }
        });

        return view;
    }

    private void displayUserInfo() {
        email.setText(currentUser.getEmail());
        username.setText(currentUser.getName());
        city.setText(currentUser.getCity());
        age.setText(currentUser.getAge());

        Picasso.with(getActivity())
                .load(currentUser.getProfileImage())
                .transform(new CropCircleTransformation())
                .into(imageView);
    }

    @Override
    public void onInfoLoaded(boolean isNewUser) {
        currentUser = mManager.getCurrentUser();
        displayUserInfo();
    }
}