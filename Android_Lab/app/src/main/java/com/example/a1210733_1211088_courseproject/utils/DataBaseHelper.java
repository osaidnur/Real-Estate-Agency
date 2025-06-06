package com.example.a1210733_1211088_courseproject.utils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    // Table names
    public static final String TABLE_USER = "User";
    public static final String TABLE_PROPERTY = "Property";
    public static final String TABLE_FAVORITE = "Favorite";
    public static final String TABLE_RESERVATION = "Reservation";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // User table
        db.execSQL(
                "CREATE TABLE " + TABLE_USER + " (" +
                        " user_id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " email             TEXT UNIQUE NOT NULL," +
                        " password          TEXT NOT NULL," +
                        " first_name        TEXT NOT NULL," +
                        " last_name         TEXT NOT NULL," +
                        " gender            TEXT," +
                        " phone             TEXT," +
                        " country           TEXT," +
                        " city              TEXT," +
                        " role              TEXT NOT NULL," +
                        " profile_photo     TEXT" +
                        ");"
        );

        // Property table
        db.execSQL(
                "CREATE TABLE " + TABLE_PROPERTY + " (" +
                        " property_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " type            TEXT NOT NULL," +
                        " title           TEXT NOT NULL," +
                        " description     TEXT," +
                        " bedrooms        INTEGER NOT NULL," +
                        " bathrooms       INTEGER NOT NULL," +
                        " area            REAL NOT NULL," +  // in square meters
                        " price           REAL NOT NULL," +
                        " country         TEXT NOT NULL," +
                        " city            TEXT NOT NULL," +
                        " image_url       TEXT," +  // URL to the property image
                        " is_special      INTEGER DEFAULT 0," + // Boolean as INTEGER (0/1)
                        " discount        REAL DEFAULT 0.0" + // Discount percentage
                        ");"
        );

        // Favorites table
        db.execSQL(
                "CREATE TABLE " + TABLE_FAVORITE + " (" +
                        " user_id     INTEGER NOT NULL," +
                        " property_id INTEGER NOT NULL," +
                        " added_at    DATE," +
                        " PRIMARY KEY (user_id, property_id)," +
                        " FOREIGN KEY(user_id) REFERENCES "  + TABLE_USER  + "(user_id) ON DELETE CASCADE," +
                        " FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTY + "(property_id) ON DELETE CASCADE" +
                        ");"
        );

        // Reservation table
        db.execSQL(
                "CREATE TABLE " + TABLE_RESERVATION + " (" +
                        " reservation_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " user_id           INTEGER NOT NULL," +
                        " property_id       INTEGER NOT NULL UNIQUE," +  // enforce one reservation per property
                        " reservation_date  DATE    NOT NULL," +
                        " status            TEXT    NOT NULL," +
                        " FOREIGN KEY(user_id)     REFERENCES "  + TABLE_USER     + "(user_id) ON DELETE CASCADE," +
                        " FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTY + "(property_id) ON DELETE CASCADE" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
