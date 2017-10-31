package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.Studio;

import java.util.List;

public class StudiosAdapter extends RecyclerView.Adapter<StudiosAdapter.PostsViewHolder> {

    private Context context;
    private List<Studio> studios;

    public StudiosAdapter(Context context, List<Studio> studios) {
        this.context = context;
        this.studios = studios;
    }

    @Override
    public StudiosAdapter.PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_studio, parent, false));
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        holder.bind(studios.get(position));
    }

    @Override
    public int getItemCount() {
        return studios.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        RatingBar rating;

        PostsViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.studio_iv);
            title = itemView.findViewById(R.id.studio_tv);
            rating = itemView.findViewById(R.id.studio_rb);
        }

        void bind(Studio studio) {
            image.setImageResource(studio.getImageId());
            title.setText(studio.getName());
            rating.setRating(studio.getRating());
        }
    }
}
