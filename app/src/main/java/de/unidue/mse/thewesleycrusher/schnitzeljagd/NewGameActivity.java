package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Size;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewGameActivity extends Activity implements View.OnClickListener {

    private LocationManager locMan;
    private LocationListener locLis;

    private Gamefile gamefile;
    private Gamefilewriter gameFileWriter;

    private Location myLastLocation;

    public final int REQUEST_ID = 200;
    private TextureView tv1;

    private String toWrite, hinweis, name, checkpoint = "Erstelle Checkpoint ";
    private Integer step ;

    private Boolean firstGpsSignal = true, photoTaken=false, routeFinished=false;
    private TextView textCurrentCheckpoint;
    private EditText editTextName, editTextHinweis;

    private Button ende, naechster, foto;

    public static final String EXTRA_MESSAGE = "de.unidue.mse.thewesleycrusher.schnitzeljagd.MESSAGE";





    /////////////////////////////////////////////

    private TextureView.SurfaceTextureListener tv1Listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCam();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    String camID;
    Size imageSize;

    private CameraDevice camera;
    private CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            camera = cameraDevice;
            createPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            camera.close();
        }
    };

    public CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captBuilder;
    private Handler handler;
    private HandlerThread hThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);



        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if(!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            noGpsAlert();
        }

        locLis=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                myLastLocation.set(location);

                if(firstGpsSignal){
                    Toast toast = Toast.makeText(NewGameActivity.this, "Du kannst nun beginnen!", Toast.LENGTH_SHORT);
                    toast.show();
                    enableButtons();
                    firstGpsSignal=false;
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
                disableButtons();

            }
        };


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locLis);
        }







        gamefile = new Gamefile();
        gameFileWriter = new Gamefilewriter( );

       // getActionBar().setDisplayHomeAsUpEnabled(true);



        hThread = new HandlerThread("Background Handler");
        hThread.start();
        handler = new Handler(hThread.getLooper());


        foto = (Button) findViewById(R.id.button_setphoto);
        foto.setOnClickListener(this);
        foto.setEnabled(false);

        Button gps = (Button) findViewById(R.id.button_setgps);
        gps.setOnClickListener(this);

        naechster = (Button) findViewById(R.id.button_nextstop);
        naechster.setOnClickListener(this);
        naechster.setEnabled(false);


        /*
        Button hinweis =(Button) findViewById(R.id.button_setText);
        hinweis.setOnClickListener(this);
        */

        ende = (Button) findViewById(R.id.button_abschließen);
        ende.setOnClickListener(this);
        ende.setEnabled(false);

        tv1 = (TextureView) findViewById(R.id.tv1);
        tv1.setSurfaceTextureListener(tv1Listener);

        textCurrentCheckpoint = (TextView) findViewById(R.id.textViewCurrentCheckpoint);
        textCurrentCheckpoint.setText(String.valueOf("Checkpoint: "+"\n" +step));

        editTextName = (EditText) findViewById(R.id.editText_Name);
        editTextHinweis = (EditText) findViewById(R.id.editText_Hinweis);


        myLastLocation = new Location("schnitzeljagd");


        if(locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null) {
            myLastLocation.set(locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
        //checkPem();

        Toast.makeText(this, "Bitte warte, bis dein Aufenhaltsort erfasst wurde.", Toast.LENGTH_SHORT).show();










    }



    private void createPreview() {
        try {
            SurfaceTexture tex = tv1.getSurfaceTexture();
            tex.setDefaultBufferSize(imageSize.getHeight(), imageSize.getWidth());
            Surface surface = new Surface(tex);
            captBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captBuilder.addTarget(surface);
            camera.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession camCaptureSession) {
                    if (camera == null) {
                        return;
                    }
                    cameraCaptureSession = camCaptureSession;
                    captBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
                        cameraCaptureSession.setRepeatingRequest(captBuilder.build(), null, handler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCam() {
        CameraManager camMan = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            camID = camMan.getCameraIdList()[0];
            CameraCharacteristics camChar = camMan.getCameraCharacteristics(camID);
            StreamConfigurationMap streamConfigMap = camChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            imageSize = streamConfigMap.getOutputSizes(SurfaceTexture.class)[0];


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                camMan.openCamera(camID, stateCallBack, null);

                /*
                ActivityCompat.requestPermissions(NewGameActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
                ////////
                */
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }






    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_setphoto:
                if(checkName()) {
                    if(checkHinweis()){
                     if (step == 1) {
                        name = editTextName.getEditableText().toString();
                        gamefile.setName(name);
                    }

                        takePicture();
                        photoTaken = true;
                        foto.setEnabled(false);
                        hinweis = editTextHinweis.getEditableText().toString();
                        speicherHinweis(step, hinweis);
                        speicherGPS(step);

                    //gamefile.setName(name);
                }
                else{
                        Toast.makeText(NewGameActivity.this, "Verfasse einen Hinweis", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_setgps:
                {
                    Intent intent = new Intent(this, StartActivity.class);
                    startActivity(intent);
                    finishAndRemoveTask();
                    locMan.removeUpdates(locLis);

                }

                break;
            case R.id.button_nextstop:
                if(checkName()) {
                    if (photoTaken) {
                        if (checkHinweis()) {
                            if (step == 1) {
                                name = editTextName.getEditableText().toString();
                                gamefile.setName(name);
                                Toast toast = Toast.makeText(this, name, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            hinweis = editTextHinweis.getEditableText().toString();
                            speicherHinweis(step, hinweis);
                            speicherGPS(step);
                            photoTaken = false;
                            foto.setEnabled(true);

                            if (step <= 5) {
                                if (step < 6) {
                                    step++;
                                }
                                if (step == 5) {
                                    disableButtons();
                                }
                                textCurrentCheckpoint.setText(String.valueOf("Checkpoint: " + "\n" + step));
                                editTextHinweis.setText("");
                            } else {
                                Toast toast = Toast.makeText(this, "Es wurden bereits 5 Checkpoints erstellt", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        } else {
                            Toast.makeText(NewGameActivity.this, "Verfasse einen Hinweis", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Mache ein Foto", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_abschließen:
                if(checkName()) {
                    if(photoTaken){
                    if (checkHinweis()) {

                        if (step == 1) {
                            name = editTextName.getEditableText().toString();
                            gamefile.setName(name);
                        }

                        hinweis = editTextHinweis.getEditableText().toString();
                        speicherHinweis(step, hinweis);
                        speicherGPS(step);


                        gameFileWriter.writeGameFile(gamefile);
                        gamefile = new Gamefile();
                        gameFileWriter.clearTmpFile();

                        locMan.removeUpdates(locLis);
                        finish();


                        Intent intent = new Intent(this, StartActivity.class);
                        startActivity(intent);
                        Toast.makeText(NewGameActivity.this, "Route erstellt", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewGameActivity.this, "Verfasse einen Hinweis", Toast.LENGTH_SHORT).show();
                    }
                } else {
                        Toast.makeText(this, "Mache ein Foto", Toast.LENGTH_SHORT).show();                    }
                }
                else {
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen", Toast.LENGTH_SHORT).show();
                }
                break;


            /*
            case R.id.button_setText:
                Toast toast = Toast.makeText(this, "testestest", Toast.LENGTH_SHORT);
                toast.show();


                if(checkName()) {
                    if (checkHinweis()) {
                        //gamefile.setName(name);
                        speicherHinweis(step, hinweis);
                    } else {
                        Toast.makeText(NewGameActivity.this, "Verfasse einen Hinweis", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen", Toast.LENGTH_SHORT).show();
                }


                break;
                */
        }

    }






    private void takePicture() {
        if (camera == null) {
            return;
        }
        CameraManager camMan = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            Size[] sizes;
            CameraCharacteristics cameraCharacteristics = camMan.getCameraCharacteristics(camera.getId());
            sizes = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            int width;
            int height;
            if (sizes.length > 0) {
                width = sizes[0].getWidth();
                height = sizes[0].getHeight();
            } else {
                Toast.makeText(NewGameActivity.this, "Sizes konnte nicht gelesen werden.", Toast.LENGTH_SHORT).show();
                return;
            }
            ImageReader imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            Surface input = new Surface(tv1.getSurfaceTexture());
            List<Surface> list = new ArrayList<Surface>();
            list.add(new Surface(tv1.getSurfaceTexture()));
            list.add(imageReader.getSurface());
            Surface output = imageReader.getSurface();
            final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            builder.addTarget(output);
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int actualrotation = 0;
            if (rotation == Surface.ROTATION_0) {
                actualrotation = 90;
            }

            builder.set(CaptureRequest.JPEG_ORIENTATION, actualrotation);


            //
            // Ab hier wird das Bild gespeichert
            //
            //


            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Schnitzeljagd/Routen", name);
            if(!directory.exists()){
                directory.mkdir();
            }

            final File file = new File(directory, name + step +".jpg");
            ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
                Image image = null;

                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    image = imageReader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] byteArray = new byte[buffer.capacity()];
                    buffer.get(byteArray);
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(byteArray);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (output != null)
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }
            };
            imageReader.setOnImageAvailableListener(onImageAvailableListener, handler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
                    super.onCaptureCompleted(session, captureRequest, totalCaptureResult);
                    createPreview();
                    Toast.makeText(NewGameActivity.this, "Erfolgreich gespeichert " + file, Toast.LENGTH_SHORT).show();
                }
            };
            camera.createCaptureSession(list, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(builder.build(), captureListener, handler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 10:
                checkPem();
                break;
            default:
                break;
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
        locMan.removeUpdates(locLis);
        finish();
    }

    public void speicherGPS (int i){

        Double j = myLastLocation.getLatitude();
        Double k = myLastLocation.getLongitude();
    switch (i){
        case 1:
            if( j != 0 && k !=0 ){
                gamefile.setLatitude1(j);
                gamefile.setLongitude1(k);
            }
            break;
        case 2:
            if( j != 0 && k !=0 ){
                gamefile.setLatitude2(j);
                gamefile.setLongitude2(k);
            }
            break;
        case 3:
            if( j != 0 && k !=0 ){
                gamefile.setLatitude3(j);
                gamefile.setLongitude3(k);
            }
            break;
        case 4:
            if( j != 0 && k !=0 ){
                gamefile.setLatitude4(j);
                gamefile.setLongitude4(k);
            }
            break;

        case 5:
            if( j != 0 && k !=0 ){
                gamefile.setLatitude5(j);
                gamefile.setLongitude5(k);
            }
            break;

    }
    }
    public void speicherHinweis(int i, String h){
        switch (i){
            case 1:
                gamefile.setHint1(h);
                break;
            case 2:
                gamefile.setHint2(h);
                break;
            case 3:
                gamefile.setHint3(h);
                break;
            case 4:
                gamefile.setHint4(h);
                break;
            case 5:
                gamefile.setHint5(h);
                break;

        }
    }


    public boolean checkName (){

        EditText edi1 = (EditText) findViewById(R.id.editText_Name);
        name = edi1.getEditableText().toString();

        return !name.equalsIgnoreCase("Name des Spiels eingeben");
    }

    public boolean checkHinweis(){
        EditText edi2 = (EditText) findViewById(R.id.editText_Hinweis);
        hinweis = edi2.getEditableText().toString();
        return !(hinweis.equals("Hinweis eingeben") && step < 5 || hinweis.isEmpty() && step < 5);
    }

    public void checkPem(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        //locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locLis);
    }





    public void showStringToWrite(){


       new Thread(new Runnable(){
            public void run(){


                ///Checks for the information to write to the File and saves it in a String

                if(gamefile.getName()!=null){
                    toWrite="<name>\r\n" + gamefile.getName()+"\r\n</name>";
                }
                if(gamefile.getLongitude1()!=0){
                    toWrite+="\r\n<Longitude1>\r\n"+String.valueOf(gamefile.getLongitude1())+"\r\n</Longitude1>";
                }
                if(gamefile.getLongitude2()!=0){
                    toWrite+="\r\n<Longitude2>\r\n"+String.valueOf(gamefile.getLongitude2())+"\r\n</Longitude2>";
                }
                if(gamefile.getLongitude3()!=0){
                    toWrite+="\r\n<Longitude3>\r\n"+String.valueOf(gamefile.getLongitude3())+"\r\n</Longitude3>";
                }
                if(gamefile.getLongitude4()!=0){
                    toWrite+="\r\n<Longitude4>\r\n"+String.valueOf(gamefile.getLongitude4())+"\r\n</Longitude4>";
                }
                if(gamefile.getLongitude5()!=0){
                    toWrite+="\r\n<Longitude5>\r\n"+String.valueOf(gamefile.getLongitude5())+"\r\n</Longitude5>";
                }
                if(gamefile.getLatitude1()!=0){
                    toWrite+="\r\n<Latitude1>\r\n"+String.valueOf(gamefile.getLatitude1())+"\r\n</Latitude1>";
                }
                if(gamefile.getLatitude2()!=0){
                    toWrite+="\r\n<Latitude2>\r\n"+String.valueOf(gamefile.getLatitude2())+"\r\n</Latitude2>";
                }
                if(gamefile.getLatitude3()!=0){
                    toWrite+="\r\n<Latitude3>\r\n"+String.valueOf(gamefile.getLatitude3())+"\r\n</Latitude3>";
                }
                if(gamefile.getLatitude4()!=0){
                    toWrite+="\r\n<Latitude4>\r\n"+String.valueOf(gamefile.getLatitude4())+"\r\n</Latitude4>";
                }
                if(gamefile.getLatitude5()!=0){
                    toWrite+="\r\n<Latitude5>\r\n"+String.valueOf(gamefile.getLatitude5())+"\r\n</Latitude5>";
                }
                if(gamefile.getHint1()!=null){
                    toWrite+="\r\n<Hint1>\r\n"+ gamefile.getHint1()+"\r\n</Hint1>";
                }
                if(gamefile.getHint2()!=null){
                    toWrite+="\r\n<Hint2>\r\n"+ gamefile.getHint2()+"\r\n</Hint2>";
                }
                if(gamefile.getHint3()!=null){
                    toWrite+="\r\n<Hint3>\r\n"+ gamefile.getHint3()+"\r\n</Hint3>";
                }
                if(gamefile.getHint4()!=null){
                    toWrite+="\r\n<Hint4>\r\n"+ gamefile.getHint4()+"\r\n</Hint4>";
                }
                if(gamefile.getHint5()!=null){
                    toWrite+="\r\n<Hint5>\r\n"+ gamefile.getHint5()+"\r\n</Hint5>";
                }

                Intent intent = new Intent(NewGameActivity.this, ShowHintActivity.class);
                String arsch = String.valueOf(myLastLocation.getLatitude());
                intent.putExtra("Class", "NewGameActivity");
                intent.putExtra(EXTRA_MESSAGE, toWrite);

                //intent.putExtra(EXTRA_MESSAGE, arsch);
                startActivity(intent);
                gameFileWriter.writeGameFile(gamefile);

            }
        }).start();


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



    private Boolean runsOnEmulator(){
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tm.getNetworkOperatorName();
        return "Android".equals(networkOperator);

    }


    @Override
    protected void onPause() {
        super.onPause();

        if(gamefile.getName()!=null){
            if(!routeFinished){
                saveTempFile();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadTempFile();

        if(gamefile.getName()!=null) {
            resumeRoute();
        }
    }



    public void enableButtons(){
        naechster.setEnabled(true);
        ende.setEnabled(true);
        foto.setEnabled(true);
    }
    public void disableButtons(){
        naechster.setEnabled(false);
        //ende.setEnabled(false);
        //foto.setEnabled(false);
    }

    public void saveTempFile(){
        gameFileWriter.writeTmpFile(gamefile);
    }
    private void loadTempFile(){

        Gamefileloader gamefileloader = new Gamefileloader();
        gamefile = new Gamefile();
        gamefileloader.loadGamefile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Schnitzeljagd/Savefiles/tmpFileJagd", gamefile);

        if(gamefile.getName()!=null){
            editTextName.setText(gamefile.getName());
            if(gamefile.getHint1()!=null){
                step=2;
            }
            if(gamefile.getHint2()!=null){
                step=3;
            }
            if(gamefile.getHint3()!=null){
                step=4;
            }
            if(gamefile.getHint4()!=null){
                step=5;
            }

            textCurrentCheckpoint.setText(checkpoint+step);
            Toast.makeText(this, "Nicht beendete Route geladen", Toast.LENGTH_SHORT).show();


        }
        else step = 1;
        textCurrentCheckpoint.setText(checkpoint+step);



    }

    public void resumeRoute(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Du hast deine letzte Route noch nicht vollendet. Möchtest du sie vollenden?").setCancelable(false)
                .setPositiveButton("JA!", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(ContextCompat.checkSelfPermission(NewGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locLis);
                        }

                        if(gamefile.getName()==null) {
                            textCurrentCheckpoint.setText(checkpoint+step);
                            foto.setEnabled(true);
                            naechster.setEnabled(true);

                        }
                        dialogInterface.cancel();


                    }
                })
                .setNegativeButton("NEIN!", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gameFileWriter.clearTmpFile();
                        gamefile=new Gamefile();
                        editTextName.setText("Name der Route eingeben");
                        editTextHinweis.setText("Hier deinen Hinweis eingeben");
                        step=1;
                        textCurrentCheckpoint.setText(String.valueOf("Checkpoint: "+"\n" +step));


                        if(ContextCompat.checkSelfPermission(NewGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locLis);
                        }
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
}



