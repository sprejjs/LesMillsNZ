package com.spreys.lesmillsnz.utils;

import com.spreys.lesmillsnz.model.Class;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 12/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class Utils {

    /**
     * Returns the day of the week as String
     * @param dayOfTheWeek day of the week as integer, where 1 is Sunday and 7 is Saturday
     *                     respectively
     * @return String representation of the day of the week.
     */
    public static String DayOfTheWeekFromInt(int dayOfTheWeek){

        if(dayOfTheWeek < 1 || dayOfTheWeek > 7) {
            throw  new IllegalArgumentException("dayOfTheWeek has to be between 1 and 7. " +
                    "Supplied dayOfTheWeek " + dayOfTheWeek);
        }

        String[] daysOfTheWeek = new String[8];
        daysOfTheWeek[1] = "Sunday";
        daysOfTheWeek[2] = "Monday";
        daysOfTheWeek[3] = "Tuesday";
        daysOfTheWeek[4] = "Wednesday";
        daysOfTheWeek[5] = "Thursday";
        daysOfTheWeek[6] = "Friday";
        daysOfTheWeek[7] = "Saturday";

        return daysOfTheWeek[dayOfTheWeek];
    }

    /**
     * Converts a string representation to int
     * @param dayOfTheWeek string representation of the day
     * @return int
     */
    public static int IntFromDayOfTheWeek(String dayOfTheWeek){
        if(dayOfTheWeek.equals("Sunday"))
            return Calendar.SUNDAY;

        if(dayOfTheWeek.equals("Monday"))
            return Calendar.MONDAY;

        if(dayOfTheWeek.equals("Tuesday"))
            return Calendar.TUESDAY;

        if(dayOfTheWeek.equals("Wednesday"))
            return Calendar.WEDNESDAY;

        if(dayOfTheWeek.equals("Thursday"))
            return Calendar.THURSDAY;

        if(dayOfTheWeek.equals("Friday"))
            return Calendar.FRIDAY;

        if(dayOfTheWeek.equals("Saturday"))
            return Calendar.SATURDAY;

        throw new IllegalArgumentException("Unable to convert sting to int. Supplied string "
                + dayOfTheWeek);
    }

    /**
     * Filters the classes by the supplied day of the week.
     * @param dayOfTheWeek day of the week as integer, where 1 is Sunday and 7 is Saturday respectively
     * @param classes list of classes to be filtered
     * @return List<Class> filtered list.
     */
    public static ArrayList<Class> FilterClassesByDayOfTheWeek(int dayOfTheWeek, List<Class> classes){
        ArrayList<Class> filteredClasses = new ArrayList<Class>();
        String weekDay = Utils.DayOfTheWeekFromInt(dayOfTheWeek);

        for(Class singleClass : classes){
            if(singleClass.getWeekDay().equals(weekDay)){
                filteredClasses.add(singleClass);
            }
        }

        return filteredClasses;
    }
}
