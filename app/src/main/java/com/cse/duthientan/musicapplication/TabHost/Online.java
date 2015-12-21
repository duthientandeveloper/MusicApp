package com.cse.duthientan.musicapplication.TabHost;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cse.duthientan.musicapplication.Activity.ListenMusicActivity;
import com.cse.duthientan.musicapplication.Activity.ListenMusicOnlineActivity;
import com.cse.duthientan.musicapplication.Adapter.AblumAdapter;
import com.cse.duthientan.musicapplication.Adapter.PlaylistAdapter;
import com.cse.duthientan.musicapplication.Adapter.SongAdapter;
import com.cse.duthientan.musicapplication.Zingmp3Client;
import com.cse.duthientan.musicapplication.database.DatabaseManager;
import com.cse.duthientan.musicapplication.model.Ablum;
import com.cse.duthientan.musicapplication.model.AblumHot;
import com.cse.duthientan.musicapplication.model.Playlist;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongManager;
import com.cse.duthientan.musicapplication.model.SongOnline;
import com.cse.duthientan.musicapplication.service.PlayMusic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/12/2015.
 */
public class Online extends Fragment {

    private GridView listplaylist;
    private ListView listsonghot;
    private Zingmp3Client client;
    private AblumAdapter ablumAdapter;
    private PlayMusic mService;
    private Boolean mBound = false;
    private TextView barname;
    private ImageButton barnext;
    private ImageButton barplay;
    private ImageButton barback;
    private ImageView imageView;
    private SeekBar bar;
    private SharedPreferences preferences;
    private Handler mHandler;
    private String link;
    private String image;
    private ImageView imagebar;
    private SongManager manager ;
    private DatabaseManager databaseManager;
    private LinearLayout onall;
    private LinearLayout processbar;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            preferences = getActivity().getSharedPreferences(PlayMusic.PLAYMUSIC, Context.MODE_PRIVATE);
            PlayMusic.LocalBinder binder = (PlayMusic.LocalBinder) service;

