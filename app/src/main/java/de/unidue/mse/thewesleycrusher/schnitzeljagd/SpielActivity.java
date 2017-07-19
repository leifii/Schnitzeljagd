package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;



public class SpielActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener {

    private MyLastLocation myLastLocation;
    private Location targetLocation, checkPointLocation;
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;
    private String currentGame, stringTargetLocation, hint;
    private float distance;
    private File myDirectory;
    private String myDirectoryPath, myPicturePath;
    private Boolean gameIsRunning=true;
    private TextView textCurrentGame, currentLocation, textTargetLocation, distanceText, textDebug, textCheckpointLocation;
    private Button button;
    private Thread tommy;

    private int i=1, j=1, number;

    private Gamefile gamefile;
    private Gamefileloader gamefileloader;

    public static final String EXTRA_MESSAGE = "com.example.tris.prototyp_schnitzeljagd.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiel);





        Intent intent = getIntent();
        String message = intent.getStringExtra(AuswahlRouteActivity.EXTRA_MESSAGE);   //////AuswahlRouteActivity

        textCurrentGame = (TextView)findViewById(R.id.textCurrentGame);
        currentLocation = (TextView)findViewById(R.id.textCurrentLocationSpiel);
        textTargetLocation = (TextView)findViewById(R.id.textTargetLocation);
        distanceText  = (TextView) findViewById(R.id.textDistanceToTargetLocation);
        textDebug = (TextView) findViewById(R.id.textDebug);
        textCheckpointLocation = (TextView) findViewById(R.id.textCheckpointLocation);
        findViewById(R.id.btnStopGame).setOnClickListener(this);
        findViewById(R.id.btnShowHint).setOnClickListener(this);
        findViewById(R.id.btnShowPicture).setOnClickListener(this);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        myLastLocation=new MyLastLocation();
        targetLocation=new Location("Schnitzeljagd");
        checkPointLocation=new Location("Schnitzeljagd");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        myLocationListener = new MyLocationListener(locationManager, myLastLocation, new Handler(this), this);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            noGpsAlert();
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, myLocationListener);
        }

        myDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/schnitzeljagd"+"/"+message+"/"+message;
        myPicturePath   = Environment.getExternalStorageDirectory().getAbsolutePath()+"/schnitzeljagd"+"/"+message;
        //myDirectory = new File(myDirectoryPath+message);

        gamefile=new Gamefile();
        gamefileloader=new Gamefileloader(myDirectoryPath, gamefile);
        gamefileloader.loadGamefile();

        //textCurrentGame.setText("Current game: "+gamefile.getName());
        textCurrentGame.setText(myDirectoryPath);
        textDebug.setText(gamefile.getName());

        setTargetLocation();
        incrementCheckpoint();

        //readFile(message);
        //textCurrentGame.setText(currentGame);
        //textCurrentGame.setText(myDirectoryPath);
        //textCurrentGame.setText(currentGame);
        textTargetLocation.setText("Targetlocation: " + "\nLongitude: "+String.valueOf(targetLocation.getLongitude())+"\nLatitude: " +String.valueOf(targetLocation.getLatitude()));

        //String test = String.valueOf(myLastLocation.getLocation().getLatitude());
        //distanceText.setText(test);
        tommy = new Thread(new ForceLocationUpdates(locationManager, myLastLocation, targetLocation, checkPointLocation, new Handler(this), this, gameIsRunning));
        tommy.start();
    }

    @Override
    public boolean handleMessage(Message message) {
        if(message.arg1==1){

            currentLocation.setText("Current Location"+"\nLongitude: " + String.valueOf(myLastLocation.getLocation().getLongitude() +
                    "\nLatitude: "+String.valueOf(myLastLocation.getLocation().getLatitude())));

            distanceText.setText("Distanz ungefähr: "+String.valueOf(getDistance())+" Meter");
        }
        if(message.arg1==2){

            incrementCheckpoint();
            distanceText.setText("Distanz ungefähr: "+String.valueOf(getDistance())+" Meter");
            checkPointReached();

        }

        if(message.arg1==3){

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            //.setSmallIcon(R.drawable.notification)
                            .setContentTitle("Notification")
                            .setContentText("Ziel erreicht!");

            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);

            stopGame();


        }
        return false;
    }


    private void noGpsAlert() {final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled. Do you want to enable it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void setTargetLocation(){

        if(gamefile.getLongitude1()!=0&&gamefile.getLatitude1()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude1());
            this.targetLocation.setLatitude(gamefile.getLatitude1());
            textDebug.setText("Hinterlegte Checkpoints = 1");
        }
        if(gamefile.getLongitude2()!=0&&gamefile.getLatitude2()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude2());
            this.targetLocation.setLatitude(gamefile.getLatitude2());
            textDebug.setText("Hinterlegte Checkpoints = 2");

        }
        if(gamefile.getLongitude3()!=0&&gamefile.getLatitude3()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude3());
            this.targetLocation.setLatitude(gamefile.getLatitude3());
            textDebug.setText("Hinterlegte Checkpoints = 3");

        }
        if(gamefile.getLongitude4()!=0&&gamefile.getLatitude4()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude4());
            this.targetLocation.setLatitude(gamefile.getLatitude4());
            textDebug.setText("Hinterlegte Checkpoints = 4");

        }
        if(gamefile.getLongitude5()!=0&&gamefile.getLatitude5()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude5());
            this.targetLocation.setLatitude(gamefile.getLatitude5());
            textDebug.setText("Hinterlegte Checkpoints = 5");

        }

    }
    public void incrementCheckpoint(){

        if(i==1&&gamefile.getLongitude1()!=0&&gamefile.getLatitude1()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude1());
            checkPointLocation.setLatitude(gamefile.getLatitude1());
            textCheckpointLocation.setText("Checkpoint 1: " + "\nLongitude: "+String.valueOf(checkPointLocation.getLongitude())+"\nLatitude: " +String.valueOf(targetLocation.getLatitude()));

        }
        if(i==2&&gamefile.getLongitude2()!=0&&gamefile.getLatitude2()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude2());
            checkPointLocation.setLatitude(gamefile.getLatitude2());
            textCheckpointLocation.setText("Checkpoint 2: " + "\nLongitude: "+String.valueOf(checkPointLocation.getLongitude())+"\nLatitude: " +String.valueOf(targetLocation.getLatitude()));

        }
        if(i==3&&gamefile.getLongitude3()!=0&&gamefile.getLatitude3()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude3());
            checkPointLocation.setLatitude(gamefile.getLatitude3());
            textCheckpointLocation.setText("Checkpoint 3: " + "\nLongitude: "+String.valueOf(checkPointLocation.getLongitude())+"\nLatitude: " +String.valueOf(targetLocation.getLatitude()));

        }
        if(i==4&&gamefile.getLongitude4()!=0&&gamefile.getLatitude4()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude4());
            checkPointLocation.setLatitude(gamefile.getLatitude4());
            textCheckpointLocation.setText("Checkpoint 4: " + "\nLongitude: "+String.valueOf(checkPointLocation.getLongitude())+"\nLatitude: " +String.valueOf(targetLocation.getLatitude()));

        }if(i==5&&gamefile.getLongitude5()!=0&&gamefile.getLatitude5()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude5());
            checkPointLocation.setLatitude(gamefile.getLatitude5());
            textCheckpointLocation.setText("Checkpoint 5: " + "\nLongitude: "+String.valueOf(checkPointLocation.getLongitude())+"\nLatitude: " +String.valueOf(targetLocation.getLatitude()));

        }
        i++;


    }

    public float getDistance(){
        distance=checkPointLocation.distanceTo(myLastLocation.getLocation());
        return distance;
    }

    public void checkPointReached(){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.notification)
                        .setContentTitle("Notification")
                        .setContentText("Checkpoint "+j+" erreicht!");

        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        j++;

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.btnStopGame:

                stopGame();

                break;

            case R.id.btnShowHint:
                Intent intent = new Intent(this, ShowHintActivity.class);

                if(j==1) {
                    hint = gamefile.getHint1();
                }
                if(j==2) {
                    hint = gamefile.getHint2();
                }
                if(j==3) {
                    hint = gamefile.getHint3();
                }
                if(j==4) {
                    hint = gamefile.getHint4();
                }
                if(j==5) {
                    hint = gamefile.getHint5();
                }
                intent.putExtra(EXTRA_MESSAGE, hint);
                startActivity(intent);
                break;

            case R.id.btnShowPicture:
                Intent intent2 = new Intent(this, ShowImageActivity.class);


                if(j==1){
                    number=1;
                }
                if(j==2){
                    number=2;
                }
                if(j==3){
                    number=3;
                }
                if(j==4){
                    number=4;
                }
                if(j==5){
                    number=5;
                }
                hint=myPicturePath+"/"+gamefile.getName()+number+".jpg";
                intent2.putExtra(EXTRA_MESSAGE, hint);
                startActivity(intent2);
        }
    }

    public void stopGame(){

        gameIsRunning = false;
        tommy.interrupt();
        locationManager.removeUpdates(myLocationListener);
    }
}
