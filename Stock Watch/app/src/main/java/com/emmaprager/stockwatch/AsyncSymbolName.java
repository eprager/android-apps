package com.emmaprager.stockwatch;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AsyncSymbolName extends AsyncTask<String,Void,String> {
    private static final String TAG = "AsyncVSymbolName";
    private MainActivity mainAct;
    private final String dataURL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private String input;
    public AsyncSymbolName(MainActivity ma) {
        mainAct = ma;
    }


    @Override
    protected String doInBackground(String... strings) {
        input = strings[0];
        String finalURL = dataURL;
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: "+urlToUse);


        StringBuilder sb = new StringBuilder();
        String result;

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line=reader.readLine()) != null){
                sb.append(line).append('\n');
            }

            result = sb.toString();
            result = result.substring(result.indexOf("["),result.indexOf("]")+1);
            Log.d(TAG, "doInBackground: "+result);
            return result;
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: "+e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        final ArrayList<String[]> sList = parseJSON(s);
        final ArrayList <String[]> fList = new ArrayList<>();
        Log.d(TAG, "onPostExecute: "+input);

        int x;
        for (String[] stk : sList){
            if (stk[0].startsWith(input)){
                fList.add(stk);
                Log.d(TAG, "onPostExecute: "+stk[0]);
            }
        }
        if(fList == null || fList.size()==0){
            Log.d(TAG, "onPostExecute: no stocks found");
            AlertDialog.Builder adb = new AlertDialog.Builder(mainAct);
            adb.setMessage("No stocks match the symbol: "+input);
            adb.setTitle("No Stock Found");
            AlertDialog dialog = adb.create();
            dialog.show();

        } else if (fList.size()==1){
            Log.d(TAG, "onPostExecute: one stock found");
            mainAct.processNewStock(fList.get(0)[0]);
        } else {
            Log.d(TAG, "onPostExecute: many stocks found");
            final CharSequence[] stockArr = new CharSequence[fList.size()];
            for (int i = 0; i < fList.size(); i++)
                stockArr[i] = fList.get(i)[0] + '\n' + fList.get(i)[1];

            AlertDialog.Builder adb = new AlertDialog.Builder(mainAct);
            adb.setTitle("Make a selection");

            adb.setItems(stockArr, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String symbol = fList.get(which)[0];
                    mainAct.processNewStock(symbol);
                }
            });

            adb.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(TAG, "onClick: user cancelled dialog");
                }
            });
            AlertDialog dialog = adb.create();
            dialog.show();
        }
    }

    private ArrayList<String[]> parseJSON(String s){
        ArrayList<String[]> stocks = new ArrayList<>();
        try{
            JSONArray jObjMain = new JSONArray(s);
            for(int i=0; i<jObjMain.length(); i++){
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("symbol");
                String companyName = jStock.getString("name");

                if(!symbol.contains(".")){
                    String[] stockKey = {symbol,companyName};
                    stocks.add(stockKey);
                }
            }
            return stocks;
        } catch (Exception e){
            Log.d(TAG, "parseJSON: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}