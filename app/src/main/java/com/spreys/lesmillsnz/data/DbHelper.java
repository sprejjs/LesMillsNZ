package com.spreys.lesmillsnz.data;

import com.spreys.lesmillsnz.data.DataContract.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 26/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "lesmillsnz.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CLASS_TABLE = "CREATE TABLE " + ClassEntry.TABLE_NAME +
                " (" +
                ClassEntry._ID + " INTEGER PRIMARY KEY," +
                ClassEntry.COLUMN_CLUB_ID + " INTEGER," +
                ClassEntry.COLUMN_DESC + " TEXT, " +
                ClassEntry.COLUMN_DURATION + " TEXT, " +
                ClassEntry.COLUMN_IMAGE + " TEXT, " +
                ClassEntry.COLUMN_INTENSITY + " TEXT, " +
                ClassEntry.COLUMN_LOC + " TEXT, " +
                ClassEntry.COLUMN_TIME + " TEXT, " +
                ClassEntry.COLUMN_WEEK_DAY + " TEXT, " +
                ClassEntry.COLUMN_NAME + " TEXT " +
                ");";

        final String SQL_CREATE_CLUB_TABLE = "CREATE TABLE " + ClubEntry.TABLE_NAME +
                " (" +
                ClubEntry._ID + " INTEGER PRIMARY KEY, " +
                ClubEntry.COLUMN_LAT + " TEXT, " +
                ClubEntry.COLUMN_LONG + " TEXT, " +
                ClubEntry.COLUMN_NAME + " TEXT " +
                ");";

        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsArticleEntry.TABLE_NAME +
                " (" +
                NewsArticleEntry._ID + " INTEGER PRIMARY KEY, " +
                NewsArticleEntry.COLUMN_CLUB_ID + " INTEGER, " +
                NewsArticleEntry.COLUMN_HEADLINE + " TEXT, " +
                NewsArticleEntry.COLUMN_DESC + " TEXT, " +
                NewsArticleEntry.COLUMN_CONT + " TEXT, " +
                NewsArticleEntry.COLUMN_IMAGE + " TEXT " +
                ");";


        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLUB_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClubEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsArticleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
