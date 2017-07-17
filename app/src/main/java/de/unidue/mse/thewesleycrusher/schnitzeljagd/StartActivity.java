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
       Button anleitung = (Button) findViewById(R.id.button_anleitung);
        anleitung.setOnClickListener(this);
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

            case R.id.button_anleitung:
                startActivity(new Intent(StartActivity.this, AnleitungActivity.class));
                break;

            case R.id.button_neuesspiel:
                startActivity(new Intent(StartActivity.this, EntwicklerLoginActivity.class));
                break;
        }

    }

}




