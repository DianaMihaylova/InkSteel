package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.fragments.ScreenSlidePageFragment;
import com.ink_steel.inksteel.fragments.UserInfoFragment;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragmentListener;
import com.ink_steel.inksteel.receivers.NetworkReceiver;
import com.ink_steel.inksteel.services.ChatNotificationService;
import com.ink_steel.inksteel.model.ChatRoom;

public class HomeActivity extends Activity implements OnReplaceFragmentListener, DatabaseManager.UserChatRoomsListener, NetworkReceiver.InternetConnectionListener {

    private FragmentManager mFragmentManager;
    private DatabaseManager mDatabaseManager;
    private BroadcastReceiver mReceiver;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mReceiver = new NetworkReceiver(this);
        View view = findViewById(R.id.activity_home_container);
        mSnackbar = Snackbar.make(view, "No internet connection",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("CONNECT", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        mDatabaseManager = DatabaseManager.getInstance();
        mDatabaseManager.getUserChatRooms(this);
        startService(new Intent(this, ChatNotificationService.class));

        mFragmentManager = getFragmentManager();
        if (getIntent().getBooleanExtra(LoginActivity.IS_NEW_USER, false)) {
            displayFragment(new UserInfoFragment());
        } else {
            displayFragment(new ScreenSlidePageFragment());
        }
    }

    private void displayFragment(Fragment fragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.home_activity_fragments_placeholder, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        displayFragment(fragment);
    }

    @Override
    public void onChatRoomsLoaded() {
        mDatabaseManager.unregisterChatRoomsListener();
    }

    @Override
    public void onChatRoomChanged(ChatRoom a) {

    }

    @Override
    public void onNetworkStateChanged(NetworkInfo info) {

        boolean isConnected = info != null &&
                info.isConnectedOrConnecting();
        if (!isConnected) {
            mSnackbar.show();
        } else {
            mSnackbar.dismiss();
        }
    }
}