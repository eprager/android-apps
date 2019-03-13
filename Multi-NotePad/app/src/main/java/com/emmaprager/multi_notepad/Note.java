package com.emmaprager.multi_notepad;

import java.io.Serializable;

public class Note implements Serializable {

    private String title;
    private String date;
    private String text;

    public Note(String t, String d, String x) {
        title = t;
        date = d;
        text = x;
    }

    public Note() {
        title = "Title";
        date = "Date";
        text = "Text";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPreview() {
        if (text.length() > 80) {
            return (text.substring(0, 80) + "...");
        } else {
            return text;
        }
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", input='" + text + '\'' +
                '}';
    }
}