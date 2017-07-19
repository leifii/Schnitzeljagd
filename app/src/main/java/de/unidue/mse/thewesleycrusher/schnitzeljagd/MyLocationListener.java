package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;

/**
 * Created by Tris on 14.07.2017.
 */

public class MyLocationListener implements LocationListener {

    private LocationManager locationManager;
    private MyLastLocation myLastLocation;
    private Handler handler;
    private Context context;
    private int updateLocationOnUi = 1;

    MyLocationListener(LocationManager locationManager, MyLastLocation lastlocation, Handler handler, Context context){
        this.locationManager=locationManager;

        this.handler=handler;
        this.context=context;
        this.myLastLocation=lastlocation;

    }

    @Override
    public void onLocationChanged(Location location) {

        if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null && myLastLocation.getLocation()!=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED) {

            myLastLocation.setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

            if(handler!=null){
                Message msg = handler.obtainMessage(0,updateLocationOnUi,0);
                handler.sendMessage(msg);

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
}
