package com.ink_steel.inksteel.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.activities.GalleryActivity;
import com.squareup.picasso.Picasso;

public class FullScreenViewAdapter extends PagerAdapter {

    private Context context;

    public FullScreenViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return GalleryActivity.images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.single_image_list, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.selected_image);
        Picasso.with(context)
                .load(GalleryActivity.images.get(position))
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
}