package com.spreys.lesmillsnz.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 5/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class PreferenceProvider {
    private static final String KEY_PREFERRED_CLUB = "key_preferred_club";
    private static final String KEY_EMAIL = "key_email_address";
    private static final String KEY_OPENED_PREVIOUSLY = "key_opened_previously";

    /**
     * Saves club selected by the user into the shared preferences
     * @param clubId club id. Has to be a positive number
     * @param ctx application context
     */
    public static void SavePreferredClub(int clubId, Context ctx){

        if(clubId < 1){
            throw new IllegalArgumentException("Invalid clubId supplied. ID " + clubId);
        }

        PreferenceProvider.SavePreference(KEY_PREFERRED_CLUB, clubId, ctx);
    }

    /**
     * Method checks if a "First Time" boolean key has been previously set.
     * If it is not stored, then it saves "First Time" key as a side affect and returns YES.
     * Returns False if has previously been saved.
     *
     * In other words it will return YES only if method is called for the very first time on this
     * device
     * @param ctx Application context
     * @return "First Time" boolean key
     */
    public static boolean IsOpenedFirstTime(Context ctx){
        boolean first_time = !PreferenceProvider.GetPreferredBoolean(KEY_OPENED_PREVIOUSLY, ctx);

        if(first_time){
            PreferenceProvider.SavePreference(KEY_OPENED_PREVIOUSLY, true, ctx);
        }

        return first_time;
    }

    /**
     * Returns the id of the club preferred by the user. Returns 1 if user has no preferred ID.
     * @param ctx application context
     * @return int preferred gym id
     */
    public static int GetPreferredClub(Context ctx){
        int prf_club = PreferenceProvider.GetPreferredInt(KEY_PREFERRED_CLUB, ctx);
        return prf_club != -1 ? prf_club : 1;
    }

    /**
     * Saves email address to the shared preferences
     * @param email email address
     * @param ctx application context
     */
    public static void SaveEmailAddress(String email, Context ctx){
        if (email == null || email.equals("")){
            throw new IllegalArgumentException("Invalid email supplied. Email " + email);
        }

        PreferenceProvider.SavePreference(KEY_EMAIL, email, ctx);
    }

    /**
     * Returns email address from the shared preferences
     * @param ctx application context
     * @return email address from the shared preferences
     */
    public static String RetrieveEmailAddress(Context ctx){
        return PreferenceProvider.GetPreferredString(KEY_EMAIL, ctx);
    }

    /**
     * Save specified key and value into the preference list
     * @param key reference key
     * @param parameter reference parameter
     */
    private static void SavePreference(String key, int parameter, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, parameter).apply();
    }

    /**
     * Save specified key and value into the preference list
     * @param key reference key
     * @param parameter reference parameter
     */
    private static void SavePreference(String key, boolean parameter, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, parameter).apply();
    }

    /**
     * Save specified key and value into the preference list
     * @param key reference key
     * @param parameter reference parameter
     */
    private static void SavePreference(String key, String parameter, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, parameter).apply();
    }

    /**
     * Returns int from the shared preferences. If preference isn't set, returns -1.
     * @param key shared preferences key
     * @param ctx application context
     * @return Int from the shared preferences.
     */
    private static int GetPreferredInt(String key, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getInt(key, -1);
    }

    /**
     * Returns String from the shared preferences. If preference isn't set, returns null.
     * @param key shared preferences key
     * @param ctx application context
     * @return String from the shared preferences.
     */
    private static String GetPreferredString(String key, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, null);
    }

    /**
     * Returns a Boolean from the shared preferences. If preference isn't set, returns False.
     * @param key shared preferences key
     * @param ctx application context
     * @return Boolean as saved in the shared preferences or False
     */
    private static boolean GetPreferredBoolean(String key, Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(key, false);
    }
}
