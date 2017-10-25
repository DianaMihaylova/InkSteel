package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private Context context;
    private List<Post> posts;

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
    public void onBindViewHolder(PostsViewHolder holder, int position) {

        Post post = posts.get(position);
        holder.summary.setText(post.getUser());
        holder.postText.setText(post.getDate());
        Picasso.with(context)
                .load(post.getProfileUri())
                .transform(new CropCircleTransformation())
                .into(holder.profilePic);
        Picasso.with(context)
                .load(post.getImageUrl())
                .into(holder.postPic);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 2 : 1;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic, postPic;
        TextView summary, postText;
        ImageButton expand;
        LinearLayout linearLayout;

        PostsViewHolder(final View itemView) {
            super(itemView);

            profilePic = (ImageView) itemView.findViewById(R.id.profile_pic);
            postPic = (ImageView) itemView.findViewById(R.id.post_pic);
            summary = (TextView) itemView.findViewById(R.id.user_tv);
            postText = (TextView) itemView.findViewById(R.id.date_tv);
            expand = (ImageButton) itemView.findViewById(R.id.expand);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.reactions);

            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (linearLayout.getVisibility() == View.GONE) {
                        linearLayout.setVisibility(View.VISIBLE);
                        expand.setImageResource(R.drawable.ic_expand_less);
                    } else {
                        linearLayout.setVisibility(View.GONE);
                        expand.setImageResource(R.drawable.ic_expand_more);
                    }
                }
            });
        }
    }
}
