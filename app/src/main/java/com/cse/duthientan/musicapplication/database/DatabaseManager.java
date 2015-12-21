package com.cse.duthientan.musicapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cse.duthientan.musicapplication.model.Playlist;
import com.cse.duthientan.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/23/2015.
 */
public class DatabaseManager {
    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;
    private String[] mPlaylistColumns = {DatabaseHelper.PLAYLIST_ORDER_COLUMN, DatabaseHelper.PLAYLIST_NAME_COLUMN, DatabaseHelper.PLAYLIST_DECRIPTION_COLUMN, DatabaseHelper.PLAYLIST_IMAGE_COLUM};
    private String[] mSongColumns = {DatabaseHelper.SONG_ORDER_COLUMN, DatabaseHelper.SONG_PLAYLIST_NAME_COLUMN, DatabaseHelper.SONG_URI_COLUMN, DatabaseHelper.SONG_NAME_COLUMN,DatabaseHelper.SONG_ID_PLAYLIST_COLUMN};

    public DatabaseManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
    }

    public void open() {
        mDatabase = mDatabaseHelper.getReadableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public long insertPlaylist(String namePlaylist, String image, String decription) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PLAYLIST_NAME_COLUMN, namePlaylist);
        values.put(DatabaseHelper.PLAYLIST_DECRIPTION_COLUMN, decription);
        values.put(DatabaseHelper.PLAYLIST_IMAGE_COLUM, image);
        return mDatabase.insert(DatabaseHelper.PLAYLIST_TABLE_NAME, null, values);
    }

    public void insertSong(String namePlaylist, String songURI, String name, String id) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SONG_PLAYLIST_NAME_COLUMN, namePlaylist);
        values.put(DatabaseHelper.SONG_NAME_COLUMN, name);
        values.put(DatabaseHelper.SONG_URI_COLUMN, songURI);
        values.put(DatabaseHelper.SONG_ID_PLAYLIST_COLUMN, id);
        mDatabase.insert(DatabaseHelper.SONG_TABLE_NAME, null, values);
    }

    public void clearPlaylist(String id){
        mDatabase.delete(DatabaseHelper.PLAYLIST_TABLE_NAME,DatabaseHelper.PLAYLIST_ORDER_COLUMN+"='"+id+"'",null);
        mDatabase.delete(DatabaseHelper.SONG_TABLE_NAME, DatabaseHelper.SONG_ID_PLAYLIST_COLUMN + "='" + id+"'", null);
    }

    public int clearSong(String id) {
        return mDatabase.delete(DatabaseHelper.SONG_TABLE_NAME, DatabaseHelper.SONG_ID_PLAYLIST_COLUMN + "='" + id+"'", null);
    }

    public List<Playlist> playlists() {
        List<Playlist> result = new ArrayList<>();
        Cursor cursor = mDatabase.query(DatabaseHelper.PLAYLIST_TABLE_NAME, mPlaylistColumns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_ORDER_COLUMN));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_NAME_COLUMN));
                String decr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_DECRIPTION_COLUMN));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_IMAGE_COLUM));
                List<Song> list = new ArrayList<>();
                Cursor tmp = mDatabase.query(DatabaseHelper.SONG_TABLE_NAME, mSongColumns, DatabaseHelper.SONG_ID_PLAYLIST_COLUMN + "='" + id + "'", null, null, null, null);

                if (tmp.getCount()!=0) {
                    tmp.moveToFirst();
                    do {

                        String uri = tmp.getString(tmp.getColumnIndexOrThrow(DatabaseHelper.SONG_URI_COLUMN));
                        String namesong = tmp.getString(tmp.getColumnIndexOrThrow(DatabaseHelper.SONG_NAME_COLUMN));
                        list.add(new Song(namesong, uri));
                    } while (tmp.moveToNext());
                }
                result.add(new Playlist(Integer.valueOf(id), name, path,decr, list));
            } while (cursor.moveToNext());
        }

        return result;
    }

    public Playlist getSong(String id) {
        List<Song> list = new ArrayList<>();
        Playlist result =null;
        Cursor cursor = mDatabase.query(DatabaseHelper.SONG_TABLE_NAME,mSongColumns,DatabaseHelper.SONG_ID_PLAYLIST_COLUMN + "='"+id+"'",null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SONG_NAME_COLUMN));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SONG_URI_COLUMN));
                list.add(new Song(name,path));
            }while (cursor.moveToNext());
        }
        cursor = mDatabase.query(DatabaseHelper.PLAYLIST_TABLE_NAME,mPlaylistColumns,DatabaseHelper.PLAYLIST_ORDER_COLUMN + "='"+id+"'",null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_NAME_COLUMN));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_IMAGE_COLUM));
                String decr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PLAYLIST_DECRIPTION_COLUMN));
                result =  new Playlist(Integer.valueOf(id),name,path,decr,list);
            }while (cursor.moveToNext());
        }
        return result;
    }

    public void update(String id,String des,String path, String name){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PLAYLIST_DECRIPTION_COLUMN,des);
        values.put(DatabaseHelper.PLAYLIST_IMAGE_COLUM,path);
        values.put(DatabaseHelper.PLAYLIST_NAME_COLUMN,name);

        String whereClause = DatabaseHelper.PLAYLIST_ORDER_COLUMN + " LIKE ?";
        String[] whereArgs = { id };
        mDatabase.update(DatabaseHelper.PLAYLIST_TABLE_NAME,values,whereClause,whereArgs);

        clearSong(id);


    }

}
