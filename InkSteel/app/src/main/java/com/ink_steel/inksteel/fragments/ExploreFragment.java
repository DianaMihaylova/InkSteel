package com.ink_steel.inksteel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.ExploreAdapter;
import com.ink_steel.inksteel.model.User;
import com.tmall.ultraviewpager.UltraViewPager;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    public static ArrayList<User> users = new ArrayList<>();

    static {
        users.add(new User("Raya", "Sofia", R.drawable.tatto1));
        users.add(new User("Nikol", "Plovdiv", R.drawable.tatto2));
        users.add(new User("Alex", "Sofia", R.drawable.pierce1));
    }

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        Button likeBtn = (Button) view.findViewById(R.id.btn_like);
        Button unlikeBtn = (Button) view.findViewById(R.id.btn_unlike);

        UltraViewPager ultraViewPager = (UltraViewPager) view.findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.VERTICAL);
        ultraViewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAutoScroll(5000);

        ExploreAdapter adapter = new ExploreAdapter((getContext()), users);
        ultraViewPager.setAdapter(adapter);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You like it!", Toast.LENGTH_SHORT).show();
            }
        });

        unlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You not like it!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}