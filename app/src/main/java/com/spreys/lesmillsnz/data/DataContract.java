package com.spreys.lesmillsnz.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 26/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class DataContract {
    public static final String CONTENT_AUTHORITY = "com.spreys.lesmillsnz.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CLASS = "class";
    public static final String PATH_CLUB = "club";
    public static final String PATH_NEWS = "news";

    /* Inner class that defines the table contents of the class table */
    public static final class ClassEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;

        public static final String TABLE_NAME = "class";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INTENSITY = "intensity";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_CLUB_ID = "club_id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_WEEK_DAY = "week_day";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_LOC = "location";

        public static Uri buildClassesForClubAndWeekDayUri(int clubId, int weekDay){
            Uri uri = Uri.withAppendedPath(CONTENT_URI, "club_id");
            uri = ContentUris.withAppendedId(uri, clubId);
            uri = Uri.withAppendedPath(uri, "weekday");
            uri = ContentUris.withAppendedId(uri, weekDay);

            return uri;
        }

        public static Uri buildClassUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getClubIdFromUri(Uri uri){
            return Integer.valueOf(uri.getPathSegments().get(2));
        }

        public static int getWeekDayFromUri(Uri uri){
            return Integer.valueOf(uri.getPathSegments().get(4));
        }
    }

    /* Inner class that defines the table contents of the Club table */
    public static final class ClubEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLUB).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CLUB;

        public static final String TABLE_NAME = "club";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LONG = "longitude";
        public static final String COLUMN_NAME = "name";

        public static Uri buildClubUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class NewsArticleEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        public static final String TABLE_NAME = "news_article";
        public static final String COLUMN_CLUB_ID ="club_id";
        public static final String COLUMN_HEADLINE ="headline";
        public static final String COLUMN_DESC ="shortDescription";
        public static final String COLUMN_CONT ="content";
        public static final String COLUMN_IMAGE ="image";

        public static Uri buildArticlesByClubId(int clubId){
            return ContentUris.withAppendedId(Uri.withAppendedPath(CONTENT_URI, "club_id"), clubId);
        }

        public static Uri buildArticleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
