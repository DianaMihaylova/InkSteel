package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.adapters.GalleryRecyclerViewAdapter;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.helpers.Listeners.GalleryImageLongClickListener;
import com.ink_steel.inksteel.model.User;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment implements GalleryImageLongClickListener {

    private static final int CHOOSE_IMAGE = 1;
    private GalleryRecyclerViewAdapter mAdapter;
    private User mCurrentUser;
    private DatabaseManager mUserManager;

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mUserManager = DatabaseManager.getInstance();
        mCurrentUser = mUserManager.getCurrentUser();

        FloatingActionButton fab = view.findViewById(R.id.btn_fab);

        RecyclerView mRecyclerView = view.findViewById(R.id.image_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (mCurrentUser != null)
            mAdapter = new GalleryRecyclerViewAdapter(mCurrentUser.getGallery(), this);

        mRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return view;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mUserManager.saveImage(uri, mAdapter);
        }
    }

    @Override
    public void onGalleryImageLongClick(int position, boolean isLongClick) {
        if (isLongClick) {
            showAlert(position);
        } else {
            FullScreenImageFragment fragment = new FullScreenImageFragment();
            Bundle bundle = new Bundle(1);
            bundle.putInt("image", position);
            fragment.setArguments(bundle);
            ((HomeActivity) getActivity()).replaceFragment(fragment);
        }
    }

    private void showAlert(final int position) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Do you want to delete the image?")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String image = mCurrentUser.getGallery().get(position);
                        mCurrentUser.getGallery().remove(image);
                        mAdapter.notifyDataSetChanged();
                        mUserManager.removeImage(image);
                    }
                });
        AlertDialog ad = alertBuilder.create();
        ad.setTitle("WARNING MESSAGE");
        ad.setIcon(R.drawable.warning_msg);
        ad.show();
    }
}