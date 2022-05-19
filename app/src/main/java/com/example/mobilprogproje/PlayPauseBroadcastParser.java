package com.example.mobilprogproje;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public final class PlayPauseBroadcastParser{
    private static final String ACTION = "com.example.devicetracker.PLAY_PAUSE_BROADCAST_ACTION";

    private final PlayPauseBroadcastReceiver receiver;
    private final IntentFilter filter;

    private OnBroadcastReceivedListener listener;

    public PlayPauseBroadcastParser(Context context){
        receiver = new PlayPauseBroadcastReceiver();
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ACTION);
        context.registerReceiver(receiver, filter);
    }

    public void setOnBroadcastReceivedListener(OnBroadcastReceivedListener listener){
        this.listener = listener;
    }

    private class PlayPauseBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION)) {
                assert intent.hasExtra("play");
                if (listener != null) {
                    listener.onBroadcastReceived(intent.getBooleanExtra("play", true));
                }
            }
        }
    }

    public interface OnBroadcastReceivedListener {
        void onBroadcastReceived(boolean play);
    }
}
