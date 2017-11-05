package com.ink_steel.inksteel.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.Listeners.ChatListClickListener;
import com.ink_steel.inksteel.model.ChatRoom;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.PostsViewHolder> {

    private List<ChatRoom> mChatRooms;
    private ChatListClickListener mListener;

    public ChatListAdapter(ChatListClickListener listener, List<ChatRoom> chatRooms) {
        mChatRooms = chatRooms;
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
        holder.bind(mChatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onChatItemClick(getAdapterPosition());
                }
            });

        }

        void bind(ChatRoom chatRoom) {

            userName.setText(chatRoom.getEmail());
            Picasso.with(itemView.getContext())
                    .load(chatRoom.getProfilePicture())
                    .placeholder(R.drawable.placeholder_posts)
                    .into(image);
            if (!chatRoom.isSeen())
                lastMsg.setTypeface(null, Typeface.BOLD);
            lastMsg.setText(chatRoom.getLastMessage());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm, dd MMM",
                    Locale.getDefault());
            Date date = new Date(chatRoom.getLastMessageTime());
            time.setText(format.format(date));

        }
    }
}
