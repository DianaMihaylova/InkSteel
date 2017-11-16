package com.ink_steel.inksteel.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.helpers.PermissionHelper;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.ink_steel.inksteel.activities.LoginActivity.IS_NEW_USER;

public class UserInfoFragment extends Fragment implements DatabaseManager.UserInfoUpdatedListener {

    private static final int CHOOSE_IMAGE_REQUEST_CODE = 1;

    @BindView(R.id.fragment_user_info_profile_iv)
    ImageView mProfileImage;
    @BindView(R.id.fragment_user_info_user_name_et)
    EditText mUserName;
    @BindView(R.id.fragment_user_info_user_city_et)
    EditText mCity;
    @BindView(R.id.fragment_user_info_user_age_et)
    EditText mAge;

    private DatabaseManager.UserManager mManager2;
    private boolean mIsNewUser;
    private User mCurrentUser;
    private Bitmap mImageBitmap;
    private Listeners.ShowSnackBarListener mSnackBarListener;
    private Listeners.OnReplaceFragmentListener mReplaceFragmentListener;

    public UserInfoFragment() {
    }

    public static UserInfoFragment newInstance(boolean isNewUser) {
        UserInfoFragment fragment = new UserInfoFragment();

        Bundle bundle = new Bundle(1);
        bundle.putBoolean(IS_NEW_USER, isNewUser);
        fragment.setArguments(bundle);

        return fragment;
    }

    @OnClick(R.id.fragment_user_info_save_btn)
    public void save() {
        mSnackBarListener.showSnackBar("Saving...");
        updateUserInfo();
    }

    @OnClick(R.id.fragment_user_info_profile_iv)
    public void changeImage() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.requestPermission(UserInfoFragment.this,
                    PermissionHelper.PermissionType.STORAGE);
        } else {
            selectProfileImage();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listeners.OnReplaceFragmentListener) {
            mReplaceFragmentListener = (Listeners.OnReplaceFragmentListener) context;
        } else {
            Log.e(UserInfoFragment.class.getSimpleName(), "Activity not implementing " +
                    "ShowSnackBarListener interface");
        }

        if (context instanceof Listeners.ShowSnackBarListener) {
            mSnackBarListener = (Listeners.ShowSnackBarListener) context;
        } else {
            Log.e(UserInfoFragment.class.getSimpleName(), "Activity not implementing " +
                    "ShowSnackBarListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this, view);
        mProfileImage.setDrawingCacheEnabled(true);
        mManager2 = DatabaseManager.getUserManager();
        mCurrentUser = mManager2.getCurrentUser();
        mIsNewUser = getArguments().getBoolean(IS_NEW_USER);
        displayUserInfo(mIsNewUser);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectProfileImage();
            } else {
                Toast.makeText(getActivity(), R.string.image_text_permission,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                CHOOSE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = CropImage.activity(data.getData())
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true).getIntent(getActivity());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                mImageBitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), result.getUri());
                loadImage(result.getUri().toString());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void updateUserInfo() {
        String userName = mUserName.getText().toString();
        String userAge = mAge.getText().toString();
        String country = mCity.getText().toString();

        String errorMessage = getString(R.string.empty_field_error);

        if (userName.isEmpty()) {
            mUserName.setError(errorMessage);
            return;
        }
        if (userAge.isEmpty()) {
            mAge.setError(errorMessage);
            return;
        }
        if (country.isEmpty()) {
            mCity.setError(errorMessage);
            return;
        }
        mCurrentUser.updateUserInfo(userName, userAge, country);
        mManager2.updateUserInfo(this, mImageBitmap, mIsNewUser);
    }

    private void displayUserInfo(boolean isNewUser) {
        if (!isNewUser) {
            mUserName.setText(mCurrentUser.getName());
            mCity.setText(mCurrentUser.getCity());
            mAge.setText(mCurrentUser.getAge());
            loadImage(mCurrentUser.getProfileImage());
        }
    }

    private void loadImage(String uri) {
        if (!uri.isEmpty()) {
            Picasso.with(getActivity())
                    .load(uri)
                    .transform(new CropCircleTransformation())
                    .into(mProfileImage);
        }
    }

    @Override
    public void onUserInfoUpdated() {
        mSnackBarListener.dismissSnackBar();
        mReplaceFragmentListener.replaceFragment(new ViewPagerFragment());
    }

    @Override
    public void onError(String message) {

    }
}