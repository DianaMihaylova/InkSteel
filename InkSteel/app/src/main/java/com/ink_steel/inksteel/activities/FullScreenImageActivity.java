package com.ink_steel.inksteel.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.FullScreenViewAdapter;

public class FullScreenImageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FullScreenViewAdapter fullScreenViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fullScreenViewAdapter = new FullScreenViewAdapter(this);
        viewPager.setAdapter(fullScreenViewAdapter);
    }
}
