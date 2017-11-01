package com.ink_steel.inksteel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.Listeners.StudioClickListener;
import com.ink_steel.inksteel.model.User;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.PostsViewHolder> {

    private List<User> mUsers;
    private StudioClickListener mListener;

    public ChatListAdapter(StudioClickListener listener, List<User> users) {
        mUsers = users;
        mListener = listener;
    }

    @Override
    public ChatListAdapter.PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false));
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        holder.bind(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, lastMsg, time;
        private ImageView image;

        PostsViewHolder(final View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.item_chat_username);
            lastMsg = itemView.findViewById(R.id.item_chat_msg);
            time = itemView.findViewById(R.id.item_chat_time);
            image = itemView.findViewById(R.id.item_chat_image);

        }

        void bind(User user) {

        }
    }
}
