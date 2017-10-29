package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.fragments.ExploreFragment;
import com.ink_steel.inksteel.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExploreAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<User> users;

    public ExploreAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (position % 2 == 0) {
            view = layoutInflater.inflate(R.layout.item_explore, container, false);
        } else {
            view = layoutInflater.inflate(R.layout.item_explore_reverse, container, false);
        }

        ImageView imageView = view.findViewById(R.id.profile_pic);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userCity = view.findViewById(R.id.user_city);

        Picasso.with(context)
                .load(users.get(position).getProfileImage())
                .into(imageView);

        String uName = "User: " + users.get(position).getName();
        String uCity = "City: " + users.get(position).getCity();
        userName.setText(uName);
        userCity.setText(uCity);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}