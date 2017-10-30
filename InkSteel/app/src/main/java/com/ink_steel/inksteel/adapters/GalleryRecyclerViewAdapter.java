package com.ink_steel.inksteel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.IOnGalleryImageLongClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private List<String> images = new ArrayList<>();
    private IOnGalleryImageLongClickListener listener;

    public GalleryRecyclerViewAdapter(List<String> images,
                                      IOnGalleryImageLongClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @Override
    public GalleryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_image, parent, false);
        GalleryViewHolders vh = new GalleryViewHolders(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolders holder, int position) {
        holder.bind(images.get(position), position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class GalleryViewHolders extends RecyclerView.ViewHolder {

        private ImageView image;

        private GalleryViewHolders(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

        private void bind(String u, final int pos) {
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