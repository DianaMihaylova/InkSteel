package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.activities.LoginActivity;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment {

    private ImageView mImageView;
    private TextView mUsername, mEmail, mAge, mCity;
    private LinearLayout mLayoutGroupBtn;
    private Button mEditProfileBtn, mGalleryFriendBtn;
    private User mCurrentUser;
    private DatabaseManager mManager;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mManager = DatabaseManager.getInstance();
        mCurrentUser = mManager.getCurrentUser();

        mImageView = view.findViewById(R.id.profile);
        mUsername = view.findViewById(R.id.qwerty);
        mEmail = view.findViewById(R.id.user_email);
        mAge = view.findViewById(R.id.user_age);
        mCity = view.findViewById(R.id.user_city);
        mLayoutGroupBtn = view.findViewById(R.id.layout_btn);
        Button galleryBtn = view.findViewById(R.id.btn_gallery);
        Button friendsBtn = view.findViewById(R.id.btn_msg);
        ImageButton logoutBtn = view.findViewById(R.id.btn_logout);
        mEditProfileBtn = view.findViewById(R.id.btn_edit_profile);
        mGalleryFriendBtn = view.findViewById(R.id.btn_friend_gallery);

        if (mCurrentUser != null) {
            displayUserInfo();
        }

        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new FriendsFragment());
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new GalleryFragment());
            }
        });

        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new UserInfoFragment());
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void displayUserInfo() {
        String emailTxt, userNameTxt, cityTxt, ageTxt, picture;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            final User friend = (User) bundle.getSerializable("friend");
            emailTxt = getString(R.string.email_dot) + friend.getEmail();
            userNameTxt = getString(R.string.username_dot) + "  " + friend.getName();
            cityTxt = getString(R.string.city_dot) + "  " + friend.getCity();
            ageTxt = getString(R.string.age_dot) + "  " + friend.getAge();
            picture = friend.getProfileImage();
            mGalleryFriendBtn.setVisibility(View.VISIBLE);
            mGalleryFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GalleryFragment fragment = new GalleryFragment();
                    Bundle bundle = new Bundle(1);
                    bundle.putSerializable("friend", friend);
                    fragment.setArguments(bundle);
                    ((HomeActivity) getActivity()).replaceFragment(fragment);
                }
            });
        } else {
            emailTxt = getString(R.string.email_dot) + "  " + mCurrentUser.getEmail();
            userNameTxt = getString(R.string.username_dot) + "  " + mCurrentUser.getName();
            cityTxt = getString(R.string.city_dot) + "  " + mCurrentUser.getCity();
            ageTxt = getString(R.string.age_dot) + "  " + mCurrentUser.getAge();
            picture = mCurrentUser.getProfileImage();
            mLayoutGroupBtn.setVisibility(View.VISIBLE);
            mEditProfileBtn.setVisibility(View.VISIBLE);
        }

        mEmail.setText(emailTxt);
        mUsername.setText(userNameTxt);
        mCity.setText(cityTxt);
        mAge.setText(ageTxt);

        Picasso.with(getActivity())
                .load(picture)
                .transform(new CropCircleTransformation())
                .into(mImageView);
    }
}