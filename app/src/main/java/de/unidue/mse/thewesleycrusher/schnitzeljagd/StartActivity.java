package de.unidue.mse.thewesleycrusher.schnitzeljagd;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    public final int REQUEST_ID = 200;
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
                }
                startActivity(new Intent(StartActivity.this, EntwicklerLoginActivity.class));
                break;
        }

    }

}




