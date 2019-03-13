package com.emmaprager.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private TextView office, name, header;
    private ImageView pic;
    private ConstraintLayout background;
    private Official official;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        header = findViewById(R.id.location);
        office = findViewById(R.id.Office);
        name = findViewById(R.id.Name);
        pic = findViewById(R.id.Picture);
        background = findViewById(R.id.Official_Background);

        Intent intent = getIntent();
        if(intent.hasExtra("header")){
            location = intent.getStringExtra("header");
            header.setText(location);
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");
            setData();
            loadImage();
        }
    }

    private void setData(){
        office.setText(official.getOffice());
        name.setText(official.getName());
        if(official.getParty().contains("Republican")){
            background.setBackgroundColor(Color.RED);
        } else if(official.getParty().contains("Democrat") || official.getParty().contains("Democratic")){
            background.setBackgroundColor(Color.BLUE);
        } else {
            background.setBackgroundColor(Color.BLACK);
        }
    }

    private void loadImage(){
        if (official.getPhoto() != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Try https if the http image attempt failed
                    final String changedUrl = official.getPhoto().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerInside()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(pic);
                }
            }).build();
            picasso.load(official.getPhoto())
                    .fit()
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(pic);
        } else {
            Picasso.get().load(R.drawable.missingimage)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(pic);
        }

    }
}