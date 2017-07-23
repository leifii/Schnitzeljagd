package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SpielActivity extends AppCompatActivity implements View.OnClickListener {


    private Location targetLocation, checkPointLocation, mLastLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private String hint;

    private String message;
    private Boolean gameIsRunning, firstTime=true, targetReached=false;
    private TextView textCurrentGame, textCheckpointsToReach, textCheckpointLocation;


    private int currentCheckpoint =1, checkpointsToReach, loadedCheckpoint, reachedCheckpoints=0;

    private Gamefile gamefile;
    private Gamefileloader gamefileloader;
    private Gamefilewriter gamefilewriter;
    private Intent mainIntent;

    public static final String EXTRA_MESSAGE = "com.example.tris.prototyp_schnitzeljagd.MESSAGE";

    private final String routsDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Schnitzeljagd/Routen";
    static final String STATE_ROUTENAME = "routeName";
    static final String STATE_CHECKPOINT = "currentCheckpoint";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainIntent = getIntent();

        if(savedInstanceState!=null){


            reachedCheckpoints = savedInstanceState.getInt(STATE_CHECKPOINT);

            message = savedInstanceState.getString(STATE_ROUTENAME);
            gamefile=new Gamefile();




        }else{

            message=mainIntent.getStringExtra(AuswahlRouteActivity.EXTRA_MESSAGE);
            Gamefileloader gamefileloader = new Gamefileloader();
            loadedCheckpoint = gamefileloader.loadSaveFile(routsDirectoryPath+"/"+message+"/"+message+"Save.txt");
            gamefile=new Gamefile();


            if(loadedCheckpoint>0&&loadedCheckpoint<6){

               reachedCheckpoints=loadedCheckpoint;

            }
            else{
                reachedCheckpoints=0;
            }

        }

        textCurrentGame = (TextView)findViewById(R.id.textCurrentGame);
        textCheckpointsToReach = (TextView) findViewById(R.id.textDebug);
        textCheckpointLocation = (TextView) findViewById(R.id.textCheckpointLocation);
        ImageView imageViewSpiel = (ImageView)  findViewById(R.id.imageViewSpielActivity);


        findViewById(R.id.btnStopGame).setOnClickListener(this);
        findViewById(R.id.btnShowHint).setOnClickListener(this);
        findViewById(R.id.btnShowPicture).setOnClickListener(this);
        findViewById(R.id.btnMap).setOnClickListener(this);
        findViewById(R.id.btnDeleteSavegame).setOnClickListener(this);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLastLocation=new Location("schnitzeljagd");

        if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null) {
            mLastLocation.set(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
        targetLocation=new Location("Schnitzeljagd");
        checkPointLocation=new Location("Schnitzeljagd");

        currentCheckpoint=reachedCheckpoints+1;



        gamefileloader=new Gamefileloader();
        gamefileloader.loadGamefile(routsDirectoryPath+"/"+message+"/"+message+".txt", gamefile);
        gamefilewriter = new Gamefilewriter();
        setTargetLocation();
        incrementCheckpoint();

        textCurrentGame.setText("Derzeitige Route: "+"\n"+gamefile.getName());


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLastLocation.set(location);


                if (checkPointLocation.distanceTo(mLastLocation) < 25 && !targetReached && currentCheckpoint<=checkpointsToReach){
                    if (firstTime) {
                        firstTime = false;
                        if(currentCheckpoint<checkpointsToReach){
                            currentCheckpoint++;
                        }
                        checkPointReached();

                        incrementCheckpoint();
                    }
                    if (reachedCheckpoints==checkpointsToReach) {
                        if (gameIsRunning) {
                            gameIsRunning = false;
                            targetReached = true;
                            checkPointReached();


                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(SpielActivity.this)
                                            .setSmallIcon(R.drawable.notification3)
                                            .setContentTitle("Notification")
                                            .setContentText("Ziel erreicht!");


                            int mNotificationId = 001;
                            NotificationManager mNotifyMgr =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());

                            Vibrator v = (Vibrator) SpielActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(1000);
                            destinationReached();
                        }
                    }
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            noGpsAlert();
        }
        if(ContextCompat.checkSelfPermission(SpielActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
        }

        gameIsRunning=true;

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
            checkpointsToReach =1;
        }
        if(gamefile.getLongitude2()!=0&&gamefile.getLatitude2()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude2());
            this.targetLocation.setLatitude(gamefile.getLatitude2());
            checkpointsToReach =2;

        }
        if(gamefile.getLongitude3()!=0&&gamefile.getLatitude3()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude3());
            this.targetLocation.setLatitude(gamefile.getLatitude3());
            checkpointsToReach =3;

        }
        if(gamefile.getLongitude4()!=0&&gamefile.getLatitude4()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude4());
            this.targetLocation.setLatitude(gamefile.getLatitude4());
            checkpointsToReach =4;

        }
        if(gamefile.getLongitude5()!=0&&gamefile.getLatitude5()!=0) {
            this.targetLocation.setLongitude(gamefile.getLongitude5());
            this.targetLocation.setLatitude(gamefile.getLatitude5());
            checkpointsToReach =5;

        }
        textCheckpointsToReach.setText("Zu erreichende Checkpoints: "+checkpointsToReach);


    }
    public void incrementCheckpoint(){


        if(currentCheckpoint ==1&&gamefile.getLongitude1()!=0&&gamefile.getLatitude1()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude1());
            checkPointLocation.setLatitude(gamefile.getLatitude1());
            textCheckpointLocation.setText("Checkpoint: "+currentCheckpoint + "\nLongitude: "+String.valueOf(checkPointLocation.getLongitude())+"\nLatitude: " +String.valueOf(checkPointLocation.getLatitude()));
        }
        if(currentCheckpoint ==2&&gamefile.getLongitude2()!=0&&gamefile.getLatitude2()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude2());
            checkPointLocation.setLatitude(gamefile.getLatitude2());
        }
        if(currentCheckpoint ==3&&gamefile.getLongitude3()!=0&&gamefile.getLatitude3()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude3());
            checkPointLocation.setLatitude(gamefile.getLatitude3());
        }
        if(currentCheckpoint==4&&gamefile.getLongitude4()!=0&&gamefile.getLatitude4()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude4());
            checkPointLocation.setLatitude(gamefile.getLatitude4());
        }
        if(currentCheckpoint ==5&&gamefile.getLongitude5()!=0&&gamefile.getLatitude5()!=0){
            checkPointLocation.setLongitude(gamefile.getLongitude5());
            checkPointLocation.setLatitude(gamefile.getLatitude5());
        }
        textCheckpointLocation.setText("Du suchst derzeit Checkpoint: "+currentCheckpoint );
        firstTime=true;
    }

    public void checkPointReached(){

        if(reachedCheckpoints<checkpointsToReach){
            reachedCheckpoints++;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification3)
                        .setContentTitle("Schnitzeljagd")
                        .setContentText("Checkpoint "+ reachedCheckpoints+" erreicht!");

        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

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

                if(currentCheckpoint ==1) {
                    hint = gamefile.getHint1();
                }
                if(currentCheckpoint ==2) {
                    hint = gamefile.getHint2();
                }
                if(currentCheckpoint ==3) {
                    hint = gamefile.getHint3();
                }
                if(currentCheckpoint ==4) {
                    hint = gamefile.getHint4();
                }
                if(currentCheckpoint ==5) {
                    hint = gamefile.getHint5();
                }
                intent.putExtra(EXTRA_MESSAGE, hint);
                intent.putExtra("Class", "SpielActivity");
                startActivity(intent);
                break;

            case R.id.btnShowPicture:

                Intent intent2 = new Intent(this, ShowImageActivity.class);



                hint= routsDirectoryPath+"/"+gamefile.getName()+"/"+gamefile.getName()+currentCheckpoint+".jpg";
                intent2.putExtra(EXTRA_MESSAGE, hint);

                startActivity(intent2);
                break;


            case R.id.btnMap:
                Intent intent3 = new Intent(this, MapsActivity.class);
                startActivity(intent3);
                break;

            case R.id.btnDeleteSavegame:
                resetSaveFile();
                break;

        }
    }

    public void stopGame(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Möchtest du das Spiel beenden?").setCancelable(false)
                .setPositiveButton("JA!", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        locationManager.removeUpdates(locationListener);

                        Intent intent = new Intent(SpielActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("NEIN!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void resetSaveFile(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Möchtest du deinen bisherigen Fortschritt auf dieser Route zurücksetzen?").setCancelable(false)
                .setPositiveButton("JA!", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        gamefilewriter.clearSaveFile(gamefile);
                        currentCheckpoint=1;
                        reachedCheckpoints=0;
                        incrementCheckpoint();

                       // Intent intent = new Intent(SpielActivity.this, AuswahlRouteActivity.class);
                        //startActivity(intent);
                        Toast.makeText(SpielActivity.this, "Fortschritt gelöscht!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NEIN!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void destinationReached(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Du hast den letzten Checkpoint erreicht! Möchtest du eine neue Route auswählen?").setCancelable(false)
                .setPositiveButton("JA!", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        currentCheckpoint=1;
                        reachedCheckpoints=0;

                        Intent intent = new Intent(SpielActivity.this, AuswahlRouteActivity.class);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("NEIN!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();


        if(reachedCheckpoints>0&&reachedCheckpoints<checkpointsToReach&&!targetReached) {
            gamefilewriter.saveReachedCheckpoints(gamefile, reachedCheckpoints);
        }



    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt(STATE_CHECKPOINT, reachedCheckpoints);
        savedInstanceState.putString(STATE_ROUTENAME, gamefile.getName());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
