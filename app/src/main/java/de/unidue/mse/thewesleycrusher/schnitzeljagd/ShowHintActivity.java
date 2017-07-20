package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class ShowHintActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_hint);

        textView = (TextView) findViewById(R.id.textViewShowHint);

        Intent intent = getIntent();
        String hint = intent.getStringExtra(SpielActivity.EXTRA_MESSAGE);

        /*
        Intent intent2=getIntent();
        String data = intent2.getStringExtra(NewGameActivity.EXTRA_MESSAGE);
           */
        textView.setText(hint);
        //textView.setText(data);

    }


}
