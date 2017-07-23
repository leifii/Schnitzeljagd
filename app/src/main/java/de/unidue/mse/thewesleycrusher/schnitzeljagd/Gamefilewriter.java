package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Tris on 18.07.2017.
 *
 *
 * Class to create a new directory for a new "Schnitzeljagd", creating a new File and writing
 * information to this file.
 *
 *
 */

public class Gamefilewriter {
    private Gamefile gftw;
    private String toWrite;
    int x = 1;
    private final String routsDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Schnitzeljagd/Routen";
    private final String saveFileDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Schnitzeljagd/Savefiles";




    Gamefilewriter() {

    }

    public void writeGameFile(Gamefile gamefile){          //gamefile to  be saved

        this.gftw = gamefile;

        new Thread(new Runnable(){
            public void run(){


                ///Checks for the information to write to the File and saves it in a String

                if(gftw.getName()!=null){
                    toWrite="<name>\r\n" +gftw.getName()+"\r\n</name>";
                }
                if(gftw.getLongitude1()!=0){
                    toWrite+="\r\n<Longitude1>\r\n"+String.valueOf(gftw.getLongitude1())+"\r\n</Longitude1>";
                }
                if(gftw.getLongitude2()!=0){
                    toWrite+="\r\n<Longitude2>\r\n"+String.valueOf(gftw.getLongitude2())+"\r\n</Longitude2>";
                }
                if(gftw.getLongitude3()!=0){
                    toWrite+="\r\n<Longitude3>\r\n"+String.valueOf(gftw.getLongitude3())+"\r\n</Longitude3>";
                }
                if(gftw.getLongitude4()!=0){
                    toWrite+="\r\n<Longitude4>\r\n"+String.valueOf(gftw.getLongitude4())+"\r\n</Longitude4>";
                }
                if(gftw.getLongitude5()!=0){
                    toWrite+="\r\n<Longitude5>\r\n"+String.valueOf(gftw.getLongitude5())+"\r\n</Longitude5>";
                }
                if(gftw.getLatitude1()!=0){
                    toWrite+="\r\n<Latitude1>\r\n"+String.valueOf(gftw.getLatitude1())+"\r\n</Latitude1>";
                }
                if(gftw.getLatitude2()!=0){
                    toWrite+="\r\n<Latitude2>\r\n"+String.valueOf(gftw.getLatitude2())+"\r\n</Latitude2>";
                }
                if(gftw.getLatitude3()!=0){
                    toWrite+="\r\n<Latitude3>\r\n"+String.valueOf(gftw.getLatitude3())+"\r\n</Latitude3>";
                }
                if(gftw.getLatitude4()!=0){
                    toWrite+="\r\n<Latitude4>\r\n"+String.valueOf(gftw.getLatitude4())+"\r\n</Latitude4>";
                }
                if(gftw.getLatitude5()!=0){
                    toWrite+="\r\n<Latitude5>\r\n"+String.valueOf(gftw.getLatitude5())+"\r\n</Latitude5>";
                }
                if(gftw.getHint1()!=null){
                    toWrite+="\r\n<Hint1>\r\n"+gftw.getHint1()+"\r\n</Hint1>";
                }
                if(gftw.getHint2()!=null){
                    toWrite+="\r\n<Hint2>\r\n"+gftw.getHint2()+"\r\n</Hint2>";
                }
                if(gftw.getHint3()!=null){
                    toWrite+="\r\n<Hint3>\r\n"+gftw.getHint3()+"\r\n</Hint3>";
                }
                if(gftw.getHint4()!=null){
                    toWrite+="\r\n<Hint4>\r\n"+gftw.getHint4()+"\r\n</Hint4>";
                }
                if(gftw.getHint5()!=null){
                    toWrite+="\r\n<Hint5>\r\n"+gftw.getHint5()+"\r\n</Hint5>";
                }



                // file to store information
                File writeFile;


                //directory the file will be saved in, directory named after name of the route/game
                File directory = new File (routsDirectoryPath, gftw.getName());

                //checking whether the directory already exists, if not creating the directory
                if(!directory.exists()){
                    directory.mkdir();
                }
                //initializing file to store information to
                writeFile = new File(directory, gftw.getName()+".txt");
                try {
                    writeFile.createNewFile();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                OutputStream out = null;
                try {
                    out = new FileOutputStream(writeFile);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //wrapping OutputStreamWriter into Outputstream to write to the file
                OutputStreamWriter osw = new OutputStreamWriter(out);

                try {
                    //writing to the file
                    osw.write(toWrite);
                    osw.flush();
                    osw.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void writeTmpFile(Gamefile gamefile){

        this.gftw = gamefile;


        new Thread(new Runnable(){
            @Override
            public void run() {


                if(gftw.getName()!=null){
                    toWrite="<name>\r\n" +gftw.getName()+"\r\n</name>";
                }
                if(gftw.getLongitude1()!=0){
                    toWrite+="\r\n<Longitude1>\r\n"+String.valueOf(gftw.getLongitude1())+"\r\n</Longitude1>";
                }
                if(gftw.getLongitude2()!=0){
                    toWrite+="\r\n<Longitude2>\r\n"+String.valueOf(gftw.getLongitude2())+"\r\n</Longitude2>";
                }
                if(gftw.getLongitude3()!=0){
                    toWrite+="\r\n<Longitude3>\r\n"+String.valueOf(gftw.getLongitude3())+"\r\n</Longitude3>";
                }
                if(gftw.getLongitude4()!=0){
                    toWrite+="\r\n<Longitude4>\r\n"+String.valueOf(gftw.getLongitude4())+"\r\n</Longitude4>";
                }
                if(gftw.getLongitude5()!=0){
                    toWrite+="\r\n<Longitude5>\r\n"+String.valueOf(gftw.getLongitude5())+"\r\n</Longitude5>";
                }
                if(gftw.getLatitude1()!=0){
                    toWrite+="\r\n<Latitude1>\r\n"+String.valueOf(gftw.getLatitude1())+"\r\n</Latitude1>";
                }
                if(gftw.getLatitude2()!=0){
                    toWrite+="\r\n<Latitude2>\r\n"+String.valueOf(gftw.getLatitude2())+"\r\n</Latitude2>";
                }
                if(gftw.getLatitude3()!=0){
                    toWrite+="\r\n<Latitude3>\r\n"+String.valueOf(gftw.getLatitude3())+"\r\n</Latitude3>";
                }
                if(gftw.getLatitude4()!=0){
                    toWrite+="\r\n<Latitude4>\r\n"+String.valueOf(gftw.getLatitude4())+"\r\n</Latitude4>";
                }
                if(gftw.getLatitude5()!=0){
                    toWrite+="\r\n<Latitude5>\r\n"+String.valueOf(gftw.getLatitude5())+"\r\n</Latitude5>";
                }
                if(gftw.getHint1()!=null){
                    toWrite+="\r\n<Hint1>\r\n"+gftw.getHint1()+"\r\n</Hint1>";
                }
                if(gftw.getHint2()!=null){
                    toWrite+="\r\n<Hint2>\r\n"+gftw.getHint2()+"\r\n</Hint2>";
                }
                if(gftw.getHint3()!=null){
                    toWrite+="\r\n<Hint3>\r\n"+gftw.getHint3()+"\r\n</Hint3>";
                }
                if(gftw.getHint4()!=null){
                    toWrite+="\r\n<Hint4>\r\n"+gftw.getHint4()+"\r\n</Hint4>";
                }
                if(gftw.getHint5()!=null){
                    toWrite+="\r\n<Hint5>\r\n"+gftw.getHint5()+"\r\n</Hint5>";
                }



                if(toWrite!=null) {
                    //File writeFile = new File(myDirectory2, "tmpFileJagd");
                    File writeFile = new File(saveFileDirectoryPath, "tmpFileJagd");

                    if (!writeFile.exists()) {
                        try {
                            writeFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }

                    OutputStream out = null;

                    try {
                        out = new FileOutputStream(writeFile);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    OutputStreamWriter osw = new OutputStreamWriter(out);

                    try {
                        osw.write(toWrite.toString());
                        osw.flush();
                        osw.close();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

            }
        }).start();
    }

    public void clearTmpFile() {

        new Thread(new Runnable() {
            public void run() {

                File writeFile = new File(saveFileDirectoryPath, "tmpFileJagd");

                if(writeFile.exists()){
                    writeFile.delete();
                }
                try {
                    writeFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public  void saveReachedCheckpoints(Gamefile gamefile, final int checkpoint){
        this.gftw=gamefile;
        final String toWrite = String.valueOf(checkpoint);
        new Thread(new Runnable(){

            @Override
            public void run() {

                File writeFile = new File(routsDirectoryPath+"/"+gftw.getName(), gftw.getName()+"Save.txt");

                if(!writeFile.exists()){
                    try {
                        writeFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                OutputStream out = null;

                try {
                    out = new FileOutputStream(writeFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                OutputStreamWriter osw = new OutputStreamWriter(out);

                try {
                    osw.write(toWrite);
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void clearSaveFile(final Gamefile gamefile){
        new Thread(new Runnable() {
            public void run() {

                File writeFile = new File(routsDirectoryPath+"/"+gamefile.getName(), gamefile.getName()+"Save.txt");

                if(writeFile.exists()){
                    writeFile.delete();

                }


                try {
                    writeFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }



        }).start();
    }
}

