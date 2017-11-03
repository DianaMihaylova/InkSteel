package com.ink_steel.inksteel.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.ink_steel.inksteel.R;

public class ScreenSlidePageFragment extends Fragment {

    public ScreenSlidePageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_screen_slide_page,
                container, false);

        final Fragment[] fragments = {new ProfileFragment(), new FeedFragment(),
                new ExploreFragment(), new ChatListFragment(), new StudiosFragment()};
        final String[] tabTitle = {"", "FEED", "EXPLORE", "CHAT", "STUDIOS"};

        final ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitle[position];
            }
        });

        viewPager.setCurrentItem(1);
        viewPager.setPageTransformer(true, new ZoomInTransformer());

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.profile);

        return view;
    }
}