package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.ChatActivity;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment {

    private ImageView imageView;
    private TextView username, email, age, city;
    private LinearLayout layoutGroupBtn;
    private Button editProfileBtn, galleryFriendBtn;
    private User mCurrentUser;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mCurrentUser = DatabaseManager.getInstance().getCurrentUser();

        imageView = view.findViewById(R.id.profile);
        username = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        age = view.findViewById(R.id.user_age);
        city = view.findViewById(R.id.user_city);
        layoutGroupBtn = view.findViewById(R.id.layout_btn);
        Button galleryBtn = view.findViewById(R.id.btn_gallery);
        Button messageBtn = view.findViewById(R.id.btn_msg);
        editProfileBtn = view.findViewById(R.id.btn_edit_profile);
        galleryFriendBtn = view.findViewById(R.id.btn_friend_gallery);

        if (mCurrentUser != null)
            displayUserInfo();

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
        String emailTxt, userNameTxt, cityTxt, ageTxt, picture;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            final int position = bundle.getInt("position");
            emailTxt = "Email: " + FriendFragment.mUsers.get(position).getEmail();
            userNameTxt = "Username: " + FriendFragment.mUsers.get(position).getName();
            cityTxt = "Country: " + FriendFragment.mUsers.get(position).getCountry();
            ageTxt = "Age: " + FriendFragment.mUsers.get(position).getAge();
            picture = FriendFragment.mUsers.get(position).getProfileImage();
            galleryFriendBtn.setVisibility(View.VISIBLE);
            galleryFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FullScreenImageFragment fragment = new FullScreenImageFragment();
                    Bundle bundle = new Bundle(1);
                    bundle.putInt("position", position);
                    fragment.setArguments(bundle);
                    ((HomeActivity) getActivity()).replaceFragment(fragment);
                }
            });
        } else {
            emailTxt = "Email: " + mCurrentUser.getEmail();
            userNameTxt = "Username: " + mCurrentUser.getName();
            cityTxt = "Country: " + mCurrentUser.getCountry();
            ageTxt = "Age: " + mCurrentUser.getAge();
            picture = mCurrentUser.getProfileImage();
            layoutGroupBtn.setVisibility(View.VISIBLE);
            editProfileBtn.setVisibility(View.VISIBLE);
        }

        email.setText(emailTxt);
        username.setText(userNameTxt);
        city.setText(cityTxt);
        age.setText(ageTxt);

        Picasso.with(getActivity())
                .load(picture)
                .transform(new CropCircleTransformation())
                .into(imageView);
    }

}