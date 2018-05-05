package com.truongkl.blockphonenumber.RoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.truongkl.blockphonenumber.Models.Group;
import com.truongkl.blockphonenumber.Models.Number;
import com.truongkl.blockphonenumber.Models.User;

import static com.truongkl.blockphonenumber.RoomDatabase.NumberDatabase.DATABASE_VERSION;

/**
 * Created by Truong KL on 11/17/2017.
 */

@Database(entities = {Group.class, Number.class, User.class}, version = DATABASE_VERSION)
public abstract class NumberDatabase extends RoomDatabase {
    public static NumberDatabase numberDatabase;
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Number_database";

    public abstract GroupDAO groupDAO();
    public abstract NumberDAO numberDAO();
    public abstract UserDAO userDAO();

    public static NumberDatabase getInstance(Context context){
        if (numberDatabase == null){
            numberDatabase = Room.databaseBuilder(context, NumberDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return numberDatabase;
    }
}
