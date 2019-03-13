package com.emmaprager.multi_notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private List<Note> noteList;
    private RecyclerView rv;
    private NoteOrganizer notes;
    private static final int NEW_NOTE_CODE = 1;
    private static final int EDIT_NOTE_CODE = 2;
    private static final int SAVED_NOTE = 3;
    private static final int NO_EDITS_MADE = 4;
    //private static final int NOTE_NOT_SAVED = 5;
    private Note editedNote;
    private int editedNotePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String jsonFile = "Notes.json";
        new AsyncLoadFile(this).execute(jsonFile);
    }

    @Override
    protected void onPause() {
        saveNoteList();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        editedNotePos = rv.getChildLayoutPosition(view);
        editedNote = noteList.get(editedNotePos);
        Intent edit = new Intent(MainActivity.this, EditActivity.class);
        edit.putExtra("EDIT_NOTE", editedNote);
        startActivityForResult(edit,EDIT_NOTE_CODE);
    }

    @Override
    public boolean onLongClick(View view) {
        final int pos = rv.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteList.remove(pos);
                notes.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure?");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Add:
                Intent add = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(add,NEW_NOTE_CODE);
                return true;
            case R.id.About:
                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(about);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_NOTE_CODE) {
            if (resultCode == SAVED_NOTE){
                Note newNote = (Note) data.getSerializableExtra("SAVED_NOTE");
                noteList.add(0,newNote);
                notes.notifyDataSetChanged();
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == EDIT_NOTE_CODE) {
            if (resultCode == SAVED_NOTE){
                editedNote = (Note) data.getSerializableExtra("SAVED_NOTE");
                noteList.remove(editedNotePos);
                noteList.add(0,editedNote);
                notes.notifyDataSetChanged();
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == NO_EDITS_MADE){
                Toast.makeText(this, "Note Unchanged", Toast.LENGTH_SHORT).show();
            }
        }
        notes.notifyDataSetChanged();
    }

    public void whenAsyncIsDone(List<Note> nl) {
        noteList = nl;
        if (noteList != null) {
            rv = findViewById(R.id.Recycler);
            notes = new NoteOrganizer(noteList, this);

            rv.setAdapter(notes);
            rv.setLayoutManager(new LinearLayoutManager(this));
            notes.notifyDataSetChanged();
        }
    }

    public void saveNoteList() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("Notes.json", Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note n: noteList){
                writer.beginObject();
                writer.name("Title").value(n.getTitle());
                writer.name("Date").value(n.getDate());
                writer.name("Text").value(n.getText());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        }

        catch (Exception e) {
            e.getStackTrace();
        }
    }
}
