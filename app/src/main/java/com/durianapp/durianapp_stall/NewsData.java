package com.durianapp.durianapp_stall;

/**
 * Created by Amer S Alkatheri on 23-Jun-17.
 */

public class NewsData {
    private int nId;
    private String title, content, sDate, eDate;

    public NewsData(int nId, String title) {
        this.nId = nId;
        this.title = title;
    }

    public NewsData(int nId, String title, String content, String sDate, String eDate) {
        this.nId = nId;
        this.title = title;
        this.content = content;
        this.sDate = sDate;
        this.eDate = eDate;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }
}