            mService = binder.getService();
            mBound = true;
            if (mService.isPlayed()) {
                updateSeekBar();
                barname.setText(mService.getNamePlayer());
                if (mService.isPlaying()) {
                    barplay.setBackgroundResource(R.drawable.pause);

                } else barplay.setBackgroundResource(R.drawable.play);
                barplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mService.isPlaying()) {
                            barplay.setBackgroundResource(R.drawable.play);
                            mService.pauseMusic();
                        } else {
                            barplay.setBackgroundResource(R.drawable.pause);
                            mService.startMusic();
                        }
                    }
                });

                barnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mService.next();
                        barplay.setBackgroundResource(R.drawable.pause);
                    }
                });

                barback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mService.back();
                        barplay.setBackgroundResource(R.drawable.pause);
                    }
                });
                bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                        mService.isPlayed();
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mService.isOn()) {
                            Intent intent = new Intent(getActivity(), ListenMusicOnlineActivity.class);
                            intent.putExtra("bar", true);
                            intent.putExtra("image", preferences.getString("image", null));
                            intent.putExtra("link", preferences.getString("link", null));
                            startActivity(intent);
                        }else {
                            if(preferences.getInt("idplaylist",9999)==9999){
                                Intent intent = new Intent(getActivity(), ListenMusicActivity.class);
                                intent.putExtra("ID",preferences.getInt("idsong",0));
                                intent.putExtra("bar",true);
                                intent.putExtra("size",manager.getListSong().size());
                                startActivityForResult(intent, 1);
                            }else {
                                Intent intent= new Intent(getActivity(),ListenMusicActivity.class);
                                intent.putExtra("PLAYLIST",true);
                                intent.putExtra("bar",true);
                                intent.putExtra("id",preferences.getInt("idplaylist",9999));
                                startActivity(intent);
                            }
                        }
                    }
                });

                barname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mService.isOn()) {
                            Intent intent = new Intent(getActivity(), ListenMusicOnlineActivity.class);
                            intent.putExtra("bar", true);
                            intent.putExtra("image", preferences.getString("image", null));
                            intent.putExtra("link", preferences.getString("link", null));
                            startActivity(intent);
                        }else {
                            if(preferences.getInt("idplaylist",9999)==9999){
                                Intent intent = new Intent(getActivity(), ListenMusicActivity.class);
                                intent.putExtra("ID",preferences.getInt("idsong",0));
                                intent.putExtra("bar",true);
                                intent.putExtra("size",manager.getListSong().size());
                                startActivityForResult(intent, 1);
                            }else {
                                Intent intent= new Intent(getActivity(),ListenMusicActivity.class);
                                intent.putExtra("PLAYLIST",true);
                                intent.putExtra("bar",true);
                                intent.putExtra("id",preferences.getInt("idplaylist",9999));
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
            if(mService.isOn()){
                Picasso.with(getActivity()).load(preferences.getString("image", null)).into(imageView);
            }else {
                if(preferences.getInt("idplaylist",9999)!=9999)
                    imageView.setImageBitmap(BitmapFactory.decodeFile(databaseManager.playlists().get(preferences.getInt("idplaylist", 9999)).getmPathImage()));
                else imageView.setBackgroundResource(R.drawable.photo_default);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        preferences = getActivity().getSharedPreferences(PlayMusic.PLAYMUSIC, Context.MODE_PRIVATE);
        onall = (LinearLayout) view.findViewById(R.id.onall);
        processbar = (LinearLayout)view.findViewById(R.id.processbar);
        client = new Zingmp3Client(getActivity());
        mHandler = new Handler();
        databaseManager=  new DatabaseManager(getActivity());
        databaseManager.open();
        manager = new SongManager(getActivity().getContentResolver());
        barback = (ImageButton) view.findViewById(R.id.bar_back);
        barname = (TextView) view.findViewById(R.id.bar_name);
        barnext = (ImageButton) view.findViewById(R.id.bar_next);
        barplay = (ImageButton) view.findViewById(R.id.bar_play);
        bar = (SeekBar) view.findViewById(R.id.barbar);
        imageView = (ImageView) view.findViewById(R.id.imagebar);
        listplaylist = (GridView) view.findViewById(R.id.gridPlaylist);
        listsonghot = (ListView) view.findViewById(R.id.hotsong);
        imagebar = (ImageView) view.findViewById(R.id.imagebar);
        client.ablum(new Zingmp3Client.OnAblumListerner() {
            @Override
            public void onSuccess(final AblumHot ablumHot) {
                ablumAdapter = new AblumAdapter(ablumHot.getmList(), getActivity());
                listplaylist.setAdapter(ablumAdapter);
                listplaylist.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                listplaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        link = ablumHot.getmList().get(i).getmLink();
                        image = ablumHot.getmList().get(i).getmImage();
                        Intent intent = new Intent(getActivity(), ListenMusicOnlineActivity.class);
                        intent.putExtra("image", image);
                        intent.putExtra("link", link);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("image", image);
                        editor.putString("link", link);
                        editor.commit();
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onError(String error) {

            }
        });
        client.song(new Zingmp3Client.OnSongListener() {
            @Override
            public void onSuccess(final List<Song> list) {
                listsonghot.setAdapter(new SongAdapter(list));
                listsonghot.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                listsonghot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getActivity(), ListenMusicOnlineActivity.class);
                        link = list.get(i).getPath();
                        image = null;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("image", image);
                        editor.putString("link", link);
                        editor.commit();
                        intent.putExtra("link", list.get(i).getPath());
                        startActivity(intent);

                    }
                });
                onall.setVisibility(View.VISIBLE);
                processbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String error) {

            }
        });
        return view;
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void onResume() {
        Intent intent = new Intent(getActivity(), PlayMusic.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            barname.setText(mService.getNamePlayer());
            bar.setMax(mService.getDuration());
            bar.setProgress(mService.getCurrentPosition());

            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }
}
