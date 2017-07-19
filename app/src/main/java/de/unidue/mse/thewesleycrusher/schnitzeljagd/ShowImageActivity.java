package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ShowImageActivity extends AppCompatActivity {

    ImageView myImage;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        Intent intent = getIntent();
        String path = intent.getStringExtra(SpielActivity.EXTRA_MESSAGE);
        //String myDir = Environment.getExternalStorageDirectory().getAbsolutePath()
        textView = (TextView) findViewById(R.id.textViewShowPicure);
        File imgFile = new  File(path);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            myImage = (ImageView) findViewById(R.id.imageViewShowPictureHint);

            myImage.setImageBitmap(myBitmap);

        }
        else{

            textView.setText(path);

        }
    }
}
