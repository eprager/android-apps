package com.emmaprager.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsSourceDownloader extends AsyncTask<String, Void, String>{

    private static final String TAG = "NewsSourceDownloader";

    private MainActivity mainAct;
    private final String APIKey = "&apiKey=d49967e189d545248ec3f4e517c92988";
    private String sourceURL = "https://newsapi.org/v2/sources?country=us&category=";
    private String category;

    public NewsSourceDownloader(MainActivity ma, String c) {
        mainAct = ma;
        if(c.equals("All")){
            category = "";
        } else if (c.equals("")){
            category=c;
        } else {
            category=c;
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String finalURL = sourceURL + category + APIKey;
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        try{
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;

            while((line=reader.readLine()) != null){
                sb.append(line);
            }

            return sb.toString();

        } catch (Exception e){
            Log.d(TAG, "doInBackground: "+e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Object[] json = parseJSON(s);
        ArrayList<NewsSource> sources = (ArrayList<NewsSource>) json[0];
        ArrayList<String> category = (ArrayList<String>) json[1];
        mainAct.setSources(sources, category);
    }

    private Object[] parseJSON(String json){
        ArrayList<NewsSource> sourceList = new ArrayList<>();
        ArrayList<String> categoryList = new ArrayList<>();
        try{
            JSONObject obj = new JSONObject(json);
            JSONArray sources = obj.getJSONArray("sources");
            for(int i=0; i<sources.length(); i++){
                JSONObject source = sources.getJSONObject(i);
                String ID = source.getString("id");
                String name = source.getString("name");
                String url = source.getString("url");
                String category = source.getString("category");
                NewsSource ns = new NewsSource(ID, name, url, category);
                sourceList.add(ns);
                if(!categoryList.contains(category)){
                    categoryList.add(category);
                }
            }

        } catch (Exception e){
            Log.d(TAG, "parseJSON: exception: "+e.getMessage());
        }

        return new Object[]{sourceList,categoryList};
    }
}