package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.FullScreenImageActivity;

import java.util.ArrayList;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolders> {

    private ArrayList image;
    private Context context;

    public GalleryRecyclerViewAdapter(Context context, ArrayList image) {
        this.context = context;
        this.image = image;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolders holder, int pos) {
        final int position = pos;
        holder.image.setImageResource((Integer) image.get(position));
        // setOnClickListener event on item view not working!!!
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("image", (Integer) image.get(position)); // put image data in Intent
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
        return this.image.size();
    }

    public class GalleryViewHolders extends RecyclerView.ViewHolder {

        public ImageView image;

        public GalleryViewHolders(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}