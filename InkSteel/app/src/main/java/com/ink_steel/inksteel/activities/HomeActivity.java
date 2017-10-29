package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.FirebaseManager;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;
import com.ink_steel.inksteel.helpers.OnReplaceFragment;
import com.ink_steel.inksteel.model.CurrentUser;
import com.ink_steel.inksteel.model.User;

import java.util.ArrayList;

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

        User user = new User("email", "name", "sofia", "https://firebasestorage.googleapis.com/v0/b/inksteel-7911e.appspot.com/o/default.jpg?alt=media&token=2a0f4edc-81e5-40a2-9558-015e18b8b1ff");

        FirebaseFirestore.getInstance().collection("users").add(user);
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
