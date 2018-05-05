package com.truongkl.blockphonenumber.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Truong KL on 11/15/2017.
 */

@Entity(tableName = "groups",
        indices = {@Index(value = {"name", "userPhoneNumber"}, unique = true)},
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "phoneNumber", childColumns = "userPhoneNumber",
                onDelete = CASCADE, onUpdate = CASCADE))
public class Group {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int count;
    private String userPhoneNumber;

    public Group(){
    }

    @Ignore
    public Group(String name, int count, String userPhoneNumber) {
        this.name = name;
        this.count = count;
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
