package com.cse.duthientan.musicapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Du Thien Tan on 10/23/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    static public final String DATABASE_NAME = "playlist_database";
    static public final int DATABASE_VERTION = 1;

    static public final String PLAYLIST_TABLE_NAME = "playlist";
    static public final String PLAYLIST_ORDER_COLUMN = "_oder";
    static public final String PLAYLIST_NAME_COLUMN = "name";
    static public final String PLAYLIST_DECRIPTION_COLUMN="decription";
    static public final String PLAYLIST_IMAGE_COLUM = "path";

    static public final String SONG_TABLE_NAME = "song";
    static public final String SONG_ORDER_COLUMN = "_oder";
    static public final String SONG_PLAYLIST_NAME_COLUMN = "namelist";
    static public final String SONG_URI_COLUMN = "path";
    static public final String SONG_NAME_COLUMN = "namesong";
    static public final String SONG_ID_PLAYLIST_COLUMN = "id";

    static public final String CREATE_PLAYLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + PLAYLIST_TABLE_NAME + "("
            + PLAYLIST_ORDER_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PLAYLIST_DECRIPTION_COLUMN + " TEXT NOT NULL,"
            + PLAYLIST_IMAGE_COLUM +" TEXT NOT NULL,"
            + PLAYLIST_NAME_COLUMN +" TEXT NOT NULL);";

    static public final String CREATE_SONG_TABLE = "CREATE TABLE IF NOT EXISTS " + SONG_TABLE_NAME + "("
            + SONG_ORDER_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SONG_PLAYLIST_NAME_COLUMN +" TEXT NOT NULL,"
            + SONG_NAME_COLUMN +" TEXT NOT NULL,"
            + SONG_ID_PLAYLIST_COLUMN + " TEXT NOT NULL,"
            + SONG_URI_COLUMN + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERTION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAYLIST_TABLE);
        database.execSQL(CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME);
        onCreate(database);
    }
}
