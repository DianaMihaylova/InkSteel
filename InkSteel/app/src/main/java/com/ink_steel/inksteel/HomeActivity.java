package com.ink_steel.inksteel;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
