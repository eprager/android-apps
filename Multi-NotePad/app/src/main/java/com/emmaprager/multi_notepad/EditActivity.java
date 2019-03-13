package com.emmaprager.multi_notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private Note n;
    private EditText title;
    private EditText note;
    private static final int SAVED_NOTE = 3;
    private static final int NO_EDITS_MADE = 4;
    private static final int NOTE_NOT_SAVED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.editTitle);
        note = findViewById(R.id.editText);
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_NOTE")){
            n = (Note) intent.getSerializableExtra("EDIT_NOTE");
            title.setText(n.getTitle());
            note.setText(n.getText());
        }
        else {
            n=new Note();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Save:
                if(title.getText().toString().matches("")){
                    Toast.makeText(this, "Please add a title.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else if (n.getTitle().equals(title.getText().toString()) && n.getText().equals(note.getText().toString())){
                    setResult(NO_EDITS_MADE);
                    finish();
                    return false;
                }
                else {
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyy 'at' h:mm a");
                    String saveTime = sdf.format(now);
                    n.setTitle(title.getText().toString());
                    n.setText(note.getText().toString());
                    n.setDate(saveTime);
                    Intent returnData = new Intent();
                    returnData.putExtra("SAVED_NOTE", n);
                    setResult(SAVED_NOTE, returnData);
                    finish();
                    return true;
                }
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(title.getText().toString().matches("")){
                    Toast.makeText(EditActivity.this, "Please add a title.", Toast.LENGTH_SHORT).show();
                }
                else if (n.getTitle().equals(title.getText().toString()) && n.getText().equals(note.getText().toString())){
                    setResult(NO_EDITS_MADE);
                    finish();
                }
                else {
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyy 'at' h:mm a");
                    String saveTime = sdf.format(now);
                    n.setTitle(title.getText().toString());
                    n.setText(note.getText().toString());
                    n.setDate(saveTime);
                    Intent returnData = new Intent();
                    returnData.putExtra("SAVED_NOTE", n);
                    setResult(SAVED_NOTE, returnData);
                    finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(NOTE_NOT_SAVED);
                finish();
            }
        });

        builder.setTitle("Note Unsaved!");
        builder.setMessage("Would you like to save your note?");

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("TITLE", title.getText().toString());
        outState.putString("NOTES", note.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        note.setText(savedInstanceState.getString("NOTES"));
        title.setText(savedInstanceState.getString("TITLE"));
        String jsonFile = "Notes.json";
        MainActivity ma = new MainActivity();
        new AsyncLoadFile(ma).execute(jsonFile);
    }
}
