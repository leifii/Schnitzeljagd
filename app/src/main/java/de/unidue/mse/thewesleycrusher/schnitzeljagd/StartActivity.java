package de.unidue.mse.thewesleycrusher.schnitzeljagd;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;


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




        //Check wether Directory to save data to exists, if it does not exist, it will be created
        String myDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File directory = new File(myDirectoryPath, "Schnitzeljagd");
        if(!directory.exists()){
            directory.mkdir();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_start:
                //startActivity(new Intent(this, AuswahlRouteActivity.class));
                Intent intent = new Intent(StartActivity.this, AuswahlRouteActivity.class);
                startActivity(intent);
                break;

            case R.id.button_laden:
                startActivity(new Intent(StartActivity.this, MapsActivity.class));
                break;

            case R.id.button_anleitung:
                startActivity(new Intent(StartActivity.this, AnleitungActivity.class));
                break;

            case R.id.button_neuesspiel:
                startActivity(new Intent(StartActivity.this, NewGameActivity.class));
                break;
        }

    }

}




