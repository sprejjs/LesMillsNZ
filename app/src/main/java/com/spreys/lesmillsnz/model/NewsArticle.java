package com.spreys.lesmillsnz.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 19/10/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class NewsArticle {
    private int club_id;
    private String headline;
    private String shortDescription;
    private String content;
    private Bitmap image;
    private String base64Image;

    public NewsArticle(String headline, String content, int club_id){
        //Validate headline
        if(headline == null || headline.length() < 1){
            throw new IllegalArgumentException("Unable to create a news article, invalid headline." +
                    " Headline - " + headline);
        }

        //Validate content
        if(content == null || content.length() < 1){
            throw new IllegalArgumentException("Unable to create a news article, invalid content. " +
                    "Content - " + content);
        }

        //Check id
        if(club_id < 1){
            throw new IllegalArgumentException("Unable to create a news article, invalid club id. " +
                    "Club id - " + club_id);
        }

        this.headline = headline;
        this.content = content;
        this.club_id = club_id;
    }

    public int getClubId() {
        return club_id;
    }

    public String getHeadline() {
        return this.headline;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return this.content;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public String getBase64Image(){
        return this.base64Image;
    }

    public void setImage(String base64) {
        this.base64Image = base64;
        byte[] decodedString = Base64.decode(base64, Base64.NO_WRAP);
        this.image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
