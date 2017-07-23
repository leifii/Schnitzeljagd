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
    private Location checkPointLocation, targetLocation, mLastLocation;
    private Handler handler;
    private Context context;
    private MyLastLocation myLastLocation;
    private Boolean gameIsRunning=true, found=false;
    private int zielErreicht = 3;
    private int checkPointsToReach, checkPointsReached;


    ForceLocationUpdates(LocationManager locationManager, Location location, Location target, Location checkPoint, Handler handler, Context context, int i, int j){
        this.locationManager=locationManager;
        this.mLastLocation=location;
        this.targetLocation=target;
        this.handler=handler;
        this.context=context;
        this.checkPointLocation=checkPoint;
        this.checkPointsToReach =i;
        this.checkPointsReached =j;

    }



    @Override
    public void run() {

        while (!found) {


            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && !mLastLocation.equals(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))) {

                mLastLocation.set(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));


                if (handler != null) {                           ////forciert alle 5sek update des UI
                    Message msg = handler.obtainMessage(0, 4, 0);
                    handler.sendMessage(msg);
                }


                if (checkPointLocation.distanceTo(mLastLocation) < 25) {   ///überprüft, ob checkpoint erreicht wurde
                    Message msg = handler.obtainMessage(0, 2, 0);
                    handler.sendMessage(msg);

                }
                if (targetLocation.distanceTo(mLastLocation) < 25 && handler != null&&!found) { //überprüft, ob ziel erreicht wurde
                    Message msg = handler.obtainMessage(0, zielErreicht, 0);
                    handler.sendMessage(msg);
                    found = true;
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

