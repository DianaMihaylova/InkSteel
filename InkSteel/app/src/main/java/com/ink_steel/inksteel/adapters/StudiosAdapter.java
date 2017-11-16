package com.ink_steel.inksteel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.Listeners.StudioClickListener;
import com.ink_steel.inksteel.model.Studio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StudiosAdapter extends RecyclerView.Adapter<StudiosAdapter.PostsViewHolder> {

    private List<Studio> mStudios;
    private StudioClickListener mListener;

    public StudiosAdapter(StudioClickListener listener, List<Studio> studios) {
        mStudios = studios;
        mListener = listener;
    }

    @Override
    public StudiosAdapter.PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_studio, parent, false));
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        holder.bind(mStudios.get(position));
    }

    @Override
    public int getItemCount() {
        return mStudios.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, readMore, openNow;
        RatingBar rating;

        PostsViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.studio_iv);
            title = itemView.findViewById(R.id.studio_name_tv);
            rating = itemView.findViewById(R.id.studio_rb);
            readMore = itemView.findViewById(R.id.studio_read_more_tv);
            openNow = itemView.findViewById(R.id.studio_open_now);
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onStudioClick(getAdapterPosition());
                }
            });
        }

        void bind(Studio studio) {
            String url = studio.getPhotoUrl();
            if (url != null)
                Picasso.with(itemView.getContext()).load(studio.getPhotoUrl()).into(image);
            else {
                image.setImageResource(R.drawable.placeholder);
            }
            if (studio.isOpenNow())
                openNow.setVisibility(View.VISIBLE);
            title.setText(studio.getName());
            rating.setRating(studio.getRating());
        }
    }
}
