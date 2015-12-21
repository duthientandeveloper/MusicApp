package com.cse.duthientan.musicapplication.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.duthientan.musicapplication.Activity.ListenMusicActivity;
import com.cse.duthientan.musicapplication.Activity.MainActivity;
import com.cse.duthientan.musicapplication.Activity.PlaylistActivity;
import com.cse.duthientan.musicapplication.database.DatabaseManager;
import com.cse.duthientan.musicapplication.model.Playlist;
import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.service.PlayMusic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/13/2015.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private Context context ;
    private List<Playlist> playlists = new ArrayList<>();
    private DatabaseManager databaseManager;
    private SharedPreferences preferences;
    public interface MyListener {
        // you can define any parameter as per your requirement
        public void callback();
    }

    public PlaylistAdapter(List<Playlist> list,Context mContext) {
        this.playlists = list;
        this.context = mContext;
        databaseManager = new DatabaseManager(context);
        preferences =  mContext.getSharedPreferences(PlayMusic.PLAYMUSIC,Context.MODE_PRIVATE);
        databaseManager.open();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mcontext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View contactView = inflater.inflate(R.layout.item_playlsit, parent, false);
        PlaylistViewHolder viewHolder = new PlaylistViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, final int position) {
        Playlist playlist = playlists.get(position);

        ImageView photo = holder.photo;
        TextView name = holder.name;
        LinearLayout card = holder.card;
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context,ListenMusicActivity.class);
                intent.putExtra("PLAYLIST",true);
                intent.putExtra("id",position);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idplaylist", position);
                editor.commit();
                context.startActivity(intent);
            }
        });
        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                choicePlaylist(position);
                return true;
            }
        });
        photo.setBackgroundResource(R.drawable.photo_default);
        photo.setImageBitmap(BitmapFactory.decodeFile(playlist.getmPathImage()));
        name.setText(playlist.getmNamePlaylist());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;
        public TextView name;
        public LinearLayout card;

        public PlaylistViewHolder(View v) {
            super(v);
            card = (LinearLayout)v.findViewById(R.id.card);
            photo = (ImageView) v.findViewById(R.id.item_playlist_photo);
            name = (TextView) v.findViewById(R.id.item_playlist_name);
        }
    }
    private void choicePlaylist(final int id) {
        final CharSequence[] items = { "Edit Playlist", "Delete Playlist", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Playlist!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Edit Playlist")) {
                    Intent intent = new Intent(context, PlaylistActivity.class);
                    intent.putExtra("EDIT", true);
                    intent.putExtra("ID", id);
                    context.startActivity(intent);
                } else if (items[item].equals("Delete Playlist")) {
                    int i = databaseManager.playlists().get(id).getmId();

                    if (id == preferences.getInt("idplaylist", 9999)) {
                        Toast.makeText(context,"Playlist is listening",Toast.LENGTH_LONG).show();
                        return;
                    }
                    databaseManager.clearPlaylist(String.valueOf(i));
                    playlists.remove(id);
                    notifyItemRemoved(id);
                    notifyItemRangeChanged(id, getItemCount());

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}


