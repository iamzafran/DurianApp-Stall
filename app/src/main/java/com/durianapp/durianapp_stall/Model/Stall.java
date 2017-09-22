package com.durianapp.durianapp_stall.Model;

import java.io.Serializable;

/**
 * Created by Lenovo on 6/20/2017.
 */

public class Stall implements Serializable {
    private int mId;
    private String mName;
    private String mPhone;
    private String mAddress;
    private String mCity;
    private String mPostcode;
    private String mState;
    private String mLocality;
    private String mPictureUrl;
    private String mFirebaseID;


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getPostcode() {
        return mPostcode;
    }

    public void setPostcode(String postcode) {
        mPostcode = postcode;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getLocality() {
        return mLocality;
    }

    public void setLocality(String locality) {
        mLocality = locality;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        mPictureUrl = pictureUrl;
    }

    public String getFirebaseID() {
        return mFirebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        mFirebaseID = firebaseID;
    }
}
