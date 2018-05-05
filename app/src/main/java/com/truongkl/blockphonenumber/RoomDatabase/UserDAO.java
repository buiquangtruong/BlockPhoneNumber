package com.truongkl.blockphonenumber.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.truongkl.blockphonenumber.Models.User;

import java.util.List;

/**
 * Created by Truong KL on 11/22/2017.
 */

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertUser(User... users);

    @Update
    public void updateUser(User... users);

    @Delete
    public void deleteGroup(User user);

    @Query("SELECT * FROM users")
    public List<User> loadUsers();

    @Query("SELECT * FROM users WHERE phoneNumber LIKE :phoneNumber")
    public User findUserByPhoneNumber(int phoneNumber);
}
