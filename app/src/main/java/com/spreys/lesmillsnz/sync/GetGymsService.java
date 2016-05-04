package com.spreys.lesmillsnz.sync;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.spreys.lesmillsnz.MyApp;
import com.spreys.lesmillsnz.data.DataContract.ClubEntry;
import com.spreys.lesmillsnz.model.Club;
import com.spreys.lesmillsnz.utils.NetworkUtils;
import com.spreys.lesmillsnz.utils.UrlProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 28/09/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class GetGymsService extends IntentService {
    private static final String PACKAGE_NAME = MyApp.getAppContext().getPackageName();
    private static final String TAG = GetGymsService.class.getSimpleName();

    public static final String ACTION_GYMS_UPDATED = PACKAGE_NAME + ".action_gyms_updated";
    public static final String ACTION_UNABLE_TO_GET_GYMS = PACKAGE_NAME + ".action_unable_to_get_gyms";

    public GetGymsService() {
        super("GetGymsService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GetGymsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        JSONObject gymsObject = NetworkUtils.GetJsonFromUrl(UrlProvider.GetClubsUrl(), null);
        try {
            ArrayList<Club> clubs = new ArrayList<Club>();
            JSONArray gymsArray = gymsObject.getJSONArray("gyms");
            for(int i = 0; i < gymsArray.length(); i++){
                JSONObject gymObject = gymsArray.getJSONObject(i);

                String name = gymObject.getString("Name");
                int id = gymObject.getInt("ID");
                double lat = gymObject.getDouble("Latitude");
                double longitude = gymObject.getDouble("Longitude");

                clubs.add(new Club(id, lat, longitude, name));
            }
            addClubsToDb(clubs);
        } catch (JSONException e) {
            e.printStackTrace();
            sendBroadcast(ACTION_UNABLE_TO_GET_GYMS);
            return;
        } catch (NullPointerException e){
            e.printStackTrace();
            sendBroadcast(ACTION_UNABLE_TO_GET_GYMS);
            return;
        }

        sendBroadcast(ACTION_GYMS_UPDATED);
    }

    protected void addClubsToDb(List<Club> clubs){
        Vector<ContentValues> cVVector = new Vector<ContentValues>(clubs.size());

        for(Club club : clubs){
            ContentValues clubValues = new ContentValues();

            clubValues.put(ClubEntry.COLUMN_LAT, club.getLatitude());
            clubValues.put(ClubEntry.COLUMN_LONG, club.getLongitude());
            clubValues.put(ClubEntry.COLUMN_NAME, club.getName());
            clubValues.put(ClubEntry._ID, club.getId());

            cVVector.add(clubValues);
        }

        this.getContentResolver().bulkInsert(
                ClubEntry.CONTENT_URI,
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
