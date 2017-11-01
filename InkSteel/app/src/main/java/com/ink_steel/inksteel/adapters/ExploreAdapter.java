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

        String username = "Username:\n" + users.get(position).getName();
        String country = "Country:\n" + users.get(position).getCountry();
        ((TextView) v.findViewById(R.id.user_name)).setText(username);
        ((TextView) v.findViewById(R.id.user_country)).setText(country);
        ImageView imageView = v.findViewById(R.id.profile_pic);

        Picasso.with(context)
                .load(users.get(position).getProfileImage())
                .transform(new CropCircleTransformation())
                .into(imageView);

        return v;
    }
}