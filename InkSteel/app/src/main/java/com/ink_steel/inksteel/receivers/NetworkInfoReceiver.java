package com.ink_steel.inksteel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInfoReceiver extends BroadcastReceiver {

    public interface InternetConnectionListener {
        void onNetworkStateChanged(NetworkInfo info);
    }

    private InternetConnectionListener mListener;

    public NetworkInfoReceiver(InternetConnectionListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            mListener.onNetworkStateChanged(activeNetwork);
        }
    }
}
