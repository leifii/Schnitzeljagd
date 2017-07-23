package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Tris on 18.07.2017.
 *
 * Class to load a Gamefileobject from a (.txt)file stored on the users device's external storage.
 *
 * Keep in mind that corrupted savefiles created by other means than this app may lead to crashes.
 *
 *
 */

public class Gamefileloader {

    private File toLoad;
    private Gamefile gameFile;
    private String hint;
    private boolean inHint = true;

    Gamefileloader(){

    }

    public void loadGamefile(String path, Gamefile gamefile){

        this.toLoad=new File(path);
        this.gameFile=gamefile;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(toLoad));
            String line = reader.readLine();

            while(line!=null){

                if(line.equals("<name>")){
                    line=reader.readLine();
                    gameFile.setName(line);
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Longitude1>")){
                    line=reader.readLine();
                    gameFile.setLongitude1(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Longitude2>")){
                    line=reader.readLine();
                    gameFile.setLongitude2(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line.equals("<Longitude3>")){
                    line=reader.readLine();
                    gameFile.setLongitude3(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Longitude4>")){
                    line=reader.readLine();
                    gameFile.setLongitude4(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Longitude5>")){
                    line=reader.readLine();
                    gameFile.setLongitude5(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Latitude1>")){
                    line=reader.readLine();
                    gameFile.setLatitude1(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Latitude2>")){
                    line=reader.readLine();
                    gameFile.setLatitude2(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Latitude3>")){
                    line=reader.readLine();
                    gameFile.setLatitude3(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Latitude4>")){
                    line=reader.readLine();
                    gameFile.setLatitude4(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }
                if(line.equals("<Latitude5>")){
                    line=reader.readLine();
                    gameFile.setLatitude5(Double.parseDouble(line));
                    line=reader.readLine();
                    line=reader.readLine();
                }
                if(line==null){
                    break;
                }


                if(line.equals("<Hint1>")){

                    while(inHint){

                        if(line.equals("<Hint1>")){
                            line=reader.readLine();
                        }

                        if(!line.equals("<Hint1>")&&!line.equals("</Hint1>")){
                            if(hint==null){
                                hint=line;
                            }
                            else{
                                hint+="\n"+line;
                            }
                            line=reader.readLine();
                        }

                        if(line.equals("</Hint1>")){
                            gameFile.setHint1(hint);
                            hint=null;
                            inHint=false;
                            line=reader.readLine();
                        }
                    }
                }

                if(line==null){
                    break;
                }


                if(line.equals("<Hint2>")){
                    inHint=true;
                    while(inHint){
                        if(line.equals("<Hint2>")){
                            line=reader.readLine();
                        }
                        if(!line.equals("<Hint2>")&&!line.equals("</Hint2>")){
                            if(hint==null){
                                hint=line;
                            }
                            else{
                                hint+="\n"+line;
                            }
                            line=reader.readLine();
                        }
                        if(line.equals("</Hint2>")){
                            gameFile.setHint2(hint);
                            hint=null;
                            inHint=false;
                            line=reader.readLine();
                        }
                    }
                }
                if(line==null){
                    break;
                }


                if(line.equals("<Hint3>")){
                    inHint=true;
                    while(inHint){
                        if(line.equals("<Hint3>")){
                            line=reader.readLine();
                        }
                        if(!line.equals("<Hint3>")&&!line.equals("</Hint3>")){
                            if(hint==null){
                                hint=line;
                            }
                            else{
                                hint+="\n"+line;
                            }
                            line=reader.readLine();
                        }
                        if(line.equals("</Hint3>")){
                            gameFile.setHint3(hint);
                            hint=null;
                            inHint=false;
                            line=reader.readLine();
                        }
                    }
                }
                if(line==null){
                    break;
                }


                if(line.equals("<Hint4>")){
                    inHint=true;
                    while(inHint){
                        if(line.equals("<Hint4>")){
                            line=reader.readLine();
                        }
                        if(!line.equals("<Hint4>")&&!line.equals("</Hint4>")){
                            if(hint==null){
                                hint=line;
                            }
                            else{
                                hint+="\n"+line;
                            }
                            line=reader.readLine();
                        }
                        if(line.equals("</Hint4>")){
                            gameFile.setHint4(hint);
                            hint=null;
                            inHint=false;
                            line=reader.readLine();
                        }
                    }
                }
                if(line==null){
                    break;

                }
                if(line.equals("<Hint5>")){
                    inHint=true;
                    while(inHint){
                        if(line.equals("<Hint5>")){
                            line=reader.readLine();
                        }
                        if(!line.equals("<Hint5>")&&!line.equals("</Hint5>")){
                            if(hint==null){
                                hint=line;
                            }
                            else{
                                hint+="\n"+line;
                            }
                            line=reader.readLine();
                        }
                        if(line.equals("</Hint5>")){
                            gameFile.setHint5(hint);
                            hint=null;
                            inHint=false;
                            line=reader.readLine();
                        }
                    }
                }
                if(line==null){
                    break;
                }


            }
            reader.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int loadSaveFile(String path){
        int i = 0;
        this.toLoad = new File(path);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(toLoad));
            String line = reader.readLine();

            if(line!=null){
                i= Integer.valueOf(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return i;

    }

}
