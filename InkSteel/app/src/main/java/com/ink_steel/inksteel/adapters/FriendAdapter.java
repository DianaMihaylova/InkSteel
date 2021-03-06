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
import com.ink_steel.inksteel.activities.HomeActivity;
import com.ink_steel.inksteel.fragments.ChatFragment;
import com.ink_steel.inksteel.helpers.Listeners.FriendClickListener;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context mContext;
    private ArrayList<User> mUsers;
    private FriendClickListener mListener;

    public FriendAdapter(Context context, ArrayList<User> users, FriendClickListener listener) {
        mContext = context;
        mUsers = users;
        mListener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendAdapter.FriendViewHolder holder, int position) {
        holder.bind(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePic;
        private TextView userName, userCity;
        private Button msg;

        FriendViewHolder(final View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userCity = itemView.findViewById(R.id.user_city);
            msg = itemView.findViewById(R.id.btn_msg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFriendClick(getAdapterPosition());
                }
            });

            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HomeActivity) itemView.getContext())
                            .replaceFragment(ChatFragment
                                    .newInstance(mUsers.get(getAdapterPosition()).getEmail()));
                }
            });
        }

        void bind(User user) {
            userName.setText(user.getName());
            userCity.setText(user.getCity());

            Picasso.with(mContext)
                    .load(user.getProfileImage())
                    .into(profilePic);
        }
    }
}