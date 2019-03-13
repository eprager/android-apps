package com.emmaprager.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private List<Official> list;
    private MainActivity mainAct;

    public OfficialAdapter (List<Official> l, MainActivity ma){
        this.list = l;
        mainAct = ma;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_item, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = list.get(position);
        holder.office.setText(official.getOffice());
        holder.nameParty.setText(official.getName() + " (" + official.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
