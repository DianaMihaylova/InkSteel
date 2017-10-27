package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;
import com.ink_steel.inksteel.helpers.OnPostClickListener;
import com.ink_steel.inksteel.helpers.OnReplaceFragment;
import com.ink_steel.inksteel.model.CurrentUser;

public class HomeActivity extends Activity implements OnReplaceFragment {

    private FragmentManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        CurrentUser.getInstance();
        mManager = getFragmentManager();
        displayFragment(new ScreenSlidePageFragment());

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
}
