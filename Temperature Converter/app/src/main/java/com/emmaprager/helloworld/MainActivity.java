package com.emmaprager.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioButton;


public class MainActivity extends AppCompatActivity {

    public EditText temp;
    public TextView out;
    public RadioButton f2c;
    public RadioButton c2f;
    public TextView history;
    public double f;
    public double c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind a variable to the screen widgets
        temp = findViewById(R.id.temp);
        out = findViewById(R.id.output);
        f2c = findViewById(R.id.f2cbox);
        c2f = findViewById(R.id.c2fbox);
        history = findViewById(R.id.history);
    }

    public void buttonClicked(View v) {
        double ans = 0;
        double in = Double.parseDouble(temp.getText().toString());
        String historyText = history.getText().toString();

        if(f2c.isChecked()) {
            ans = (in-32.0)/1.8;
            String outString = String.format("%.1f",ans);
            out.setText(outString);
            history.setText("F to C: " + in + "->" + outString + "\n" + historyText);
        }

        else if(c2f.isChecked()) {
            ans = (in*1.8)/+32;
            String outString = String.format("%.1f",ans);
            out.setText(outString);
            history.setText("C to F: " + in + "->" + outString + "\n" + historyText);
        }
    }

}
