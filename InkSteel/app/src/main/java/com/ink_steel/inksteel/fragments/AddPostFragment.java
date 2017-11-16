package com.ink_steel.inksteel.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.helpers.PermissionHelper;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment implements DatabaseManager.OnPostSavedErrorListener {

    private static final int IMAGE_CHOOSER_REQUEST_CODE = 1;
    @BindView(R.id.fragment_add_post_crop_iv)
    CropImageView mCropIv;
    @BindView(R.id.fragment_add_post_description_et)
    EditText mDescriptionEt;
    @BindView(R.id.fragment_add_post_save_btn)
    Button mSaveBtn;
    @BindView(R.id.fragment_add_post_choose_image_btn)
    Button mChooseImage;
    private Uri mUri;
    private Listeners.OnReplaceFragmentListener mReplaceFragmentListener;

    public AddPostFragment() {
    }

    @OnClick(R.id.fragment_add_post_save_btn)
    public void savePost() {
        DatabaseManager.PostsManager manager = DatabaseManager.getPostsManager();
        manager.savePost(this, mCropIv.getCroppedImage(), mUri,
                mDescriptionEt.getText().toString());
        mReplaceFragmentListener.replaceFragment(new ViewPagerFragment());
    }

    @OnClick(R.id.fragment_add_post_choose_image_btn)
    public void chooseImage() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.requestPermission(AddPostFragment.this,
                    PermissionHelper.PermissionType.STORAGE);
        } else {
            selectImageForPost();
        }
    }

    @OnClick(R.id.fragment_add_post_cancel_btn)
    public void cancel() {
        mReplaceFragmentListener.replaceFragment(new ViewPagerFragment());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listeners.OnReplaceFragmentListener) {
            mReplaceFragmentListener = (Listeners.OnReplaceFragmentListener) context;
        } else {
            Log.e(AddPostFragment.class.getSimpleName(), "Activity not implementing " +
                    "OnReplaceFragmentListener interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageForPost();
            } else {
                Toast.makeText(getActivity(),
                        "Permission is necessary to get images from your device!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectImageForPost() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                IMAGE_CHOOSER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            mUri = data.getData();

            try {
                Bitmap imageBitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), mUri);
                mCropIv.setImageBitmap(imageBitmap);
                mCropIv.setAspectRatio(4, 3);

                mSaveBtn.setVisibility(View.VISIBLE);
                mChooseImage.setVisibility(View.GONE);
                mDescriptionEt.setVisibility(View.VISIBLE);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String message) {

    }
}
