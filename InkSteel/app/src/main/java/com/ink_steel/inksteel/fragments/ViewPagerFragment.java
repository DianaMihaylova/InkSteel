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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPagerFragment extends Fragment {

    @BindView(R.id.fragment_view_pager_tl)
    TabLayout mTabLayout;
    @BindView(R.id.fragment_view_pager_vp)
    ViewPager mViewPager;

    public ViewPagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_pager,
                container, false);
        ButterKnife.bind(this, view);

        final Fragment[] fragments = {new ProfileFragment(), new FeedFragment(),
                new ExploreFragment(), new ChatListFragment(), new StudiosFragment()};
        final String[] tabTitle = {"", getString(R.string.feed), getString(R.string.explore),
                getString(R.string.chat_fragment), getString(R.string.studios)};

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 4) {
                    fragments[position].setUserVisibleHint(true);
                }
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

        mViewPager.setCurrentItem(1);
        mViewPager.setPageTransformer(true, new ZoomInTransformer());

        mTabLayout.setupWithViewPager(mViewPager);
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) tab.setIcon(R.drawable.notification_icon);

        return view;
    }
}