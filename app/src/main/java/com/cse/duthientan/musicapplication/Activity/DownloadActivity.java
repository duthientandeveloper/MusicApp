package com.cse.duthientan.musicapplication.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cse.duthientan.musicapplication.Adapter.SongAdapter;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {
    ListView view ;
    List<Song> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        view = (ListView) findViewById(R.id.list);
        list = new ArrayList<>();

        File yourDir = new File(Environment.getExternalStorageDirectory(),"musicapp");
        for (File f : yourDir.listFiles()) {
            if (f.isFile()&&f.getName().contains(".mp3"))
            {
               list.add(new Song(f.getName(),f.getPath()));
            }
        }

        view.setAdapter(new SongAdapter(list));
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File mFile = new File(list.get(i).getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(mFile), "audio/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
