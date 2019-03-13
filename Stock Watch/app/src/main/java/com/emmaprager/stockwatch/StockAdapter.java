package com.emmaprager.stockwatch;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Stock> stockList;
    private MainActivity mainAct;

    StockAdapter(List<Stock> sList, MainActivity ma) {
        this.stockList = sList;
        mainAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_entry, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.company.setText(stock.getCompany());
        holder.price.setText(Double.toString(stock.getPrice()));

        Double pc = stock.getPriceChangePercent();
        holder.price_change_percent.setText("("+String.format( "%.2f", pc )+"%)");

        if(stock.getPriceChange()<0){
            holder.symbol.setTextColor(Color.RED);
            holder.company.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.price_change_percent.setTextColor(Color.RED);
            holder.price_change.setTextColor(Color.RED);

            String changeWithSymbol = "\u25BC "+Double.toString(stock.getPriceChange());
            holder.price_change.setText(changeWithSymbol);
        }
        else{
            holder.symbol.setTextColor(Color.GREEN);
            holder.company.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.price_change.setTextColor(Color.GREEN);
            holder.price_change_percent.setTextColor(Color.GREEN);

            String changeWithSymbol = "\u25B2 "+Double.toString(stock.getPriceChange());
            holder.price_change.setText(changeWithSymbol);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
