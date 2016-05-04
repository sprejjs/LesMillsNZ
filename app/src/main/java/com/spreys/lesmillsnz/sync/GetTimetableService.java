package com.spreys.lesmillsnz.sync;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.spreys.lesmillsnz.MyApp;
import com.spreys.lesmillsnz.data.DataContract.*;
import com.spreys.lesmillsnz.model.Class;
import com.spreys.lesmillsnz.utils.NetworkUtils;
import com.spreys.lesmillsnz.utils.UrlProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 8/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class GetTimetableService extends IntentService {
    private static final String TAG = GetTimetableService.class.getSimpleName();
    private static final String PACKAGE_NAME = MyApp.getAppContext().getPackageName();

    public static final String EXTRA_KEY_PREF_CLUB = "extra_key_pref_club";

    public static final String ACTION_UNABLE_TO_GET_CLASSES = PACKAGE_NAME + ".action_cant_get_classes";
    public static final String ACTION_CLASSES_UPDATED = PACKAGE_NAME + ".action_classes_updated";

    /**
     * Default service constructor
     */
    public GetTimetableService(){
        super("GetTimetableService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GetTimetableService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int preferredClubId = intent.getIntExtra(EXTRA_KEY_PREF_CLUB, -1);

        if(preferredClubId == -1){
            throw new IllegalArgumentException("Preferred club is not supplied in the intent");
        }

        JSONObject timetableObject = NetworkUtils.GetJsonFromUrl(
                UrlProvider.GetTimetableUrl(preferredClubId),
                null
        );

        try {
            ArrayList<Class> classes = new ArrayList<Class>();
            JSONArray timeTableArray = timetableObject.getJSONArray("classes");
            for(int i = 0; i < timeTableArray.length(); i++){
                JSONObject classObject = timeTableArray.getJSONObject(i);

                Time time = null;
                String name = classObject.getString("Name");
                String weekDay = classObject.getString("WeekDay");
                String duration = classObject.getString("Duration");
                String location = classObject.getString("Location");
                String intensity = classObject.getString("Intensity");
                String description = classObject.getString("Description");

                try {
                    String strTime = classObject.getString("Time");
                    DateFormat formatter = new SimpleDateFormat("h:mm a");
                    time = new java.sql.Time(formatter.parse(strTime).getTime());
                } catch (ParseException ex){
                    Log.e(TAG, "Unable to parse the Time. Ex = " + ex);
                }

                Class newClass = new Class(name, weekDay, time, preferredClubId);
                newClass.setDuration(duration);
                newClass.setLocation(location);
                newClass.setIntensity(intensity);
                newClass.setDescription(description);

                classes.add(newClass);
            }
            //Remove old timetable
            deleteTimetableForClub(preferredClubId);
            //Add new timetable
            addTimetableToDb(classes);
        } catch (JSONException e) {
            e.printStackTrace();
            sendBroadcast(ACTION_UNABLE_TO_GET_CLASSES);
            return;
        } catch (NullPointerException e){
            e.printStackTrace();
            sendBroadcast(ACTION_UNABLE_TO_GET_CLASSES);
            return;
        }

        sendBroadcast(ACTION_CLASSES_UPDATED);
    }

    /**
     * Removes all classes associated with the supplied club id
     * @param club_id the id of a club
     */
    private void deleteTimetableForClub(int club_id){
        this.getContentResolver().delete(
                ClassEntry.CONTENT_URI,
                ClassEntry.COLUMN_CLUB_ID + " = ?",
                new String[]{String.valueOf(club_id)});
    }

    private void addTimetableToDb(List<Class> classes){
        Vector<ContentValues> cVVector = new Vector<ContentValues>(classes.size());

        for(Class temp_class : classes){
            ContentValues classValues = new ContentValues();

            classValues.put(ClassEntry.COLUMN_NAME, temp_class.getName());
            classValues.put(ClassEntry.COLUMN_INTENSITY, temp_class.getIntensity());
            classValues.put(ClassEntry.COLUMN_DURATION, temp_class.getDuration());
            classValues.put(ClassEntry.COLUMN_CLUB_ID, temp_class.getClubId());
            classValues.put(ClassEntry.COLUMN_IMAGE, temp_class.getImage());
            classValues.put(ClassEntry.COLUMN_WEEK_DAY, temp_class.getWeekDay());
            classValues.put(ClassEntry.COLUMN_TIME, temp_class.getTime().toString());
            classValues.put(ClassEntry.COLUMN_DESC, temp_class.getDescription());
            classValues.put(ClassEntry.COLUMN_LOC, temp_class.getLocation());

            cVVector.add(classValues);
        }

        this.getContentResolver().bulkInsert(
                ClassEntry.CONTENT_URI,
                cVVector.toArray(new ContentValues[cVVector.size()])
        );
    }

    protected void sendBroadcast(String action) {
        Log.d(TAG, "sending broadcast, action = " + action);
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(action);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pending.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(action));
    }
}
