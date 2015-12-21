package com.cse.duthientan.musicapplication.backend;

import android.content.Context;

import com.cse.duthientan.musicapplication.model.Ablum;
import com.cse.duthientan.musicapplication.model.AblumHot;
import com.cse.duthientan.musicapplication.model.Singer;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongOnline;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Du Thien Tan on 10/27/2015.
 */
public class Zingmp3API {
    private static final String URL = "http://mp3.zing.vn/";
    private Context mContext;

    public Zingmp3API(Context context) {
        this.mContext = context;
    }

    public interface OnBackendErrorListener {
        void onError(String error);
    }

    public interface OnBackendAlbumListener extends OnBackendErrorListener {
        void onCompleted(AblumHot ablumHot);
    }

    public interface OnBackendSongListener extends OnBackendErrorListener {
        void onCompleted(List<Song> list);
    }

    public interface OnBackendDataListener extends OnBackendErrorListener {
        void onCompleted(List<Song> list);
    }

    public void album(final OnBackendAlbumListener onBackendAlbumListener) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setTimeout(30000);
        httpClient.setMaxRetriesAndTimeout(5, 100);
        httpClient.setEnableRedirects(true);
        httpClient.get(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Ablum> list = new ArrayList<Ablum>();

                String response = new String(responseBody);
                Document doc = Jsoup.parse(response);
                Elements body = doc.select("div.wrap-content").first().children();
                Elements rowAblum = body.get(3).children().select("div.row");
                for (Element e : rowAblum) {
                    Elements linkAblum = e.children().select("a");
                    Elements imageAblum = e.children().select("img");
                    Elements nameAblum = e.children().select("h3");
                    Elements singerAblum = e.children().select("h4");

                    for (int i = 0; i < 4; i++) {
                        list.add(new Ablum(nameAblum.get(i).text(), linkAblum.get(i).attr("href"), imageAblum.get(i).attr("src")));
                    }
                }

                onBackendAlbumListener.onCompleted(new AblumHot(list));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                onBackendAlbumListener.onError(error.getMessage());
            }
        });
    }

    public void Data(final String URL, final OnBackendDataListener onBackendDataListener) {
        final String[] link = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(URL).get();
                    Element element = doc.select("div#html5player").first();
                    link[0] = element.attr("data-xml");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setTimeout(30000);
        httpClient.setMaxRetriesAndTimeout(5, 100);
        httpClient.setEnableRedirects(true);
        httpClient.get(link[0], new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Song> list = new ArrayList<>();
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(new String(responseBody)));
                    int eventType = xpp.getEventType();
                    List<String> name = new ArrayList<String>();
                    final List<String> link = new ArrayList<String>();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("title")) {
                            xpp.nextToken();
                            name.add(xpp.getText());
                        } else if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("source")) {
                            xpp.nextToken();
                            link.add(xpp.getText());
                        }
                        eventType = xpp.nextToken();
                    }
                    for (int i = 0; i < name.size(); i++) {
                        final int finalI = i;
                        final String[] s = {""};
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    java.net.URL url = new URL(link.get(finalI));
                                    HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                                    ucon.setInstanceFollowRedirects(false);
                                    s[0] = ucon.getHeaderField("Location");

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
                        thread.join();
                        list.add(new Song(name.get(i), s[0]));
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onBackendDataListener.onCompleted(list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                onBackendDataListener.onError(error.toString());
            }
        });
    }

    public void song( final OnBackendSongListener onBackendSongListener ){
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setTimeout(30000);
        httpClient.setMaxRetriesAndTimeout(5, 100);
        httpClient.setEnableRedirects(true);
        httpClient.post(URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Song> list= new ArrayList<Song>();
                String response = new String(responseBody);
                Document doc = Jsoup.parse(response);
                Elements body = doc.select("div.wrap-content").first().children();
                Elements rowAblum = body.get(5).children().select("div.row");
                Element e = rowAblum.get(0);
                    Elements linkAblum = e.children().select("a");
                    for (Element i:linkAblum){
                        if(i.text().equals("Nhạc Việt Hot")||i.text().equals("Nhạc Việt Mới"))
                            continue;
                        list.add(new Song(i.text(), i.attr("href")));

                    }
                List<Song> result = new ArrayList<Song>();
                for(int i=0;i<23;i++){
                    result.add(list.get(i*4));
                }
                onBackendSongListener.onCompleted(result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
