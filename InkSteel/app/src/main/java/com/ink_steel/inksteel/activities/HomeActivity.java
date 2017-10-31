package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;
import com.ink_steel.inksteel.fragments.UserInfoFragment;
import com.ink_steel.inksteel.helpers.OnReplaceFragment;

public class HomeActivity extends Activity implements OnReplaceFragment {

    public static String userEmail;
    private FragmentManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onCreate");

        mManager = getFragmentManager();
        if (getIntent().getBooleanExtra(LoginActivity.IS_NEW_USER, false)) {
            displayFragment(new UserInfoFragment());
        } else {
            displayFragment(new ScreenSlidePageFragment());
        }

    }

    private void displayFragment(Fragment fragment) {
        mManager.beginTransaction()
                .replace(R.id.home_activity_fragments_placeholder, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        displayFragment(fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onStop");
//        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d("Lifecycle", this.getClass().getSimpleName() + " onDestroy");
        super.onDestroy();
    }
}
