package com.emmaprager.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class NewsArticleDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NewsArticleDownloader";
    private NewsService newS;
    private String id;
    private final String APIKey = "&apiKey=d49967e189d545248ec3f4e517c92988";
    private final String articleURL = "https://newsapi.org/v2/top-headlines?sources=";

    public NewsArticleDownloader(NewsService ns, String id) {
        newS = ns;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... strings) {
        String finalURL = articleURL+id+APIKey;
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
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Article> articles = parseJSON(s);
        newS.setArticles(articles);
    }

    private ArrayList<Article> parseJSON(String json){
        ArrayList<Article> articleList = new ArrayList<>();
        try{
            JSONObject obj = new JSONObject(json);
            JSONArray articles = obj.getJSONArray("articles");
            for(int i=0; i<articles.length(); i++){
                JSONObject article = articles.getJSONObject(i);
                String author=null, title=null, description=null, url=null, urlToImage=null, publishedAt=null;
                if(article.has("author")){
                    author = article.getString("author");
                }
                if(article.has("title")){
                    title = article.getString("title");
                }
                if(article.has("description")){
                    description = article.getString("description");
                }
                if(article.has("url")){
                    url = article.getString("url");
                }
                if(article.has("urlToImage")){
                    urlToImage = article.getString("urlToImage");
                }
                if(article.has("publishedAt")){
                    publishedAt = article.getString("publishedAt");
                }
                Article a = new Article(author, title, description, url, urlToImage, publishedAt);
                articleList.add(a);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return articleList;
    }
}
