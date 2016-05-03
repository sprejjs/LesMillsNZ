package com.spreys.lesmillsnz;

import android.app.Application;
import android.content.Context;

import com.spreys.lesmillsnz.model.Class;
import com.spreys.lesmillsnz.model.NewsArticle;
import com.spreys.lesmillsnz.utils.Utils;

import java.util.ArrayList;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 5/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class MyApp extends Application {
    private static MyApp appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    /**
     * Returns application context
     * @return application context
     */
    public static Context getAppContext(){
        return appContext;
    }
}
