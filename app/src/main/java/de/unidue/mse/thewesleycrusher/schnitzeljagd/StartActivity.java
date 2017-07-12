package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
        Button neuesspiel = (Button) findViewById(R.id.button_neuesspiel );
        neuesspiel.setOnClickListener(this);

      

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_start:
                startActivity(new Intent(StartActivity.this, AuswahlRouteActivity.class));
                break;

            case R.id.button_laden:
                startActivity(new Intent(StartActivity.this, MapsActivity.class));
                break;

            case R.id.button_einstellungen:
                startActivity(new Intent(StartActivity.this, SettingsActivity.class));
                break;

            case R.id.button_neuesspiel:
                startActivity(new Intent(StartActivity.this, NewGameActivity.class));
                break;
        }

    }

}




