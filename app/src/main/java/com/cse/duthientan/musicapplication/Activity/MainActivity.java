package com.cse.duthientan.musicapplication.Activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cse.duthientan.musicapplication.R;
import com.cse.duthientan.musicapplication.TabHost.Offline;
import com.cse.duthientan.musicapplication.TabHost.Online;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends AppCompatActivity {
    private FragmentTabHost tabHost;
    CallbackManager callbackManager;
    FacebookDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("OFFLINE"), Offline.class, null);
        tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("ONLINE"), Online.class, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.download:
                Intent intent = new Intent(MainActivity.this,DownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.share:
                FacebookSdk.sdkInitialize(MainActivity.this);
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(MainActivity.this);
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException e) {

                    }
                });
                share();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void share() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.facebook.com/meoxauxa.kiss"))
                .build();

        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(content);
        } else {
            Toast.makeText(getApplicationContext(), "You don't seem to have facebook installed on this device", Toast.LENGTH_SHORT).show();
        }
    }
}

