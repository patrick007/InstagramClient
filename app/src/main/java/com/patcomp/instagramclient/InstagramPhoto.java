package com.patcomp.instagramclient;

import java.util.ArrayList;

/**
 * Created by patrick on 2/7/16.
 */
public class InstagramPhoto {
    public String username;
    public String caption;
    public String imageUrl;
    public int imagHeight;
    public int imageWidth;
    public int likesCount;
    public String profileImageUrl;
    public String timespan;
    public ArrayList<InstagramComment> comments;
    public int commentsCount;


    public InstagramPhoto(){
        username = "";
        caption = "";
        imageUrl = "";
        imagHeight = 0;
        imageWidth = 0;
        likesCount = 0;
        profileImageUrl = "";
        timespan = "";
        comments = new ArrayList<InstagramComment>();
        commentsCount = 0;
    }

}
