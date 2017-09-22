package com.durianapp.durianapp_stall;

/**
 * Created by Amer S Alkatheri on 16-Jun-17.
 */

public class Config {
    //Main URL
    private static final String MAIN_URL = "http://durianapp.esy.es/";
    //private static final String MAIN_URL = "http://192.168.1.106/durianapp/";

    //Data URL
    public static final String DURIAN_FEED_URL = MAIN_URL + "durianfeed.php";
    public static final String DURIAN_INFO_URL = MAIN_URL + "durianinfo.php?id=";
    public static final String DURIAN_SEARCH_URL = MAIN_URL + "searchdurian.php";
    public static final String DURIAN_NEWS_URL = MAIN_URL + "duriannews.php";
    public static final String NEWS_INFO_URL = MAIN_URL + "newsinfo.php?id=";
}
