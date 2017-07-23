package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;

public class AuswahlRouteActivity extends AppCompatActivity {


    String myDir;
    private ListView listView;
    private TextView textView;



    public static final String EXTRA_MESSAGE = "de.unidue.mse.thewesleycrusher.schnitzeljagd.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswahl_route);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = (TextView) findViewById(R.id.textSpielLaden);

        myDir= Environment.getExternalStorageDirectory().getAbsolutePath()+"/schnitzeljagd/routen";

        File file;


        //checking wether a directory "schnitzeljagd" exists
        File checkForDir = new File (myDir);
        if(!checkForDir.exists()){
           file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            textView.setText("Ein Ordner namens Schnitzeljagd existiert nicht");
        }
        else{ file=checkForDir;}


        String list[] = null;

        if(file.isDirectory()){
            list = file.list();
            textView.setText(myDir);
        }
        if(list==null){
            Toast.makeText(this, "list is null", Toast.LENGTH_SHORT);
        }
        if(list!=null){

            MyFiles myFilesArray[] = new MyFiles[list.length];
            for(int i = 0; i<list.length;i++){

                    myFilesArray[i] = new MyFiles(list[i]);

            }


            listView = (ListView) findViewById(R.id.listView1);

            ArrayAdapter<MyFiles> adapter = new ArrayAdapter<MyFiles>(this, android.R.layout.simple_list_item_1, myFilesArray);
            listView.setAdapter(adapter);




            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    String file = ((TextView)view).getText().toString();

                    textView.setText(file);

                    Intent intent = new Intent(AuswahlRouteActivity.this, SpielActivity.class);   ///\\\\
                    intent.putExtra(EXTRA_MESSAGE,file);
                    startActivity(intent);


                }
            });


        }


    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }




}
