package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment implements DatabaseManager.PostSavedListener {

    public static final int IMAGE_CHOOSER_REQUEST_CODE = 1;
    private CropImageView mCropImageView;
    private Button mAddImageButton;
    private EditText mDescriptionEditText;

    private Uri mUri;
    private Button mSaveButton;

    public AddPostFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        mCropImageView = view.findViewById(R.id.crop_iv);
        mAddImageButton = view.findViewById(R.id.add_image_b);
        Button cancelButton = view.findViewById(R.id.cancel_b);
        mSaveButton = view.findViewById(R.id.save_b);
        mDescriptionEditText = view.findViewById(R.id.description_et);

        final DatabaseManager manager = DatabaseManager.getInstance();

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        IMAGE_CHOOSER_REQUEST_CODE);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.savePost(AddPostFragment.this, mCropImageView.getCroppedImage(), mUri,
                        mDescriptionEditText.getText().toString());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
            }
        });

        return view;
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
        ((HomeActivity) getActivity()).replaceFragment(new ScreenSlidePageFragment());
    }
}
