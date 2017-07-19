package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.location.Location;

/**
 * Created by Tris on 14.07.2017.
 */

public class MyLastLocation {

    private Location location;

    MyLastLocation(){
        location=null;
    }

    public void setLocation(Location location){
        this.location=location;
    }

    public Location getLocation(){
        return location;
    }
}
