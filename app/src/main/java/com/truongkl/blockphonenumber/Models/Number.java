package com.truongkl.blockphonenumber.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Truong KL on 11/17/2017.
 */

@Entity(tableName = "numbers",
        primaryKeys = {"number", "groupId"},
        foreignKeys = @ForeignKey(entity = Group.class, parentColumns = "id", childColumns = "groupId",
                onDelete = CASCADE, onUpdate = CASCADE))
public class Number {
    @NonNull
    private String number;
    private int groupId;

    public Number(){
    }

    @Ignore
    public Number(String number, int groupId) {
        this.number = number;
        this.groupId = groupId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
