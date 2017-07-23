package de.unidue.mse.thewesleycrusher.schnitzeljagd;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private String mainDirectoryPath;

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







        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }





        //Check wether Directory to save data to exists, if it does not exist, it will be created



        File mainDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Schnitzeljagd");

        if(!mainDirectory.exists()){
            mainDirectory.mkdir();
        }
        mainDirectoryPath = mainDirectory.getAbsolutePath();

        File routsDirectory = new File(mainDirectoryPath, "Routen");
        if(!routsDirectory.exists()){
            routsDirectory.mkdir();
        }
        File saveFilesDirectory = new File(mainDirectoryPath, "Savefiles");
        if(!saveFilesDirectory.exists()){
            saveFilesDirectory.mkdir();
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




