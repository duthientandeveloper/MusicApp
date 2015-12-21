package com.cse.duthientan.musicapplication.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cse.duthientan.musicapplication.Adapter.SongAdapter;
import com.cse.duthientan.musicapplication.database.DatabaseManager;
import com.cse.duthientan.musicapplication.model.Playlist;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongManager;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.service.PlayMusic;

import java.util.List;

public class ListenMusicActivity extends AppCompatActivity {
    private ListView listsong;
    private SongAdapter adapter;
    private SeekBar bar;
    private SongManager manager;
    private TextView nameSongListening;
    private Handler mHandler;
    private Button back_bnt;
    private Button play_bnt;
    private Button next_bnt;
    private List<Song> list;
    private PlayMusic mService;
    private Boolean mBound = false;
    private int mIdPlaylist = 9999;
    private boolean barcheck;
    private RelativeLayout listenall;
    private LinearLayout probar;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PlayMusic.LocalBinder binder = (PlayMusic.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if (mService.isPlaying()) {

                if(barcheck){
                    mService.setList(list);
                    mService.setisOnline(false);
                    updateSeekBar();
                }else {
                    mService.pauseMusic();
                    mService.setList(list);
                    mService.setisOnline(false);
                    playmusic(id[0]);
                }
            } else {
                mService.setList(list);

                if(barcheck){
                    mService.setisOnline(false);
                    updateSeekBar();
                }else {
                    mService.setisOnline(false);
                    playmusic(id[0]);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    ;
    final int[] id = {0};
    final int[] sizeMusic = {0};
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_music);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(this, PlayMusic.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        imageView = (ImageView) findViewById(R.id.imagelisten);
        bar = (SeekBar) findViewById(R.id.listen_bar);
        listsong = (ListView) findViewById(R.id.listensong);
        nameSongListening = (TextView) findViewById(R.id.namelisten);
        back_bnt = (Button) findViewById(R.id.back_bnt);
        play_bnt = (Button) findViewById(R.id.play_bnt);
        next_bnt = (Button) findViewById(R.id.next_bnt);
        probar = (LinearLayout) findViewById(R.id.probar);
        listenall = (RelativeLayout) findViewById(R.id.listenall);
        probar.setVisibility(View.INVISIBLE);
        listenall.setVisibility(View.VISIBLE);
        manager = new SongManager(getContentResolver());
        mHandler = new Handler();
        int tmpId;
        int tmpSize;
        barcheck = getIntent().getBooleanExtra("bar",false);
        if (getIntent().getBooleanExtra("PLAYLIST", false)) {
            int id = getIntent().getIntExtra("id", 0);
            mIdPlaylist = id;
            DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
            databaseManager.open();

            Playlist playlist = databaseManager.playlists().get(id);
            imageView.setImageBitmap(BitmapFactory.decodeFile(playlist.getmPathImage()));
            list = playlist.getmListSong();
            tmpId = 0;
            tmpSize = list.size();
        } else {
            tmpId = getIntent().getIntExtra("ID", 0);
            tmpSize = manager.getListSong().size();
            list = manager.getListSong();
        }
        id[0] = tmpId;
        sizeMusic[0] = tmpSize;

        play_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService.isPlaying())
                    mService.pauseMusic();
                else
                    mService.startMusic();
            }
        });

        next_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.next();
            }
        });

        back_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.back();
            }
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int newProgress, boolean fromUser) {
                if (fromUser) {
                    mService.seekTo(newProgress * 1000);
                    mService.startMusic();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mService.pauseMusic();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.startMusic();
            }
        });

        adapter = new SongAdapter(list);
        listsong.setAdapter(adapter);
        listsong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                id[0] = i;
                playmusic(id[0]);

            }
        });
    }

    private void playmusic(int id) {
        bar.setProgress(0);
        if (mService.isPlaying()) {
            mService.stopMusic();
        }
        mService.addMusic(id);
        mService.playMusic();
        nameSongListening.setText(mService.getNamePlayer());
        bar.setMax(mService.getDuration());
        updateSeekBar();
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            bar.setMax(mService.getDuration());
            bar.setProgress(mService.getCurrentPosition());
            nameSongListening.setText(mService.getNamePlayer());
            if (mService.isPlaying())
                play_bnt.setBackgroundResource(R.drawable.pause);
            else play_bnt.setBackgroundResource(R.drawable.play);
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        unbindService(mConnection);
        super.onDestroy();
    }
}
