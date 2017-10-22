package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.FullScreenImageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private ArrayList<Uri> images;
    private Context context;

    public GalleryRecyclerViewAdapter(Context context, ArrayList<Uri> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolders holder, int pos) {
        final int position = pos;
        Picasso.with(context)
                .load(images.get(position))
                .transform(new CropCircleTransformation())
                .resize(600, 690)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image", position);
                context.startActivity(intent);
            }
        });
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
    }
}