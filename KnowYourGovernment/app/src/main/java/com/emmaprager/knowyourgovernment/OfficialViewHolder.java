package com.emmaprager.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    public TextView office;
    public TextView nameParty;

    public OfficialViewHolder(View view){
        super(view);
        office = view.findViewById(R.id.Office);
        nameParty = view.findViewById(R.id.Official);
    }
}