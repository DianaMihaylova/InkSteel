package com.ink_steel.inksteel.activities;

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

        int position = getIntent().getIntExtra("image", 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HorizontalInfiniteCycleViewPager viewPager = findViewById(R.id.view_pager_horizontal_cycle);
        FullScreenViewAdapter fullScreenViewAdapter = new FullScreenViewAdapter(this);
        viewPager.setAdapter(fullScreenViewAdapter);
        viewPager.setCurrentItem(position);
    }
}
