package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.FirebaseManager;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;
import com.ink_steel.inksteel.helpers.OnReplaceFragment;
import com.ink_steel.inksteel.model.CurrentUser;

public class HomeActivity extends Activity implements OnReplaceFragment, FirebaseManager.UserListener {

    public static String userEmail, userName, userImageUrl;
    private FragmentManager mManager;
    private FirebaseManager.UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        CurrentUser.getInstance();
        mManager = getFragmentManager();
        displayFragment(new ScreenSlidePageFragment());

        mUserManager = FirebaseManager.getInstance()
                .getUserManager(this);
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
    public void onUserLoaded() {
        userEmail = mUserManager.getUserEmail();
        userName = mUserManager.getUserName();
        userImageUrl = mUserManager.getUserProfilePicture();
    }
}
