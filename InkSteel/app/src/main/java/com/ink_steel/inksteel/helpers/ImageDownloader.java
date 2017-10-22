package com.ink_steel.inksteel.helpers;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDownloader implements Target {

    private final String mImageName;
    private CropImageView mCropImageView;

    public ImageDownloader(String imageName, CropImageView cropImageView) {
        mImageName = imageName;
        mCropImageView = cropImageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + mImageName + ".jpeg");
        try {
            if (file.createNewFile()) {
                Log.d("ImageDownloader", "File created");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                mCropImageView.setImageBitmap(bitmap);
            }
            mCropImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ImageDownloader", "Error");
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
