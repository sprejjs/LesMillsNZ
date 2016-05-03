package com.spreys.lesmillsnz.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.Window;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 7/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class UiUtils {
    private static ProgressDialog mySpinnerDialog;

    /**
     * Getter for the spinner dialog. Includes lazy initialisation
     * @param activity reference to the calling activity
     * @return static version of the progress dialog
     */
    private static Dialog getSpinnerDialog(Activity activity){
        if (mySpinnerDialog == null){
            mySpinnerDialog = new ProgressDialog(activity);
            mySpinnerDialog.getWindow().getCurrentFocus();
            mySpinnerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mySpinnerDialog.setCancelable(false);
            mySpinnerDialog.setOwnerActivity(activity);
        }

        return mySpinnerDialog;
    }

    /**
     * Displays a progress dialog with a loading spinner
     * @param activity reference to the calling activity
     */
    public static void ShowLoadingSpinner(Activity activity, String title, String message){
        getSpinnerDialog(activity).show();

        mySpinnerDialog.setTitle(title);
        mySpinnerDialog.setMessage(message);
    }

    /**
     * Hides a progress dialog with a loading spinner
     * @param activity reference to the calling activity
     */
    public static void HideLoadingSpinner(Activity activity){
        getSpinnerDialog(activity).dismiss();
    }
}
