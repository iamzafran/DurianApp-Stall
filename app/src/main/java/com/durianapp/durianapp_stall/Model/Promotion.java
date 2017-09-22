package com.durianapp.durianapp_stall.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Lenovo on 7/3/2017.
 */

public class Promotion implements Serializable{
    private int mPromotionId;
    private String mPromotionText;
    private Date mEndDate;

    public int getPromotionId() {
        return mPromotionId;
    }

    public void setPromotionId(int promotionId) {
        mPromotionId = promotionId;
    }

    public String getPromotionText() {
        return mPromotionText;
    }

    public void setPromotionText(String promotionText) {
        mPromotionText = promotionText;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }
}
