package com.emmaprager.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import java.util.ArrayList;

import static com.emmaprager.newsgateway.MainActivity.ACTION_MSG_TO_SERVICE;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean isRunning = true;
    private ArrayList<Article> articleList = new ArrayList<>();
    private ServiceReceiver sReceiver = new ServiceReceiver();
    private NewsService ns = this;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(sReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(isRunning){
                    while(articleList.isEmpty()){
                        try{
                            Thread.sleep(250);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    sendArticles(articleList);
                }
            }
        }).start();

        return Service.START_STICKY;
    }

    public void setArticles(ArrayList<Article> a){
        articleList.clear();
        for(Article as: a){
            articleList.add(as);
        }
    }

    public void sendArticles(ArrayList<Article> sl){
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        intent.putExtra(MainActivity.STORY_LIST, sl);
        intent.putExtra("Page", 0);
        sendBroadcast(intent);
        articleList.clear();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sReceiver);
        isRunning = false;
        super.onDestroy();
    }

    class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case ACTION_MSG_TO_SERVICE:
                    String id = intent.getStringExtra("SourceID");
                    new NewsArticleDownloader(ns,id).execute();
                    break;
            }
        }
    }
}