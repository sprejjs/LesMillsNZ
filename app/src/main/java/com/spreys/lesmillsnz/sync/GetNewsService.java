package com.spreys.lesmillsnz.sync;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.spreys.lesmillsnz.MyApp;
import com.spreys.lesmillsnz.data.DataContract.NewsArticleEntry;
import com.spreys.lesmillsnz.model.NewsArticle;
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
 *         Date: 19/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class GetNewsService extends IntentService{
    private static final String TAG = GetNewsService.class.getSimpleName();
    private static final String PACKAGE_NAME = MyApp.getAppContext().getPackageName();

    public static final String ACTION_NEWS_UPDATED = PACKAGE_NAME + ".action_news_updated";
    public static final String ACTION_UNABLE_TO_GET_NEWS = PACKAGE_NAME + ".action_unable_to_get_news";
    public static final String EXTRA_KEY_PREF_CLUB = "extra_key_pref_club";

    public GetNewsService() {
        super("GetNewsService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GetNewsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int preferredClubId = intent.getIntExtra(EXTRA_KEY_PREF_CLUB, -1);

        JSONObject newsObject = NetworkUtils.GetJsonFromUrl(UrlProvider.GetNewsUrl(preferredClubId), null);
        try {
            ArrayList<NewsArticle> newsArticles = new ArrayList<NewsArticle>();
            JSONArray gymsArray = newsObject.getJSONArray("news");

            for(int i = 0; i < gymsArray.length(); i++){
                JSONObject articleObject = gymsArray.getJSONObject(i);

                String headline = articleObject.getString("Headline");
                String content = articleObject.getString("Content");
                String base64 = articleObject.getString("Image");
                String shortDescription = articleObject.getString("ShortDescription");

                NewsArticle newsArticle = new NewsArticle(headline, content, preferredClubId);
                newsArticle.setImage(base64);
                newsArticle.setShortDescription(shortDescription);

                newsArticles.add(newsArticle);
            }

            //Delete old data
            deleteNewsForClub(preferredClubId);
            //Add new data
            addNewsToDb(newsArticles);
        } catch (JSONException e) {
            e.printStackTrace();
            sendBroadcast(ACTION_UNABLE_TO_GET_NEWS);
            return;
        }

        sendBroadcast(ACTION_NEWS_UPDATED);
    }

    /**
     * Deletes data from the data base which is older than 1 day
     */
    private void deleteNewsForClub(int clubId){
        this.getContentResolver().delete(NewsArticleEntry.CONTENT_URI,
                NewsArticleEntry.COLUMN_CLUB_ID + " = " + clubId, null);
    }

    private void addNewsToDb(List<NewsArticle> news){
        Vector<ContentValues> cVVector = new Vector<ContentValues>(news.size());

        for(NewsArticle article : news){
            ContentValues newsValues = new ContentValues();

            newsValues.put(NewsArticleEntry.COLUMN_CLUB_ID, article.getClubId());
            newsValues.put(NewsArticleEntry.COLUMN_HEADLINE, article.getHeadline());
            newsValues.put(NewsArticleEntry.COLUMN_DESC, article.getShortDescription());
            newsValues.put(NewsArticleEntry.COLUMN_CONT, article.getContent());
            newsValues.put(NewsArticleEntry.COLUMN_IMAGE, article.getBase64Image());

            cVVector.add(newsValues);
        }

        this.getContentResolver().bulkInsert(
                NewsArticleEntry.CONTENT_URI,
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
