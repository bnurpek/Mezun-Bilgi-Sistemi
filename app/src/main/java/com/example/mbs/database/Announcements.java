package com.example.mbs.database;

import android.net.Uri;

import java.util.Date;

public class Announcements {
    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public int type;
    private static int count = 0;
    private String id;
    public String path;
    public Date sendDate;
    public String header;
    public String author;
    public String text;

    public Announcements() { }

    public Announcements(String path, String header, String author, String text){
        type = Announcements.IMAGE_TYPE;
        sendDate = new Date();
        this.path = path;
        this.header = header;
        this.author = author;
        this.text = text;
        id = String.valueOf(count);
        count++;
    }

    public Announcements(String path, String header, String id, String author, String text){
        type = Announcements.IMAGE_TYPE;
        sendDate = new Date();
        this.path = path;
        this.header = header;
        this.author = author;
        this.text = text;
        this.id = id;
        count++;
    }

    public Announcements(String header, String author, String text){
        type = Announcements.TEXT_TYPE;
        sendDate = new Date();
        this.header = header;
        this.author = author;
        this.text = text;
        id = String.valueOf(count);
        count++;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public void setPath(String path) {
        this.path = path;
    }
}
