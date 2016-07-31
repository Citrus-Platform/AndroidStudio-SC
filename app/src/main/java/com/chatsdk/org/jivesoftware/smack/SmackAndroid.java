package com.chatsdk.org.jivesoftware.smack;

import com.chatsdk.org.jivesoftware.smackx.ConfigureProviderManager;
import com.chatsdk.org.jivesoftware.smackx.InitStaticCode;
import com.chatsdk.org.xbill.DNS.ResolverConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SmackAndroid {
    private static SmackAndroid sSmackAndroid = null;

    private BroadcastReceiver mConnectivityChangedReceiver;
    private Context mCtx;

    private SmackAndroid(Context ctx) {
        ConfigureProviderManager.configureProviderManager();
        InitStaticCode.initStaticCode(ctx);
        mConnectivityChangedReceiver = new ConnectivtyChangedReceiver();
        ctx.registerReceiver(mConnectivityChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mCtx = ctx;
    }

    public static SmackAndroid init(Context ctx) {
        if (sSmackAndroid == null) {
            sSmackAndroid = new SmackAndroid(ctx);
        }
        return sSmackAndroid;
    }

    public void exit() {
        mCtx.unregisterReceiver(mConnectivityChangedReceiver);
    }

    class ConnectivtyChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ResolverConfig.refresh();
        }

    }
}
