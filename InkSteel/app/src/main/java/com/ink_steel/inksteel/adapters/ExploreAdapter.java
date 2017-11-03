package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ExploreAdapter extends BaseAdapter {

    private List<User> users;
    private Context context;

    public ExploreAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        if (position % 2 == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_reverse, parent, false);
        }

        User user = (User) getItem(position);

        String username = "Username:\n" + user.getName();
        String country = "City:\n" + user.getCity();
        ((TextView) v.findViewById(R.id.user_name)).setText(username);
        ((TextView) v.findViewById(R.id.user_city)).setText(country);
        ImageView imageView = v.findViewById(R.id.profile_pic);

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty())
            Picasso.with(context)
                    .load(user.getProfileImage())
                    .transform(new CropCircleTransformation())
                    .into(imageView);

        return v;
    }
}