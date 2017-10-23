package com.ink_steel.inksteel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.FullScreenViewAdapter;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        int position = 0;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("image")) {
            position = intent.getIntExtra("image", 0);
        }

        HorizontalInfiniteCycleViewPager viewPager = (HorizontalInfiniteCycleViewPager)
                findViewById(R.id.view_pager_horizontal_cycle);
        FullScreenViewAdapter fullScreenViewAdapter = new FullScreenViewAdapter(this);
        viewPager.setAdapter(fullScreenViewAdapter);
        viewPager.setCurrentItem(position);
    }
}
