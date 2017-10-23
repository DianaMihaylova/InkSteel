package com.ink_steel.inksteel;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.model.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private Uri mImage;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostsAdapter.PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return new PostsViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_item, parent, false));
            default:
                return new PostsViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_item_reverse, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);

        if (holder instanceof PostsViewHolder) {
            PostsViewHolder holder2 = (PostsViewHolder) holder;
            holder2.summary.setText(post.getUser());

            holder2.postText.setText(dateFormatter(post.getDate()));

            Picasso.with(context)
                    .load(post.getProfileUri())
                    .transform(new CropCircleTransformation())
                    .into(holder2.profilePic);
            if (post.getProfileUri() != null)
                Picasso.with(context)
                        .load(post.getImageUrl())
                        .into(holder2.postPic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 2 : 1;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private String dateFormatter(Date date) {
        String today = new SimpleDateFormat("dd MMMM",
                Locale.US).format(new Date());
        if (today.equals(new SimpleDateFormat("dd MMMM",
                Locale.US).format(date)))
            return new SimpleDateFormat("h:mm",
                    Locale.US).format(date) + ", Today";

        return new SimpleDateFormat("h:mm, dd MMMM", Locale.US).format(date);
    }

    private class PostsViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic, postPic;
        TextView summary, postText;

        PostsViewHolder(View itemView) {
            super(itemView);

            profilePic = (ImageView) itemView.findViewById(R.id.profile_pic);
            postPic = (ImageView) itemView.findViewById(R.id.post_pic);
            summary = (TextView) itemView.findViewById(R.id.user_tv);
            postText = (TextView) itemView.findViewById(R.id.date_tv);
        }

    }
}
