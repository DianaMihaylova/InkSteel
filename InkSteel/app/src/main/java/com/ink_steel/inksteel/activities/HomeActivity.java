package com.ink_steel.inksteel.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.data.DatabaseManager;
import com.ink_steel.inksteel.fragments.UserInfoFragment;
import com.ink_steel.inksteel.fragments.ViewPagerFragment;
import com.ink_steel.inksteel.helpers.Listeners;
import com.ink_steel.inksteel.helpers.Listeners.OnReplaceFragmentListener;
import com.ink_steel.inksteel.receivers.NetworkInfoReceiver;
import com.ink_steel.inksteel.services.ChatNotificationService;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ink_steel.inksteel.activities.LoginActivity.IS_NEW_USER;

public class HomeActivity extends Activity implements OnReplaceFragmentListener,
        NetworkInfoReceiver.InternetConnectionListener, DatabaseManager.OnUserInfoLoadedListener,
        Listeners.ShowSnackBarListener {

    @BindView(R.id.activity_home_container)
    View mContainer;
    @BindView(R.id.activity_home_pb)
    ProgressBar mProgressBar;

    private FragmentManager mFragmentManager;
    private BroadcastReceiver mReceiver;
    private Snackbar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        DatabaseManager.UserManager manager2 = DatabaseManager.getUserManager();
        if (!manager2.isUserSignedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            Log.d("posts", "opening logn activity");
//            finish();
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(IS_NEW_USER)
                    && intent.getBooleanExtra(IS_NEW_USER, false)) {
                mProgressBar.setVisibility(View.GONE);
                displayFragment(UserInfoFragment.newInstance(true));
                Log.d("posts", "is new user");
            } else {
                manager2.loadUserInfo(this);
                startService(new Intent(this, ChatNotificationService.class));
                mReceiver = new NetworkInfoReceiver(this);
                Log.d("posts", "not new user");
            }
        }
    }

    private void displayFragment(Fragment fragment) {
        if (mFragmentManager == null)
            mFragmentManager = getFragmentManager();

        Log.d("posts", "opening" + fragment.getClass().getSimpleName());
        mFragmentManager.beginTransaction()
                .replace(R.id.activity_home_fragment_placeholder, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        displayFragment(fragment);
    }


    @Override
    public void onNetworkStateChanged(NetworkInfo info) {
        if (mSnackBar == null)
            initSnackBar();
        if (info != null && !info.isConnectedOrConnecting()) {
            mSnackBar.show();
        } else {
            mSnackBar.dismiss();
        }
    }

    private void initSnackBar() {
        mSnackBar = Snackbar.make(mContainer, "No internet connection",
                Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void onUserInfoLoaded() {
        mProgressBar.setVisibility(View.GONE);
        displayFragment(new ViewPagerFragment());
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showSnackBar(String message) {
        if (mSnackBar == null)
            initSnackBar();
        if (message.equals("No internet connection"))
            mSnackBar.setAction("CONNECT", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                }
            });
        mSnackBar.setText(message).show();
    }

    @Override
    public void dismissSnackBar() {
        mSnackBar.dismiss();
    }
}
