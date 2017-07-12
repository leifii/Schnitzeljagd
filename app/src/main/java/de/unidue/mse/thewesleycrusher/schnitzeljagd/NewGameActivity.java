package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.BitmapFactory;

import java.io.File;

import static de.unidue.mse.thewesleycrusher.schnitzeljagd.R.id.imageView;
import static de.unidue.mse.thewesleycrusher.schnitzeljagd.R.id.imageView_bildAnzeige;


public class NewGameActivity extends Activity implements View.OnClickListener {

    Intent bildintent;

    File bildfile = new File(Environment.getExternalStorageDirectory() + "/FotoApp/bild.png");
    Uri bilduri = Uri.fromFile(bildfile);

    int Kameracode = 15;

    Bitmap bm1;


    final File photo = new File(Environment.getExternalStorageDirectory()+ "/meineApp/", "Bildname.jpg");
    Uri imageUri = Uri.fromFile(photo);



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

         ImageView imFoto = (ImageView ) findViewById(R.id.imageView_bildAnzeige);
        if (bildfile.exists()){
            bm1 = BitmapFactory.decodeFile(bildfile.getAbsolutePath());
            imFoto.setImageBitmap(bm1);
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_setphoto:
                try{

                    bildintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    bildintent.putExtra(MediaStore.EXTRA_OUTPUT, bilduri);
                    startActivityForResult(bildintent, Kameracode);

                }catch(Exception e){

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Kamera nicht unterst�tzt!", Toast.LENGTH_SHORT).show();

                }
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



