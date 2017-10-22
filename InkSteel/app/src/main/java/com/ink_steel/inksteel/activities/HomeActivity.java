package com.ink_steel.inksteel.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        transaction.replace(R.id.view_pager, fragment);
        transaction.commit();
    }
}
