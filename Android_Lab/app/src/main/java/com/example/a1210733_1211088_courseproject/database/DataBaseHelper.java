package com.example.a1210733_1211088_courseproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.a1210733_1211088_courseproject.database.sql.FavoriteQueries;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.ReservationQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables using SQL queries from dedicated query files
        db.execSQL(UserQueries.CREATE_TABLE);
        db.execSQL(PropertyQueries.CREATE_TABLE);
        db.execSQL(FavoriteQueries.CREATE_TABLE);
        db.execSQL(ReservationQueries.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // For more drastic changes, drop and recreate tables
            db.execSQL(ReservationQueries.DROP_TABLE);
            db.execSQL(FavoriteQueries.DROP_TABLE);
            db.execSQL(PropertyQueries.DROP_TABLE);
            db.execSQL(UserQueries.DROP_TABLE);
            onCreate(db);
    }
}
