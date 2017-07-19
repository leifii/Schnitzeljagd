package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;

/**
 * Created by Tris on 14.07.2017.
 */

public class ForceLocationUpdates implements Runnable {
    private LocationManager locationManager;
    private Location checkPointLocation, targetLocation;
    private Handler handler;
    private Context context;
    private MyLastLocation myLastLocation;
    private Boolean gameIsRunning, found=false;
    private int zielErreicht = 3;


    ForceLocationUpdates(LocationManager locationManager, MyLastLocation  location, Location target, Location checkPoint, Handler handler, Context context, Boolean gameIsRunning){
        this.locationManager=locationManager;
        this.myLastLocation=location;
        this.targetLocation=target;
        this.handler=handler;
        this.context=context;
        this.gameIsRunning=gameIsRunning;
        this.checkPointLocation=checkPoint;

    }



    @Override
    public void run() {

        while(gameIsRunning) {

            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                    &&myLastLocation.getLocation()!=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
            {
                myLastLocation.setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

                if(handler!=null){
                    Message msg = handler.obtainMessage(0,1,0);
                    handler.sendMessage(msg);
                }


                if(checkPointLocation.distanceTo(myLastLocation.getLocation())<25){
                    Message msg = handler.obtainMessage(0, 2, 0);
                    handler.sendMessage(msg);
                }
                if(targetLocation.distanceTo(myLastLocation.getLocation())<25&&handler!=null){
                    Message msg = handler.obtainMessage(0, zielErreicht, 0);
                    handler.sendMessage(msg);
                    gameIsRunning=false;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }



    }
}
