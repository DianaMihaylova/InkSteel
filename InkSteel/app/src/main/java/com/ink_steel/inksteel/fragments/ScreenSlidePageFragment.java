package com.ink_steel.inksteel.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ink_steel.inksteel.R;

public class ScreenSlidePageFragment extends Fragment {

    public static final int FRAGMENT_TABS = 4;
    private ProfileFragment profileFragment;
    private FeedFragment feedFragment;
    private ExploreFragment exploreFragment;
    private ContactStudioFragment contactStudioFragment;

    public ScreenSlidePageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        profileFragment = new ProfileFragment();
        feedFragment = new FeedFragment();
        exploreFragment = new ExploreFragment();
        contactStudioFragment = new ContactStudioFragment();

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment tab = null;

                switch (position) {
                    case 0:
                        tab = profileFragment;
                        break;
                    case 1:
                        tab = feedFragment;
                        break;
                    case 2:
                        tab = exploreFragment;
                        break;
                    case 3:
                        tab = contactStudioFragment;
                        break;
                }
                return tab;
            }

            @Override
            public int getCount() {
                return FRAGMENT_TABS;
            }


            @Override
            public CharSequence getPageTitle(int position) {
                String title = "";
                switch (position) {
                    case 0:
                        title = "";
                        break;
                    case 1:
                        title = "Feed";
                        break;
                    case 2:
                        title = "Explore";
                        break;
                    case 3:
                        title = "Studios contacts";
                        break;
                }
                return title;
            }
        });

        viewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.profile);

        return view;
    }
}
