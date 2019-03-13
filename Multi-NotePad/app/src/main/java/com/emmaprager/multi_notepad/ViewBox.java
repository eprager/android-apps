package com.emmaprager.multi_notepad;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewBox extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView updatedDate;
    public TextView preview;

    public ViewBox(View view){
        super(view);
        title = view.findViewById(R.id.Title);
        updatedDate = view.findViewById(R.id.Updated);
        preview = view.findViewById(R.id.Preview);
    }
}
