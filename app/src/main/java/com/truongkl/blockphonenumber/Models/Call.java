package com.truongkl.blockphonenumber.Models;

import java.util.Date;

/**
 * Created by Truong KL on 11/29/2017.
 */

public class Call {
    private String phoneNumber;
    private int callType;
    private Date callDate;
    private int duration;
    private boolean isChoose;
    private int count;

    public Call() {
    }

    public Call(String phoneNumber, int callType, Date callDate, int duration) {
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.duration = duration;
        this.count = 1;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public Date getCallDate() {
        return callDate;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
