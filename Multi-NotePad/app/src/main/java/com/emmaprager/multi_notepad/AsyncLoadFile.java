package com.emmaprager.multi_notepad;

import android.os.AsyncTask;
import android.util.JsonReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AsyncLoadFile extends AsyncTask<String, Void, ArrayList<Note>>{
    private MainActivity mainA;

    public AsyncLoadFile(MainActivity ma) {
        mainA = ma;
    }

    @Override
    protected ArrayList<Note> doInBackground(String... strings) {
        ArrayList<Note> nl;
        nl = loadNotes(strings[0]);
        return nl;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(ArrayList<Note> notes) {
        super.onPostExecute(notes);
        mainA.whenAsyncIsDone(notes);
    }

    private ArrayList<Note> loadNotes(String filename){
        ArrayList<Note> jsonContents = new ArrayList<Note>();
        try {
            InputStream is = mainA.getApplicationContext().openFileInput(filename);
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

            reader.beginArray();
            while (reader.hasNext()) {
                String title = null;
                String date = null;
                String input = null;

                reader.beginObject();
                while(reader.hasNext()){
                    String name = reader.nextName();
                    if(name.equals("Title")){
                        title = reader.nextString();
                    }
                    else if (name.equals("Date")){
                        date = reader.nextString();
                    }
                    else if (name.equals("Input")){
                        input = reader.nextString();
                    }
                    else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                jsonContents.add(new Note(title, date, input));
            }
            reader.endArray();
            reader.close();
            is.close();
            return jsonContents;

        } catch (FileNotFoundException e) {
            return jsonContents;
        } catch (Exception e) {
            e.printStackTrace();
            return jsonContents;
        }
    }
}