package com.truongkl.blockphonenumber.RoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.truongkl.blockphonenumber.Models.Group;

import java.util.List;

/**
 * Created by Truong KL on 11/17/2017.
 */

@Dao
public interface GroupDAO {
    @Insert
    public void insertGroup(Group... groups);

    @Insert
    public long insert(Group group);

    @Update
    public void updateGroup(Group... groups);

    @Query("UPDATE groups SET count = :newCount WHERE id LIKE :id")
    public void updateCount(int newCount, int id);

    @Delete
    public void deleteGroup(Group group);

    @Query("SELECT * FROM groups")
    public List<Group> loadGroups();

    @Query("SELECT * FROM groups WHERE userPhoneNumber LIKE :userPhoneNumber")
    public List<Group> loadGroupsByUserPhoneNumber(String userPhoneNumber);

    @Query("SELECT * FROM groups WHERE id LIKE :id")
    public Group findGroupById(int id);
}
