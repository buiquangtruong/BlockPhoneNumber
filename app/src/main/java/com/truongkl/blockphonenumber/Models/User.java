package com.truongkl.blockphonenumber.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Truong KL on 11/22/2017.
 */

@Entity (tableName = "users")
public class User {
    @NonNull
    @PrimaryKey()
    private String phoneNumber;

    public User(){
    }

    @Ignore
    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
