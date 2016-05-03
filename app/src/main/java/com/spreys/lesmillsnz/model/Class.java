package com.spreys.lesmillsnz.model;

import com.spreys.lesmillsnz.utils.Utils;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 8/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class Class {
    private String intensity;
    private String duration;
    private int clubId;
    private String name;
    private String image;
    private String weekDay;
    private Time time;
    private String description;
    private String location;

    /**
     * Default constructor
     * @param name name of the class has to be supplied
     */
    public Class(String name, String weekDay, Time time, int clubId){

        //Validate name
        if(name == null || name.length() < 1){
            throw new IllegalArgumentException("Invalid name. Name has to be not null and has to be not empty. " +
                    "Supplied name -> " + name);
        }

        //validate weekDay
        if(!validateWeekDay(weekDay)){
            throw new IllegalArgumentException("Invalid weekday. Received = " + weekDay);
        }

        //Validate time
        if(time == null){
            throw new IllegalArgumentException("Time cannot be null");
        }

        //Validate clubId
        if(clubId < 1){
            throw new IllegalArgumentException("Club id has to be higher than 0, current club id" +
                    "is " + clubId);
        }

        this.name = name;
        this.weekDay = weekDay;
        this.time = time;
        this.clubId = clubId;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getClubId() {
        return clubId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getTime() {
        return time;
    }

    private boolean validateWeekDay(String weekDay){
        String[] validArrays = new String[7];
        validArrays[0] = "Monday";
        validArrays[1] = "Tuesday";
        validArrays[2] = "Wednesday";
        validArrays[3] = "Thursday";
        validArrays[4] = "Friday";
        validArrays[5] = "Saturday";
        validArrays[6] = "Sunday";

        boolean validInput = false;
        for (String validArray : validArrays) {
            if (validArray.equals(weekDay)) {
                validInput = true;
            }
        }

        return validInput;
    }

    /**
     * Converts class duration string to int and multiplies by 60 000.
     * @return class duration as in milliseconds
     */
    public int getDurationInMills(){
        return Integer.valueOf(this.getDuration().replaceAll("[^0-9]", "")) * 60000;
    }


    /**
     * Returns the next day of the week
     * @return the next day of the week
     */
    public Calendar getDate(){
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(this.getTime());

        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int classDay = Utils.IntFromDayOfTheWeek(this.getWeekDay());

        Calendar calendarToReturn = Calendar.getInstance();
        calendarToReturn.set(Calendar.DAY_OF_WEEK, classDay);
        calendarToReturn.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
        calendarToReturn.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));

        if((today > classDay && classDay != Calendar.SUNDAY) ||
                (today == Calendar.SUNDAY && classDay != Calendar.SUNDAY)){
            calendarToReturn.add(Calendar.DAY_OF_WEEK, 7);
        }

        return calendarToReturn;
    }
}
