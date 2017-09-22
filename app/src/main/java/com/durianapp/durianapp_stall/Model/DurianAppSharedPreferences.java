package com.durianapp.durianapp_stall.Model;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Lenovo on 6/19/2017.
 */

public class DurianAppSharedPreferences {
    public static final String PREF_STALL_ID = "PREF_STALL_ID";

    public static int getStallId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_STALL_ID,-1);
    }

    public static void setStallID(Context context, int id){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_STALL_ID,id).apply();
    }
}
