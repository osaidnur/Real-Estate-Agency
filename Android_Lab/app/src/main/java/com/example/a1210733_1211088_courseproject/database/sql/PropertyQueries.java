package com.example.a1210733_1211088_courseproject.database.sql;

/**
 * Contains all SQL queries related to the Property table.
 * This separates SQL queries from database logic for better maintenance.
 */
public class PropertyQueries {
    // Table name
    public static final String TABLE_NAME = "Property";

    // Column names
    public static final String COLUMN_PROPERTY_ID = "property_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_BEDROOMS = "bedrooms";
    public static final String COLUMN_BATHROOMS = "bathrooms";
    public static final String COLUMN_AREA = "area";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_IS_SPECIAL = "is_special";
    public static final String COLUMN_DISCOUNT = "discount";

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_PROPERTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TYPE + " TEXT NOT NULL," +
            COLUMN_TITLE + " TEXT NOT NULL," +
            COLUMN_DESCRIPTION + " TEXT," +
            COLUMN_BEDROOMS + " INTEGER NOT NULL," +
            COLUMN_BATHROOMS + " INTEGER NOT NULL," +
            COLUMN_AREA + " REAL NOT NULL," +  // in square meters
            COLUMN_PRICE + " REAL NOT NULL," +
            COLUMN_COUNTRY + " TEXT NOT NULL," +
            COLUMN_CITY + " TEXT NOT NULL," +
            COLUMN_IMAGE_URL + " TEXT," +  // URL to the property image
            COLUMN_IS_SPECIAL + " INTEGER DEFAULT 0," + // Boolean as INTEGER (0/1)
            COLUMN_DISCOUNT + " REAL DEFAULT 0.0" + // Discount percentage
            ");";

    // Drop table query
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Alter table queries for migrations
    public static final String ADD_IS_SPECIAL_COLUMN =
            "ALTER TABLE " + TABLE_NAME +
            " ADD COLUMN " + COLUMN_IS_SPECIAL + " INTEGER DEFAULT 0";

    public static final String ADD_DISCOUNT_COLUMN =
            "ALTER TABLE " + TABLE_NAME +
            " ADD COLUMN " + COLUMN_DISCOUNT + " REAL DEFAULT 0.0";

    // Common queries
    public static final String GET_ALL_PROPERTIES = "SELECT * FROM " + TABLE_NAME;

    public static final String GET_PROPERTY_BY_ID =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_PROPERTY_ID + " = ?";

    public static final String GET_SPECIAL_PROPERTIES =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_IS_SPECIAL + " = 1";

    public static final String DELETE_PROPERTY =
            "DELETE FROM " + TABLE_NAME +
            " WHERE " + COLUMN_PROPERTY_ID + " = ?";
}
