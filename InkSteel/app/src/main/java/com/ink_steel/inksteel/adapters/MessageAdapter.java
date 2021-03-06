package com.ink_steel.inksteel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.PostsViewHolder> {

    private List<Message> mMessages;
    private String mUsername;

    public MessageAdapter(List<Message> messages, String username) {
        mMessages = messages;
        mUsername = username;
    }

    @Override
    public MessageAdapter.PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        holder.bind(mMessages.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (mUsername.equals(mMessages.get(position).getUserEmail())) {
            return R.layout.item_message_reverse;
        }
        return R.layout.item_message;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        TextView messageTv, time;

        PostsViewHolder(final View itemView) {
            super(itemView);
            messageTv = itemView.findViewById(R.id.item_message);
            time = itemView.findViewById(R.id.item_time);
        }

        void bind(Message message) {
            messageTv.setText(message.getMessage());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm",
                    Locale.getDefault());
            Date date = new Date(message.getTime());
            time.setText(format.format(date));
        }
    }
}