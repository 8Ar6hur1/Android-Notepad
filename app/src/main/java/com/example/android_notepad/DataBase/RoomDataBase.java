package com.example.android_notepad.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.android_notepad.Models.Notes;

@Database(entities = Notes.class, version = 1, exportSchema = false)
public abstract class RoomDataBase extends RoomDatabase {

    private static RoomDataBase dataBase;
    private static String DATABASE_NAME = "NoteApp";

    public synchronized static RoomDataBase getInstance(Context context) {
        if (dataBase == null) {
            dataBase = Room.databaseBuilder(context.getApplicationContext(), RoomDataBase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return  dataBase;
    }
    public abstract mainDAO mainDAO();
}












