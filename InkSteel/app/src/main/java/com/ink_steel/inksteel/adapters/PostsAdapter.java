package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.OnPostClickListener;
import com.ink_steel.inksteel.model.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private Context context;
    private List<Post> posts;
    private OnPostClickListener mListener;

    public PostsAdapter(Context context, List<Post> posts, OnPostClickListener listener) {
        this.context = context;
        this.posts = posts;
        mListener = listener;
    }

    @Override
    public PostsAdapter.PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? R.layout.item_post_reversed : R.layout.item_post;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic, postPic;
        TextView summary, postText;

        PostsViewHolder(final View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profile_pic);
            postPic = itemView.findViewById(R.id.post_pic);
            summary = itemView.findViewById(R.id.user_tv);
            postText = itemView.findViewById(R.id.date_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPostClickListener(getAdapterPosition());
                }
            });
        }

        void bind(Post post) {
            summary.setText(post.getUserEmail());

            SimpleDateFormat format = new SimpleDateFormat("hh:mm, dd MMM",
                    Locale.getDefault());
            Date date = new Date(post.getCreatedAt());

            postText.setText(format.format(date));
            Picasso.with(context)
                    .load(post.getUrlProfileImage())
                    .transform(new CropCircleTransformation())
                    .into(profilePic);
            Picasso.with(context)
                    .load(post.getUrlThumbnailImage())
                    .into(postPic);
        }
    }
}