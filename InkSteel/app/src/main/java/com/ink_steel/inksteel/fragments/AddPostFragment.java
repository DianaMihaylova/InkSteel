package com.ink_steel.inksteel.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.PermissionUtil;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment implements DatabaseManager.PostSavedListener {

    private static final int IMAGE_CHOOSER_REQUEST_CODE = 1;
    private CropImageView mCropImageView;
    private Button mAddImageButton;
    private EditText mDescriptionEditText;
    private Uri mUri;
    private Button mSaveButton;
    private Snackbar mSnackbar;

    public AddPostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        mCropImageView = view.findViewById(R.id.crop_iv);
        mAddImageButton = view.findViewById(R.id.add_image_b);
        Button mCancelButton = view.findViewById(R.id.cancel_b);
        mSaveButton = view.findViewById(R.id.save_b);
        mDescriptionEditText = view.findViewById(R.id.description_et);
        View layoutCOntainer = getActivity().findViewById(R.id.activity_home_container);
        mSnackbar = Snackbar.make(layoutCOntainer, "Saving...", Snackbar.LENGTH_INDEFINITE);

        final DatabaseManager manager = DatabaseManager.getInstance();

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    PermissionUtil.requestPermission(AddPostFragment.this, PermissionUtil.PermissionType.STORAGE);
                } else {
                    selectImageForPost();
                }
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackbar.show();
                manager.savePost(AddPostFragment.this, mCropImageView.getCroppedImage(), mUri,
                        mDescriptionEditText.getText().toString());
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageForPost();
            } else {
                Toast.makeText(getActivity(), "Permission is necessary to get images from your device!", Toast.LENGTH_LONG).show();
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
                mCropImageView.setImageBitmap(imageBitmap);
                mCropImageView.setAspectRatio(4, 3);

                mSaveButton.setVisibility(View.VISIBLE);
                mAddImageButton.setVisibility(View.GONE);
                mDescriptionEditText.setVisibility(View.VISIBLE);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public void onPostSaved() {
        mSnackbar.dismiss();
        ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
    }
}
