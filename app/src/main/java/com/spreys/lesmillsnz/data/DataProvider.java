package com.spreys.lesmillsnz.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.spreys.lesmillsnz.data.DataContract.ClassEntry;
import com.spreys.lesmillsnz.data.DataContract.ClubEntry;
import com.spreys.lesmillsnz.data.DataContract.NewsArticleEntry;
import com.spreys.lesmillsnz.utils.Utils;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 26/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class DataProvider extends ContentProvider {
    private static final String TAG = DataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    private static final int CLASS = 100;
    private static final int CLASSES_IN_CLUB = 101;
    private static final int CLASSES_IN_CLUB_FOR_DAY = 102;
    private static final int CLUB = 200;
    private static final int NEWS = 300;
    private static final int NEWS_ID = 301;
    private static final int NEWS_IN_CLUB = 302;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DataContract.PATH_CLUB, CLUB);
        matcher.addURI(authority, DataContract.PATH_CLASS, CLASS);
        matcher.addURI(authority, DataContract.PATH_CLASS + "/club_id/*", CLASSES_IN_CLUB);
        matcher.addURI(authority, DataContract.PATH_CLASS + "/club_id/*/weekday/*", CLASSES_IN_CLUB_FOR_DAY);
        matcher.addURI(authority, DataContract.PATH_NEWS, NEWS);
        matcher.addURI(authority, DataContract.PATH_NEWS + "/club_id/*", NEWS_IN_CLUB);
        matcher.addURI(authority, DataContract.PATH_NEWS + "/*", NEWS_ID);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case CLASS:
                retCursor = mDbHelper.getReadableDatabase().query(
                        ClassEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CLASSES_IN_CLUB_FOR_DAY:
                String sel = ClassEntry.COLUMN_CLUB_ID + " = ? AND " +
                        ClassEntry.COLUMN_WEEK_DAY + " = ?";

                String clubId = String.valueOf(ClassEntry.getClubIdFromUri(uri));
                String weekDay = Utils.DayOfTheWeekFromInt(ClassEntry.getWeekDayFromUri(uri));

                retCursor =  mDbHelper.getReadableDatabase().query(
                        ClassEntry.TABLE_NAME,
                        projection,
                        sel,
                        new String[]{clubId, weekDay},
                        null,
                        null,
                        sortOrder
                );
                break;
            case CLASSES_IN_CLUB:
                retCursor =  mDbHelper.getReadableDatabase().query(
                        ClassEntry.TABLE_NAME,
                        projection,
                        ClassEntry.COLUMN_CLUB_ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CLUB:
                retCursor = mDbHelper.getReadableDatabase().query(
                        ClubEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case NEWS:
                retCursor = mDbHelper.getReadableDatabase().query(
                        NewsArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case NEWS_ID:
                retCursor =  mDbHelper.getReadableDatabase().query(
                        NewsArticleEntry.TABLE_NAME,
                        projection,
                        NewsArticleEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case NEWS_IN_CLUB:
                retCursor = mDbHelper.getReadableDatabase().query(
                        NewsArticleEntry.TABLE_NAME,
                        projection,
                        NewsArticleEntry.COLUMN_CLUB_ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CLUB:
                return ClubEntry.CONTENT_TYPE;
            case CLASS:
                return ClassEntry.CONTENT_TYPE;
            case NEWS:
                return NewsArticleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case CLUB: {
                try{
                    long _id = db.insert(ClubEntry.TABLE_NAME, null, values);
                    if (_id > 0)
                        returnUri = ClubEntry.buildClubUri(_id);
                } catch (SQLiteConstraintException ex){
                    Log.d(TAG, "Unable to add a new club, club already exist.");
                }
                break;
            }
            case NEWS: {
                long _id = db.insert(NewsArticleEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = NewsArticleEntry.buildArticleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CLASS: {
                long _id = db.insert(ClassEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ClassEntry.buildClassUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case CLASS:
                rowsDeleted = db.delete(ClassEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CLUB:
                rowsDeleted = db.delete(ClubEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NEWS:
                rowsDeleted = db.delete(NewsArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
