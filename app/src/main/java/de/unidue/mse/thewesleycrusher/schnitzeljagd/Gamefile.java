package de.unidue.mse.thewesleycrusher.schnitzeljagd;

/**
 * Created by Tris on 18.07.2017.
 *
 * Class represents the real-world "Schnitzeljagd" in the app
 */

public class Gamefile {
    private double longitude1, longitude2, longitude3, longitude4, longitude5, latitude1, latitude2, latitude3, latitude4, latitude5;

    private String name, hint1, hint2, hint3, hint4, hint5;


    Gamefile(){

    }

    public void setName(String name){
        this.name=name;


        //////Setter for necessary information to create a "Schnitzeljagd"

    }
    public void setLongitude1(double longitude){
        this.longitude1=longitude;

    }
    public void setLongitude2(Double longitude){
        this.longitude2=longitude;
    }
    public void setLongitude3(Double longitude){
        this.longitude3=longitude;
    }
    public void setLongitude4(Double longitude){
        this.longitude4=longitude;
    }
    public void setLongitude5(Double longitude){
        this.longitude5=longitude;
    }
    public void setLatitude1(Double latitude){
        this.latitude1=latitude;
    }
    public void setLatitude2(Double latitude){
        this.latitude2=latitude;
    }
    public void setLatitude3(Double latitude){
        this.latitude3=latitude;
    }
    public void setLatitude4(Double latitude){
        this.latitude4=latitude;
    }
    public void setLatitude5(Double latitude){
        this.latitude5=latitude;
    }
    public void setHint1(String hint){
        this.hint1=hint;
    }
    public void setHint2(String hint){
        this.hint2=hint;
    }
    public void setHint3(String hint){
        this.hint3=hint;
    }
    public void setHint4(String hint){
        this.hint4=hint;
    }
    public void setHint5(String hint){
        this.hint5=hint;
    }

    ///getter
    public String getName(){
        return name;
    }
    public Double getLongitude1(){
        return longitude1;
    }
    public Double getLongitude2(){
        return longitude2;
    }
    public Double getLongitude3(){
        return longitude3;
    }
    public Double getLongitude4(){
        return longitude4;
    }
    public Double getLongitude5(){
        return longitude5;
    }
    public Double getLatitude1(){
        return latitude1;
    }
    public Double getLatitude2(){
        return latitude2;
    }
    public Double getLatitude3(){
        return latitude3;
    }
    public Double getLatitude4(){
        return latitude4;
    }
    public Double getLatitude5(){
        return latitude5;
    }
    public String getHint1(){
        return hint1;
    }
    public String getHint2(){
        return hint2;
    }
    public String getHint3(){
        return hint3;
    }
    public String getHint4(){
        return hint4;
    }public String getHint5(){
        return hint5;
    }
}
