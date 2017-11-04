package com.ink_steel.inksteel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.Listeners.GalleryImageLongClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private ArrayList<String> mGallery;
    private GalleryImageLongClickListener mListener;

    public GalleryRecyclerViewAdapter(ArrayList<String> gallery, GalleryImageLongClickListener listener) {
        mGallery = gallery;
        mListener = listener;
    }

    @Override
    public GalleryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_image, parent, false);
        GalleryViewHolders vh = new GalleryViewHolders(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolders holder, int position) {
        holder.bind(mGallery.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mGallery.size();
    }

    class GalleryViewHolders extends RecyclerView.ViewHolder {

        private ImageView image;

        private GalleryViewHolders(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

        private void bind(String u, final int pos) {
            Picasso.with(itemView.getContext())
                    .load(u)
                    .resize(200, 200)
                    .centerCrop()
                    .into(image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onGalleryImageLongClick(pos, false);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onGalleryImageLongClick(pos, true);
                    return true;
                }
            });
        }
    }
}