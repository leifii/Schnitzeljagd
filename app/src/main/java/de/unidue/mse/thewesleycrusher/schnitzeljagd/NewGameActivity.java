package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class NewGameActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Button foto = (Button) findViewById(R.id.button_setphoto);
        foto.setOnClickListener(this);

        Button gps = (Button) findViewById(R.id.button_setgps);
        gps.setOnClickListener(this);

        Button naechster = (Button) findViewById(R.id.button_nextstop);
        foto.setOnClickListener(this);

        Button ende = (Button) findViewById(R.id.button_abschließen);
        foto.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_setphoto:

                break;
            case R.id.button_setgps:

                break;
            case R.id.button_nextstop:

                break;
            case R.id.button_abschließen:

                break;

        }
    }
}



