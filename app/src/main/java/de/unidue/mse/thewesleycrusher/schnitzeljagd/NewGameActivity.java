package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewGameActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Button foto =(Button) findViewById(R.id.button_setphoto);
        foto.setOnClickListener(this);

        Button gps =(Button) findViewById(R.id.button_setgps);
        gps.setOnClickListener(this);

        Button naechster =(Button) findViewById(R.id.button_nextstop);
        foto.setOnClickListener(this);

        Button ende =(Button) findViewById(R.id.button_abschließen);
        foto.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_setphoto:
                dispatchTakePictureIntent();
                break;
            case R.id.button_setgps:

                break;
            case R.id.button_nextstop:

                break;
            case R.id.button_abschließen:

                break;

        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
