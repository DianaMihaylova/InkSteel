package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.IOnGalleryImageLongClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private Context context;
    private ArrayList<Uri> images;
    private IOnGalleryImageLongClickListener listener;

    public GalleryRecyclerViewAdapter(Context context, ArrayList<Uri> images, IOnGalleryImageLongClickListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolders holder, final int position) {
        holder.bind(images.get(position), position);
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

        public void bind(Uri u, final int pos) {
            Picasso.with(itemView.getContext())
                    .load(u)
                    .transform(new CropCircleTransformation())
                    .resize(600, 690)
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