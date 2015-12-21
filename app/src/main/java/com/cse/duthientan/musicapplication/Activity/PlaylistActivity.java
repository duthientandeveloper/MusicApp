package com.cse.duthientan.musicapplication.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cse.duthientan.musicapplication.Adapter.SongCheckAdapter;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.database.DatabaseManager;
import com.cse.duthientan.musicapplication.model.Playlist;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongManager;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA = 1;
    private final int SELECT_FILE = 2;
    private SongCheckAdapter adapter;
    private ImageView imagePlaylist;
    private SongManager manager;
    private ListView songcheck;
    private DatabaseManager databaseManager;
    private EditText namePlaylist;
    private EditText decriptionPlaylist;
    private String pathImagePlaylist = "";
    private int id;
    private int mID;
    private FloatingActionButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        //
        save = (FloatingActionButton) findViewById(R.id.save);
        save.setIcon(R.drawable.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getBooleanExtra("EDIT", false))
                    update();
                else
                    save();
            }
        });
        namePlaylist = (EditText) findViewById(R.id.name_playlist);
        decriptionPlaylist = (EditText) findViewById(R.id.des_playlist);
        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        manager = new SongManager(getContentResolver());
        //
        imagePlaylist = (ImageView) findViewById(R.id.photo_playlist);
        if (getIntent().getBooleanExtra("EDIT", false)) {
            id = getIntent().getIntExtra("ID", 0);
            List<Playlist> playlists = databaseManager.playlists();
            Playlist playlist = playlists.get(id);
            namePlaylist.setText(playlist.getmNamePlaylist());
            decriptionPlaylist.setText(playlist.getmDecriptionPlaylist());
            imagePlaylist.setImageBitmap(BitmapFactory.decodeFile(playlist.getmPathImage()));
            mID = playlist.getmId();
            pathImagePlaylist = playlist.getmPathImage();
            adapter = new SongCheckAdapter(manager.getListSong(), playlist.getmListSong());
        } else adapter = new SongCheckAdapter(manager.getListSong());

        imagePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        songcheck = (ListView) findViewById(R.id.list_song_check);
        songcheck.setAdapter(adapter);
        //
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                pathImagePlaylist = destination.getPath();
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePlaylist.setImageBitmap(thumbnail);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                pathImagePlaylist = selectedImagePath;
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                imagePlaylist.setImageBitmap(bm);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        System.out.println(pathImagePlaylist);
        if (adapter.getListSongChecked().size() != 0) {
            long rownew = databaseManager.insertPlaylist(namePlaylist.getText().toString(), pathImagePlaylist, decriptionPlaylist.getText().toString());
            for (Song s : adapter.getListSongChecked()) {
                databaseManager.insertSong(namePlaylist.getText().toString(), s.getPath(), s.getName(), String.valueOf(rownew));
            }
            databaseManager.close();
            finish();
        } else {
            Toast.makeText(getApplicationContext(),"Must add music to playlist",Toast.LENGTH_LONG).show();
        }
    }

    private void update() {
        databaseManager.update(String.valueOf(mID), decriptionPlaylist.getText().toString(), pathImagePlaylist, namePlaylist.getText().toString());
        for (Song s : adapter.getListSongChecked()) {
            databaseManager.insertSong(namePlaylist.getText().toString(), s.getPath(), s.getName(), String.valueOf(mID));
        }
        databaseManager.close();
        finish();
    }
}
