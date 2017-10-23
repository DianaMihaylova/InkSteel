package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.FullScreenImageActivity;
import com.ink_steel.inksteel.activities.GalleryActivity;
import com.ink_steel.inksteel.helpers.ConstantUtils;
import com.ink_steel.inksteel.helpers.IOnGalleryImageLongClickListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private ArrayList<Uri> images;
    private Context context;
    private IOnGalleryImageLongClickListener listener;

    public GalleryRecyclerViewAdapter(Context context, ArrayList<Uri> images, IOnGalleryImageLongClickListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolders holder, final int position) {

//        Picasso.with(context)
//                .load(images.get(position))
//                .transform(new CropCircleTransformation())
//                .resize(600, 690)
//                .into(holder.image);
        holder.bind(images.get(position), listener, position);
    }

    @Override
    public GalleryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list, parent, false);
        GalleryViewHolders vh = new GalleryViewHolders(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }

    public class GalleryViewHolders extends RecyclerView.ViewHolder {

        public ImageView image;

        public GalleryViewHolders(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bind(Uri u, IOnGalleryImageLongClickListener l, final int pos) {
            Picasso.with(itemView.getContext())
                    .load(u)
                    .into(image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onGalleryImageLongClickListener(pos, false);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onGalleryImageLongClickListener(pos, true);
                    return true;
                }
            });
        }
    }
}