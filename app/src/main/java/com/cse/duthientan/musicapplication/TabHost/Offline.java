package com.cse.duthientan.musicapplication.TabHost;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.cse.duthientan.musicapplication.Activity.ListenMusicActivity;
import com.cse.duthientan.musicapplication.Activity.ListenMusicOnlineActivity;
import com.cse.duthientan.musicapplication.Activity.PlaylistActivity;
import com.cse.duthientan.musicapplication.Adapter.PlaylistAdapter;
import com.cse.duthientan.musicapplication.Adapter.SongAdapter;
import com.cse.duthientan.musicapplication.database.DatabaseManager;
import com.cse.duthientan.musicapplication.model.Playlist;
import com.cse.duthientan.musicapplication.model.SongManager;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.service.PlayMusic;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/12/2015.
 */
public class Offline extends Fragment {
    private RecyclerView listplaylist;
    private ListView listsong;
    private SongAdapter adapter;
    private FloatingActionButton addPlaylistButton;
    private SongManager  manager ;
    private DatabaseManager databaseManager;
    private PlayMusic mService;
    private Boolean mBound = false;
    private Handler mHandler;
    private TextView barname;
    private ImageButton barnext;
    private ImageButton barplay;
    private ImageButton barback;
    private ImageView imageView;
    private PlaylistAdapter playlistAdapter;
    private SeekBar bar;
    private SharedPreferences preferences;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            preferences = getActivity().getSharedPreferences(PlayMusic.PLAYMUSIC, Context.MODE_PRIVATE);
            PlayMusic.LocalBinder binder = (PlayMusic.LocalBinder) service;

            mService = binder.getService();
            mBound = true;
            if(mService.isPlayed()){
                updateSeekBar();
                barname.setText(mService.getNamePlayer());
                if (mService.isPlaying()){
                    barplay.setBackgroundResource(R.drawable.pause);

                }else barplay.setBackgroundResource(R.drawable.play);

                if(mService.isOn()){
                    Picasso.with(getActivity()).load(preferences.getString("image", null)).into(imageView);
                }else {
                    if(preferences.getInt("idplaylist",9999)!=9999)
                        imageView.setImageBitmap(BitmapFactory.decodeFile(databaseManager.playlists().get(preferences.getInt("idplaylist",9999)).getmPathImage()));
                    else {
                        imageView.setBackgroundResource(R.drawable.photo_default);
                    }
                }
                barplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mService.isPlaying()) {
                            barplay.setBackgroundResource(R.drawable.play);
                            mService.pauseMusic();
                        }else {
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
                        } else {
                            if (preferences.getInt("idplaylist", 9999) == 9999) {
                                Intent intent = new Intent(getActivity(), ListenMusicActivity.class);
                                intent.putExtra("ID", preferences.getInt("idsong", 0));
                                intent.putExtra("bar", true);
                                intent.putExtra("size", manager.getListSong().size());
                                startActivityForResult(intent, 1);
                            } else {
                                Intent intent = new Intent(getActivity(), ListenMusicActivity.class);
                                intent.putExtra("PLAYLIST", true);
                                intent.putExtra("bar", true);
                                intent.putExtra("id", preferences.getInt("idplaylist", 9999));
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_host_fragment, container, false);
        mHandler = new Handler() ;
        barback = (ImageButton) view.findViewById(R.id.bar_back);
        barname = (TextView)view.findViewById(R.id.bar_name);
        barnext = (ImageButton) view.findViewById(R.id.bar_next);
        barplay = (ImageButton) view.findViewById(R.id.bar_play);
        bar = (SeekBar) view.findViewById(R.id.barbar);
        imageView = (ImageView) view.findViewById(R.id.imagebar);
        //
        addPlaylistButton = (FloatingActionButton)view.findViewById(R.id.add_playlist);
        addPlaylistButton.setIcon(R.drawable.playlist);
        addPlaylistButton.setTitle("Playlist");
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                startActivity(intent);
            }
        });
        //
        manager = new SongManager(getActivity().getContentResolver());
        adapter =  new SongAdapter(manager.getListSong());
        listsong = (ListView) view.findViewById(R.id.list_song);
        listsong.setAdapter(adapter);
        listsong.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listsong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ListenMusicActivity.class);
                intent.putExtra("ID",i);
                intent.putExtra("size", manager.getListSong().size());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idplaylist", 9999);
                editor.commit();
                startActivityForResult(intent, 1);
            }
        });
        //
        databaseManager = new DatabaseManager(getActivity());
        databaseManager.open();
        playlistAdapter=new PlaylistAdapter(databaseManager.playlists(), getActivity());
        listplaylist = (RecyclerView) view.findViewById(R.id.recycler_horizontal);
        if(databaseManager.playlists().size()==0)
            listplaylist.setVisibility(View.GONE);
        else listplaylist.setVisibility(View.VISIBLE);
        listplaylist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listplaylist.setAdapter(playlistAdapter);
        return view;
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
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
    public void onResume() {
        Intent intent = new Intent(getActivity(), PlayMusic.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if(databaseManager.playlists().size()==0)
            listplaylist.setVisibility(View.GONE);
        else listplaylist.setVisibility(View.VISIBLE);
        playlistAdapter=new PlaylistAdapter(databaseManager.playlists(), getActivity());
        listplaylist.setAdapter(playlistAdapter);
        super.onResume();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        getActivity().unbindService(mConnection);
        super.onDestroy();
    }
}
