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
import com.squareup.picasso.Picasso;

public class ExploreAdapter extends PagerAdapter {

    private Context context;

    public ExploreAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ExploreFragment.users.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view;
        if (position % 2 == 0) {
            view = layoutInflater.inflate(R.layout.list_explore_item, container, false);
        } else {
            view = layoutInflater.inflate(R.layout.list_explore_item_reverse, container, false);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.profile_pic);
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        TextView userCity = (TextView) view.findViewById(R.id.user_city);

        Picasso.with(context)
                .load(ExploreFragment.users.get(position).getProfileImg())
                .into(imageView);

        String uName = "User: " + ExploreFragment.users.get(position).getUserName();
        String uCity = "City: " + ExploreFragment.users.get(position).getUserCity();
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