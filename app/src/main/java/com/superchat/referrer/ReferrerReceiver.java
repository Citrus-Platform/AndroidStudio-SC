package com.superchat.referrer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.superchat.utils.Log;

/**
 * Created by Mahesh on 19/01/17.
 */

public class ReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action != null && TextUtils.equals(action, "com.android.vending.INSTALL_REFERRER")) {
            try {
                final String referrer = intent.getStringExtra("referrer");

                // Parse parameters
                String[] params = referrer.split("&");
                for (String p : params) {
                    if (p.startsWith("utm_content=")) {
                        final String content = p.substring("utm_content=".length());

                        /**
                         * USE HERE YOUR CONTENT (i.e. configure the app based on the link the user clicked)
                         */
                        System.out.println("ReferrerReceiver :: "+content);

                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * OPTIONAL: Forward the intent to Google Analytics V2 receiver
             */
            // new com.google.analytics.tracking.android.AnalyticsReceiver().onReceive(context, intent);
        }

    }
}
