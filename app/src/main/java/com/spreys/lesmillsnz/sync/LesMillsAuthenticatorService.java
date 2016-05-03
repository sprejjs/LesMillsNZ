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
public class LesMillsAuthenticatorService extends Service {

    private LesMillsAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new LesMillsAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
