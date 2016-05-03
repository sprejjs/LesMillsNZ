package com.spreys.lesmillsnz.utils;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 5/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class UrlProvider {
    private static boolean productionEnv = true;

    //Development URLs
    private static final String DEV_API_URL_REGISTRATION = "http://192.168.178.20:8888/LesMillsAPI/v1/register";
    private static final String DEV_API_URL_GET_CLUBS = "http://192.168.178.20:8888/LesMillsAPI/v1/gym";
    private static final String DEV_API_URL_GET_TIMETABLE = "http://192.168.178.20:8888/LesMillsAPI/v1/timetable/";
    private static final String DEV_API_URL_GET_NEWS = "http://192.168.178.20:8888/LesMillsAPI/v1/news/";

    //Production URLs
    private static final String API_URL_REGISTRATION = "http://api.spreys.com/LesMillsAPI/v1/register";
    private static final String API_URL_GET_CLUBS = "http://api.spreys.com/LesMillsAPI/v1/gym";
    private static final String API_URL_GET_TIMETABLE = "http://api.spreys.com/LesMillsAPI/v1/timetable/";
    private static final String API_URL_GET_NEWS = "http://api.spreys.com/LesMillsAPI/v1/news/";

    /**
     * URL to get the timetable from the REST API
     * @param club_id gym id has to be supplied
     * @return String url of the REST API
     */
    public static String GetTimetableUrl(int club_id){
        if(productionEnv){
            return API_URL_GET_TIMETABLE + club_id;
        } else {
            return DEV_API_URL_GET_TIMETABLE + club_id;
        }
    }

    /**
     * URL to get the news articles from the REST API
     * @param club_id club id has to be supplied
     * @return String url of the REST API
     */
    public static String GetNewsUrl(int club_id){
        if(productionEnv){
            return API_URL_GET_NEWS + club_id;
        } else {
            return DEV_API_URL_GET_NEWS + club_id;
        }

    }

    /**
     * Registration URL address
     * @return String url of the REST API
     */
    public static String GetRegistrationUrl(){
        if(productionEnv){
            return API_URL_REGISTRATION;
        } else {
            return DEV_API_URL_REGISTRATION;
        }
    }

    /**
     * Return URL address to get the list of the clubs
     * @return String url of the REST API
     */
    public static String GetClubsUrl(){
        if(productionEnv){
            return API_URL_GET_CLUBS;
        } else {
            return DEV_API_URL_GET_CLUBS;
        }

    }
}
