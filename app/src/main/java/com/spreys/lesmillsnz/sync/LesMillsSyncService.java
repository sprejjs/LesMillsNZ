package com.spreys.lesmillsnz.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created with Android Studio
 *
 * @author vspreys
 *         Date: 8/26/14.
 *         Project: Sunshine
 *         Contact by: vlad@spreys.com
 */
public class LesMillsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static LesMillsSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new LesMillsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
