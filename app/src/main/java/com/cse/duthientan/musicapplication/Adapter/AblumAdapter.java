package com.cse.duthientan.musicapplication.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.model.Ablum;
import com.cse.duthientan.musicapplication.model.AblumHot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Du Thien Tan on 11/1/2015.
 */
public class AblumAdapter extends BaseAdapter {
    private List<Ablum> mAblum;
    private Context mContext;

    public AblumAdapter(List<Ablum> ablum,Context context){
        mAblum =  ablum;
        mContext = context;
    }



    @Override
    public int getCount() {
        return mAblum.size();
    }

    @Override
    public Object getItem(int i) {
        return mAblum.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        view=View.inflate(context, R.layout.item_ablum,null);
        Ablum ablum = mAblum.get(i);
        final ImageView photo =  (ImageView) view.findViewById(R.id.item_playlist_photo);
        TextView name = (TextView)view.findViewById(R.id.item_playlist_name);
        name.setText(ablum.getmName());
        Picasso.with(mContext)
                .load(mAblum.get(i).getmImage())
                .into(photo);
        return view;
    }
}
