package com.cse.duthientan.musicapplication.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.duthientan.musicapplication.Adapter.SongAdapter;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.Zingmp3Client;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongOnline;
import com.cse.duthientan.musicapplication.service.PlayMusic;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.NameValuePair;

public class ListenMusicOnlineActivity extends AppCompatActivity {
    private ImageView imageAblum;
    private Zingmp3Client client;
    private ListView listView;
    private SongAdapter adapter;
    private MediaPlayer player;
    private int numPlaying;
    private Button pausebnt;
    private SeekBar seekBar;
    private Button backbnt;
    private Button nextbnt;
    private Handler mHandler;
    private int numSong;
    private List<Song> mSong;
    private TextView view;
    private Button download;
    private PlayMusic mService;
    private Boolean mBound = false;
    private boolean isBar;
    private RelativeLayout listenall;
    private LinearLayout probar;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PlayMusic.LocalBinder binder = (PlayMusic.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if(isBar) {
                mService.setList(mSong);
                mService.setisOnline(true);
                updateSeekBar();
            }
            else {
                if (mService.isPlaying())
                    mService.pauseMusic();
                mService.setList(mSong);
                mService.setisOnline(true);
                mService.addMusicOnline(numPlaying);
                mService.playMusic();
                updateSeekBar();
            }
            backbnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mService.back();
                    pausebnt.setBackgroundResource(R.drawable.pause);
                }
            });

            nextbnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mService.next();
                    pausebnt.setBackgroundResource(R.drawable.pause);
                }
            });

            pausebnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mService.isPlaying())
                    {
                        mService.pauseMusic();
                        pausebnt.setBackgroundResource(R.drawable.play);
                    }else {
                        mService.startMusic();
                        pausebnt.setBackgroundResource(R.drawable.pause);
                    }
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        mService.seekTo(i * 1000);
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
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mService.pauseMusic();
                    mService.addMusicOnline(i);
                    mService.playMusic();
                }
            });
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    PersistentCookieStore cookieStore = new PersistentCookieStore(getApplication());

                    httpClient.setCookieStore(cookieStore);
                    final File finalFile = new File(Environment.getExternalStorageDirectory()+"/musicapp", mService.getNamePlayer()+".mp3");
                    try {
                        finalFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    httpClient.get(mSong.get(mService.getIsnumplaying()).getPath(), new FileAsyncHttpResponseHandler(finalFile) {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            Toast.makeText(getApplicationContext(),"Downloaded",Toast.LENGTH_LONG).show();
                            file.setReadable(true);


                        }

                        @Override
                        public void onProgress(long bytesWritten, long totalSize) {
                        }
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_music);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        probar = (LinearLayout) findViewById(R.id.probar);
        listenall = (RelativeLayout) findViewById(R.id.listenall);
        isBar = getIntent().getBooleanExtra("bar",false);
        mHandler = new Handler();
        backbnt = (Button) findViewById(R.id.back_bnt);
        nextbnt = (Button) findViewById(R.id.next_bnt);
        pausebnt = (Button) findViewById(R.id.play_bnt);
        seekBar = (SeekBar) findViewById(R.id.listen_bar);
        view = (TextView) findViewById(R.id.namelisten);
        download = (Button) findViewById(R.id.next_bnt1);
        imageAblum = (ImageView) findViewById(R.id.imagelisten);
        listView = (ListView) findViewById(R.id.listensong);
        client = new Zingmp3Client(getApplicationContext());
        if (getIntent().getStringExtra("image") != null)
            Picasso.with(getApplicationContext())
                    .load(getIntent().getStringExtra("image")).into(imageAblum);
        final String link = getIntent().getStringExtra("link");
        numPlaying = 0;

        client.data(link, new Zingmp3Client.OnDataListener() {
            @Override
            public void onSuccess(final List<Song> list) {
                mSong = list;
                adapter = new SongAdapter(mSong);
                listView.setAdapter(adapter);
                probar.setVisibility(View.INVISIBLE);
                listenall.setVisibility(View.VISIBLE);
                numSong = mSong.size();
                Intent intent = new Intent(getApplicationContext(), PlayMusic.class);
                startService(intent);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            seekBar.setMax(mService.getDuration());
            seekBar.setProgress(mService.getCurrentPosition());
            view.setText(mService.getNamePlayer());
            mHandler.postDelayed(this, 100);
        }
    };


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
