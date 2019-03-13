package com.emmaprager.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String STORY_LIST = "STORY_LIST";
    private HashMap<String, ArrayList<NewsSource>> sourcesMap = new HashMap<>();
    private ArrayList<NewsSource> sources = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<Article> articles = new ArrayList<>();
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager vp;
    private Menu main;
    private DrawerLayout dLayout;
    private ListView dList;
    private ActionBarDrawerToggle dToggle;
    private NewsReceiver nReceiver = new NewsReceiver();
    private String categorySelected = "All";
    private int sourceSelected = -1;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent in = new Intent(this, NewsService.class);
        startService(in);
        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(nReceiver, filter);

        dLayout = findViewById(R.id.drawerLayout);
        dList = findViewById(R.id.leftDrawer);
        dList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list, sources));
        dList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        sourceSelected = position;
                        selectItem(sourceSelected);
                    }
                }
        );
        dToggle = new ActionBarDrawerToggle(this, dLayout, R.string.drawer_open, R.string.drawer_close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        vp = findViewById(R.id.viewPager);
        vp.setAdapter(pageAdapter);

        if(sources.isEmpty()){
            new NewsSourceDownloader(this,categorySelected).execute();
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
        unregisterReceiver(nReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CAT_SEL", categorySelected);
        outState.putString("Action_Bar_Title", (String) getTitle());
        outState.putSerializable("Articles", articles);
        outState.putInt("pagePosition", vp.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        setTitle(savedInstanceState.getString("Action_Bar_Title"));

        categorySelected = savedInstanceState.getString("CAT_SEL");
        new NewsSourceDownloader(this,categorySelected).execute();

        articles = (ArrayList<Article>) savedInstanceState.getSerializable("Articles");
        page = savedInstanceState.getInt("pagePosition");

        for (int i = 0; i < pageAdapter.getCount(); i++) {
            pageAdapter.notifyChangeInPosition(i);
        }
    }

    @Override
    protected void onResume() {
        if(!articles.isEmpty()){
            vp.setBackground(null);
            Intent intent = new Intent();
            intent.setAction(ACTION_NEWS_STORY);
            intent.putExtra(STORY_LIST, articles);
            intent.putExtra("Page", page);
            sendBroadcast(intent);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        main = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (dToggle.onOptionsItemSelected(item)) {
            return true;
        }
        categorySelected = item.toString();
        new NewsSourceDownloader(this,categorySelected).execute();
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        vp.setBackground(null);
        setTitle(sources.get(position).getName());
        Intent in = new Intent();
        in.setAction(ACTION_MSG_TO_SERVICE);
        in.putExtra("SourceID", sources.get(position).getId());
        sendBroadcast(in);
        dLayout.closeDrawer(dList);
    }

    public void setSources(ArrayList<NewsSource> listIn, ArrayList<String> catList){
        sourcesMap.clear();
        sources.clear();
        for (NewsSource s : listIn) {
            if (!sourcesMap.containsKey(s.getCategory())) {
                sourcesMap.put(s.getCategory(), new ArrayList<NewsSource>());
            }
            sourcesMap.get(s.getCategory()).add(s);
        }
        sourcesMap.put("All", listIn);
        sources.addAll(listIn);

        ((ArrayAdapter) dList.getAdapter()).notifyDataSetChanged();

        if(categories.isEmpty()){
            categories = catList;
            categories.add(0,"All");
            if(main == null){
                invalidateOptionsMenu();
                if(main == null){
                    return;
                }
            }
            for(String c: categories){
                main.add(c);
            }
        }
    }

    /***********************NEWS RECEIVER CLASS*************************************/
    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case ACTION_NEWS_STORY:
                    articles = (ArrayList<Article>) intent.getSerializableExtra(STORY_LIST);
                    page = intent.getIntExtra("Page", 0);
                    reDoFragments(articles, page);
                    break;
            }
        }

        public void reDoFragments(ArrayList<Article> a, int p){
            for (int i = 0; i < pageAdapter.getCount(); i++) {
                pageAdapter.notifyChangeInPosition(i);
            }
            fragments.clear();
            int count = a.size();
            for (int i = 0; i < count; i++) {
                fragments.add(NewsFragment.newInstance(a.get(i),(i+1)+" of "+count));
            }
            pageAdapter.notifyDataSetChanged();
            vp.setCurrentItem(p);
        }
    }

    /***********************PAGE ADAPTER CLASS*************************************/
    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }
}
