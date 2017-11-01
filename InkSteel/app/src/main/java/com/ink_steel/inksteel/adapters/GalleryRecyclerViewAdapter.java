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

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private ArrayList<String> gallery;
    private GalleryImageLongClickListener listener;

    public GalleryRecyclerViewAdapter(ArrayList<String> gallery, GalleryImageLongClickListener listener) {
        this.gallery = gallery;
        gallery.add("https://undertheradar.military.com/wp-content/uploads/2016/07/Anchor-Tattoo-750x437.jpg");
        gallery.add("https://i.pinimg.com/736x/c5/d8/c5/c5d8c5194695ac74c24bf9184333b9e9--rose-band-tattoo-rose-bracelet-tattoo.jpg");
        gallery.add("http://lazara.bg/wp-content/uploads/2016/01/%D1%82%D0%B0%D1%82%D1%83%D1%81%D0%B8-%D0%B7%D0%B0-%D0%B3%D1%80%D1%8A%D0%B1-13.jpg");
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
        holder.bind(gallery.get(position), position);
    }

    @Override
    public int getItemCount() {
        return gallery.size();
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
                    listener.onGalleryImageLongClick(pos, false);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onGalleryImageLongClick(pos, true);
                    return true;
                }
            });
        }
    }
}