package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FullScreenViewAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mGallery;

    public FullScreenViewAdapter(Context context, ArrayList<String> gallery) {
        mContext = context;
        mGallery = gallery;
    }

    @Override
    public int getCount() {
        return mGallery.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_image_fullscreen, container, false);

        ImageView imageView = view.findViewById(R.id.selected_image);

        Picasso.with(mContext)
                .load(mGallery.get(position))
                .into(imageView);

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