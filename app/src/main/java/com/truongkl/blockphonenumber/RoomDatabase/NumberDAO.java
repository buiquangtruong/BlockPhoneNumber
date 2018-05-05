package com.truongkl.blockphonenumber.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.truongkl.blockphonenumber.Models.Number;

import java.util.List;

/**
 * Created by Truong KL on 11/17/2017.
 */

@Dao
public interface NumberDAO {

    @Insert
    public void insertNumber(Number... numbers);

    @Update
    public void updateNumber(Number... numbers);

    @Delete
    public void deleteNumber(Number... numbers);

    @Query("SELECT * FROM numbers")
    public List<Number> loadNumbers();

    @Query("SELECT * FROM numbers WHERE groupId like :groupId")
    public List<Number> loadNumbersByGroupId(int groupId);

    @Query("SELECT number FROM numbers WHERE groupId in (SELECT id FROM groups WHERE userPhoneNumber LIKE :userPhoneNumber)")
    public List<String> loadnum(String userPhoneNumber);

    @Query("SELECT * FROM numbers WHERE number LIKE :number")
    public Number findNumber(String number);

}
