package com.cse.duthientan.musicapplication.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cse.duthientan.musicapplication.Activity.NotificationView;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/26/2015.
 */
public class PlayMusic extends Service {

    public static final String PLAYMUSIC = "Playing";

    private IBinder mBinder;
    private MediaPlayer player;
    private List<Song> songList;

    public int getIsnumplaying() {
        return isnumplaying;
    }

    private int isnumplaying;
    private Handler mHandler;
    private Boolean played = false;
    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews remoteViews;
    private SharedPreferences preferences;
    private boolean isOnline = false;

    @Override
    public void onCreate() {
        player = new MediaPlayer();
        mBinder = new LocalBinder();
        mHandler = new Handler();
        played = false;
        remoteViews = new RemoteViews(getPackageName(), R.layout.notifycation);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification.Builder(getApplicationContext())
                .setContent(remoteViews)
                .setSmallIcon(R.drawable.music)
                .build();
        preferences = getSharedPreferences(PLAYMUSIC, Context.MODE_PRIVATE);
        updateSeekBar();
        super.onCreate();
    }

    public void setisOnline(boolean b) {
        isOnline = b;
    }

    public boolean isOn() {
        return isOnline;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public PlayMusic getService() {

            return PlayMusic.this;
        }
    }


    public void setList(List<Song> list) {
        songList = list;
    }

    public void addMusicOnline(int id) {
        try {
            isnumplaying = id;
            player = new MediaPlayer();
            player.setDataSource(songList.get(id).getPath());
            player.prepare();
            updateSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMusic(int id) {
        try {
            isnumplaying = id;
            player = new MediaPlayer();
            File file = new File(songList.get(id).getPath());
            FileInputStream inputStream = new FileInputStream(file);
            player.setDataSource(inputStream.getFD());
            player.prepare();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNamePlayer() {
        return songList.get(isnumplaying).getName();
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (played) {
                if (player.getCurrentPosition() / 1000 == player.getDuration() / 1000) {

                    if (isnumplaying < songList.size() - 1)
                        isnumplaying++;
                    else isnumplaying = 0;
                    if (isOnline)
                        addMusicOnline(isnumplaying);
                    else
                        addMusic(isnumplaying);
                    playMusic();
                }
            }
                mHandler.postDelayed(this, 100);
            }
        }

        ;

        public Boolean isPlayed() {
            return played;
        }

        public void playMusic() {
            startMusic();
            played = true;
        }

        public void pauseMusic() {
            notificationManager.cancelAll();
            player.pause();
        }

        public void startMusic() {
            player.start();
            notificationManager.cancelAll();
            notificationManager.notify(9999, notification);
        }

        public void seekTo(int numSeek) {
            player.seekTo(numSeek);
            playMusic();
        }

        public int getDuration() {
            int duration = player.getDuration() / 1000;
            return duration;
        }

        public void stopMusic() {
            player.stop();
            notificationManager.cancelAll();
        }

        public boolean isPlaying() {
            return player.isPlaying();
        }

        public int getCurrentPosition() {
            return player.getCurrentPosition() / 1000;
        }

        public void back() {
            stopMusic();
            if (isnumplaying > 0)
                isnumplaying--;
            else isnumplaying = songList.size() - 1;
            if (isOnline) addMusicOnline(isnumplaying);
            else addMusic(isnumplaying);
            playMusic();
        }

        public void next() {
            stopMusic();
            if (isnumplaying < songList.size() - 1)
                isnumplaying++;
            else isnumplaying = 0;
            if (isOnline) addMusicOnline(isnumplaying);
            else addMusic(isnumplaying);
            playMusic();
        }
    }
