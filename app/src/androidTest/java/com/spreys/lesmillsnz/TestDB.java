package com.spreys.lesmillsnz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.spreys.lesmillsnz.data.DataContract.*;
import com.spreys.lesmillsnz.data.DbHelper;
import com.spreys.lesmillsnz.model.*;
import com.spreys.lesmillsnz.model.Class;

import java.sql.Time;
import java.util.Map;
import java.util.Set;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 26/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class TestDB extends AndroidTestCase {
    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        deleteAllRecords();
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Test class
        runTestOnTable(ClassEntry.TABLE_NAME, createClassValues(), db);
        runTestOnTable(NewsArticleEntry.TABLE_NAME, createArticleValues(), db);
        runTestOnTable(ClubEntry.TABLE_NAME, createClubValues(), db);

        deleteAllRecords();
        dbHelper.close();
    }

    public void testInsertReadProvider() {
        deleteAllRecords();
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        runTestOnTable(NewsArticleEntry.TABLE_NAME, NewsArticleEntry.CONTENT_URI, createArticleValues(), db);
        runTestOnTable(ClubEntry.TABLE_NAME, ClubEntry.CONTENT_URI, createClubValues(), db);
        runTestOnTable(ClassEntry.TABLE_NAME, ClassEntry.CONTENT_URI, createClassValues(), db);

        deleteAllRecords();
        dbHelper.close();
    }

    private void runTestOnTable(String tableName, ContentValues testValues, SQLiteDatabase db){
        long rowId = db.insert(tableName, null, testValues);
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);
        Cursor cursor = db.query(
                tableName,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        validateCursor(cursor, testValues);
    }

    private void runTestOnTable(String tableName, Uri tableUri, ContentValues testValues, SQLiteDatabase db){
        long rowId = db.insert(tableName, null, testValues);
        assertTrue(rowId != -1);

        Cursor weatherCursor = mContext.getContentResolver().query(
                tableUri,
                null,
                null,
                null,
                null
        );

        validateCursor(weatherCursor, testValues);
    }

    static ContentValues createClassValues() {
        String intensity = "High";
        String duration = "45 Minutes";
        int clubId = 1;
        String name = "Bodypump";
        String image = "null";
        String weekDay = "Monday";
        Time time = new Time(1231231231);
        String description = "The most awesome class ever created.";
        String location = "Ground floor";

        Class temp_class = new Class(name, weekDay, time, clubId);
        temp_class.setIntensity(intensity);
        temp_class.setDuration(duration);
        temp_class.setImage(image);
        temp_class.setDescription(description);
        temp_class.setLocation(location);

        ContentValues classValues = new ContentValues();
        classValues.put(ClassEntry._ID, 1);
        classValues.put(ClassEntry.COLUMN_INTENSITY, temp_class.getIntensity());
        classValues.put(ClassEntry.COLUMN_DURATION, temp_class.getDuration());
        classValues.put(ClassEntry.COLUMN_CLUB_ID, temp_class.getClubId());
        classValues.put(ClassEntry.COLUMN_NAME, temp_class.getName());
        classValues.put(ClassEntry.COLUMN_IMAGE, temp_class.getImage());
        classValues.put(ClassEntry.COLUMN_WEEK_DAY, temp_class.getWeekDay());
        classValues.put(ClassEntry.COLUMN_TIME, temp_class.getTime().toString());
        classValues.put(ClassEntry.COLUMN_DESC, temp_class.getDescription());
        classValues.put(ClassEntry.COLUMN_LOC, temp_class.getLocation());

        return classValues;
    }

    static ContentValues createArticleValues(){
        NewsArticle temp_article = new NewsArticle("New class in the gym", "Very good class, " +
                "everybody has to join", 1);

        temp_article.setShortDescription("Have a look inside");
        temp_article.setImage("base64 image will go inside");

        ContentValues articleValues = new ContentValues();
        articleValues.put(NewsArticleEntry._ID, 1);
        articleValues.put(NewsArticleEntry.COLUMN_CLUB_ID, temp_article.getClubId());
        articleValues.put(NewsArticleEntry.COLUMN_HEADLINE, temp_article.getHeadline());
        articleValues.put(NewsArticleEntry.COLUMN_CONT, temp_article.getContent());
        articleValues.put(NewsArticleEntry.COLUMN_DESC, temp_article.getShortDescription());

        return articleValues;
    }

    static ContentValues createClubValues(){
        Club club = new Club(1, 15.00, -10.00, "Britomart");
        ContentValues clubValues = new ContentValues();
        clubValues.put(ClubEntry._ID, club.getId());
        clubValues.put(ClubEntry.COLUMN_LAT, club.getLatitude());
        clubValues.put(ClubEntry.COLUMN_LONG, club.getLongitude());
        clubValues.put(ClubEntry.COLUMN_NAME, club.getName());
        return clubValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    private void deleteAllRecords(){
        mContext.getContentResolver().delete(
                ClassEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                ClubEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                NewsArticleEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ClassEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ClubEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                NewsArticleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
}
