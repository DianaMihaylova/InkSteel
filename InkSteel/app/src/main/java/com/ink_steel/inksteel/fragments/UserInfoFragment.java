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

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.data.UserManager;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;


public class UserInfoFragment extends Fragment implements UserManager.UserInfoListener {

    private static final int CHOOSE_IMAGE = 1;
    private EditText name, age, city;
    private ImageView imageView;
    private User mUser;
    private Bitmap imageBitmap;
    private UserManager mManager;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Intent intent = CropImage.activity(data.getData())
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true).getIntent(getActivity());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageBitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), result.getUri());
                Picasso.with(getActivity()).load(result.getUri())
                        .transform(new CropCircleTransformation())
                        .into(imageView);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        name = view.findViewById(R.id.user_name);
        age = view.findViewById(R.id.user_age);
        city = view.findViewById(R.id.user_city);
        imageView = view.findViewById(R.id.profile_picture);
        Button saveBtn = view.findViewById(R.id.button_save);

        imageView.setDrawingCacheEnabled(true);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        mManager = UserManager.getInstance();
        mUser = mManager.getCurrentUser();
        displayUserInfo();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        CHOOSE_IMAGE);
            }
        });

        return view;
    }

    private void updateUserInfo() {
        String userName = name.getText().toString();
        String userAge = age.getText().toString();
        String userCity = city.getText().toString();

        String errorMessage = "Field can't be empty";

        if (userName.isEmpty()) {
            name.setError(errorMessage);
            return;
        }
        if (userAge.isEmpty()) {
            age.setError(errorMessage);
            return;
        }
        if (userCity.isEmpty()) {
            city.setError(errorMessage);
            return;
        }

        mUser.updateUserInfo(userName, userAge, userCity);
        mManager.updateUserInfo(this, imageBitmap);
    }

    private void displayUserInfo() {
        if (mUser != null) {
            name.setText(mUser.getName());
            city.setText(mUser.getCity());
            age.setText(mUser.getAge());
            loadImage(mUser.getProfileImage());
        }
    }

    private void loadImage(String uri) {
        if (!uri.isEmpty()) {
            Picasso.with(getActivity())
                    .load(Uri.parse(uri))
                    .transform(new CropCircleTransformation())
                    .into(imageView);
        }
    }

    @Override
    public void onUserInfoSaved() {
        ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
    }
}
