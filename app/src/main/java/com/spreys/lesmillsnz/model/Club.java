package com.spreys.lesmillsnz.model;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 5/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class Club {
    private int id;
    private double latitude;
    private double longitude;
    private String name;

    /**
     * Default constructor. All of the parameters have to be valid
     * @param id unique identifier. Has to be a positive number
     * @param latitude clubs location latitude. Valid lat is between -90 and 90
     * @param longitude clubs location longitude. Valid long is between -180 and 180
     * @param name name of the club. Has to be a none-empty String.
     */
    public Club(int id, double latitude, double longitude, String name){

        //Check id
        if(id < 1){
            throw new IllegalArgumentException("Unable to create a club, ID has to be a positive " +
                    "integer. Supplied id is " + id);
        }

        //Check latitude
        if(latitude < -90 || latitude > 90){
            throw new IllegalArgumentException("Unable to create a club, latitude has to be between" +
                    "-90 and 90. Supplied latitude is " + latitude);
        }

        //Check longitude
        if(longitude < -180 || longitude > 180){
            throw new IllegalArgumentException("Unable to create a club, longitude has to be between" +
                    "-180 and 180. Supplied longitude is " + longitude);
        }

        //Check name
        if(name == null || name.equals("")){
            throw new IllegalArgumentException("Unable to create a club, name has to be a none-empty" +
                    "string. Supplied name is " + name);
        }

        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    /**
     * Returns club's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns club's longitude
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns club's latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns club's unique identifier
     * @return id
     */
    public int getId() {
        return id;
    }
}
