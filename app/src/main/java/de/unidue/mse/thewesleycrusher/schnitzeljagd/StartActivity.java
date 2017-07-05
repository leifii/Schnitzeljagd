package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

       Button start = (Button) findViewById(R.id.button_start);
        start.setOnClickListener(this);
       Button laden = (Button) findViewById(R.id.button_laden);
        laden.setOnClickListener(this);
       Button einstellungen = (Button) findViewById(R.id.button_einstellungen);
        einstellungen.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_start:
                startActivity(new Intent(StartActivity.this, MapsActivity.class));
                break;

            case R.id.button_laden:
                startActivity(new Intent(StartActivity.this, MapsActivity.class));
                break;

            case R.id.button_einstellungen:
                startActivity(new Intent(StartActivity.this, SettingsActivity.class));
                break;
        }
    }
}
