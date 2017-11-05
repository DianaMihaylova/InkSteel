package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;
import com.ink_steel.inksteel.fragments.UserInfoFragment;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragmentListener;
import com.ink_steel.inksteel.helpers.NetworkService;
import com.ink_steel.inksteel.helpers.PermissionUtil;
import com.ink_steel.inksteel.model.ChatRoom;

public class HomeActivity extends Activity implements OnReplaceFragmentListener, DatabaseManager.UserChatRoomsListener {

    private FragmentManager mManager;
    private DatabaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        manager = DatabaseManager.getInstance();
        manager.setActivity(this);
        manager.getUserChatRooms(this);
        startService(new Intent(this, NetworkService.class));

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
    public void onChatRoomsLoaded() {
        manager.unregisterChatRoomsListener();
    }

    @Override
    public void onChatRoomChanged(ChatRoom a) {

    }
}