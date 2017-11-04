package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.Listeners.PostClickListener;
import com.ink_steel.inksteel.model.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private PostClickListener mListener;

    public PostsAdapter(Context context, List<Post> posts, PostClickListener listener) {
        mContext = context;
        mPosts = posts;
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
        holder.bind(mPosts.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? R.layout.item_post_reversed : R.layout.item_post;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic, postPic;
        TextView userName, postText;

        PostsViewHolder(final View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            postPic = itemView.findViewById(R.id.post_pic);
            userName = itemView.findViewById(R.id.user_tv);
            postText = itemView.findViewById(R.id.date_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPostClick(getAdapterPosition());
                }
            });
        }

        void bind(Post post) {
            userName.setText(post.getUserEmail());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm, dd MMM",
                    Locale.getDefault());
            Date date = new Date(post.getCreatedAt());
            postText.setText(format.format(date));
            Picasso.with(mContext)
                    .load(post.getUrlProfileImage())
                    .transform(new CropCircleTransformation())
                    .into(profilePic);
            Picasso.with(mContext)
                    .load(post.getUrlThumbnailImage())
                    .into(postPic);
        }
    }
}