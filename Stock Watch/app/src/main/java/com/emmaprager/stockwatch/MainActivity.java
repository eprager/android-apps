package com.emmaprager.stockwatch;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<Stock> stockList = new ArrayList<>();  // Main content is here
    private RecyclerView recyclerView; // Layout's recyclerview
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout
    private StockAdapter sAdapter; // Data to recyclerview adapter
    private DatabaseHandler databaseHandler;
    private static final String marketWatch = "https://www.marketwatch.com/investing/stock/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        sAdapter = new StockAdapter(stockList, this);
        databaseHandler = new DatabaseHandler(this);

        recyclerView.setAdapter(sAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });


        if(checkConnection()==true){
            refreshStocks();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Stocks cannot be loaded without a network connection.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void addNewStock(Stock stock) {
        if(stock != null){
            stockList.add(stock);
            Collections.sort(stockList, new Comparator<Stock>() {
                public int compare(Stock s1, Stock s2) {
                    return s1.getSymbol().compareTo(s2.getSymbol());
                }
            });
            DatabaseHandler.addStock(stock);
            sAdapter.notifyDataSetChanged();
        }
    }

    private void doRefresh() {
        if(checkConnection() == true){
            refreshStocks();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Stocks cannot be updated without a network connection.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        swiper.setRefreshing(false);

        Toast.makeText(this, "List content refreshed", Toast.LENGTH_SHORT).show();
    }

    private void refreshStocks(){
        ArrayList<String[]> temp = DatabaseHandler.loadStocks();
        stockList.clear();
        for(int i=0; i<temp.size();i++){
            new AsyncAllStockData(this).execute(temp.get(i)[0]);
        }
    }

    public void processNewStock(String symbol){
        boolean duplicate = false;
        for(int i=0; i<stockList.size();i++){
            if(stockList.get(i).getSymbol().equals(symbol)){
                duplicate = true;
            }
        }
        if(duplicate){
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Duplicate Stock");
            b.setMessage("Stock Symbol "+ symbol +" is already displayed");
            AlertDialog d = b.create();
            d.show();
        }
        else {
            new AsyncAllStockData(this).execute(symbol); //??
        }
    }

    private boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        String stock = stockList.get(pos).getCompany().toUpperCase();
        String webURL = "http://www.marketwatch.com/investing/stock/"+stock;
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webURL)));
        } catch (ActivityNotFoundException e) {
        }
    }

    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        final int pos = recyclerView.getChildLayoutPosition(v);
        final Stock stock = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                sAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Delete Stock "+ stock.getSymbol()+"("+stock.getCompany()+")?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Add:
                if(checkConnection()==true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final MainActivity ma = this;
                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                    et.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder.setView(et);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new AsyncSymbolName(ma).execute(et.getText().toString());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.setMessage("Please enter a stock Symbol:");
                    builder.setTitle("New Stock");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage("Stocks cannot be added without a network connection.");
                    builder.setTitle("No Network Connection");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }
}