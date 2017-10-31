package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.helpers.IOnFriendClickListener;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private IOnFriendClickListener mListener;

    public FriendAdapter(Context context, ArrayList<User> users, IOnFriendClickListener listener) {
        this.context = context;
        this.users = users;
        mListener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendAdapter.FriendViewHolder vh = new FriendAdapter.FriendViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FriendAdapter.FriendViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePic;
        private TextView userName, userCity;
        Button msg;

        FriendViewHolder(final View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userCity = itemView.findViewById(R.id.user_city);
            msg = itemView.findViewById(R.id.btn_msg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFriendClickListener(getAdapterPosition());
                }
            });
        }

        void bind(User user) {
            userName.setText(user.getName());
            userCity.setText(user.getCountry());

            Picasso.with(context)
                    .load(user.getProfileImage())
                    .into(profilePic);
        }
    }
}