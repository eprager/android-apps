package com.emmaprager.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView company;
    public TextView price;
    public TextView price_change;
    public TextView price_change_percent;

    MyViewHolder(View view) {
        super(view);
        symbol = view.findViewById(R.id.symbol);
        company = view.findViewById(R.id.company);
        price = view.findViewById(R.id.price);
        price_change = view.findViewById(R.id.price_change);
        price_change_percent = view.findViewById(R.id.price_change_percent);
    }

}

