package com.emmaprager.multi_notepad;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class NoteOrganizer extends RecyclerView.Adapter<ViewBox> {
    private List<Note> noteList;
    private MainActivity mainA;

    public NoteOrganizer(List<Note> notelst, MainActivity ma){
        this.noteList = notelst;
        mainA = ma;
    }

    @Override
    public ViewBox onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.anote, parent, false);
        itemView.setOnClickListener(new MainActivity());
        itemView.setOnLongClickListener(new MainActivity());
        return new ViewBox(itemView);
    }

    public void onBindViewHolder(ViewBox holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.updatedDate.setText(note.getDate());
        holder.preview.setText(note.getPreview());
    }

    public int getItemCount() {
        return noteList.size();
    }
}
