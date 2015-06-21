package com.ap.steven.digipolis.com.steven.digipolis.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ap.steven.digipolis.NetworkHandler;

/**
 * Created by Steven on 3/6/2015.
 * syncadapter logic
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {


    ContentResolver mContentResolver;
    /**
     * Set up the sync adapter
     */

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        Log.e("sync","sync");
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e("sync","perform Sync");
        NetworkHandler handler = NetworkHandler.getNetworkHandler();
        handler.getData();
/*
        try {
            Thread.sleep(10000);

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
*/


    }
}
