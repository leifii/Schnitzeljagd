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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
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
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewGameActivity extends Activity implements View.OnClickListener {

    private LocationManager locMan;
    private LocationListener locLis;

    private Gamefile gamFi;
    private Gamefilewriter gamFiWri;

    private Location myLastLocation;

    public final int REQUEST_ID = 200;
    private TextureView tv1;

    private String toWrite;
    private Boolean firstGpsSignal = true;
    private TextView textCurrentCheckpoint;

    private Button ende, naechster;

    public static final String EXTRA_MESSAGE = "de.unidue.mse.thewesleycrusher.schnitzeljagd.MESSAGE";







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
    String name;
    String camID;
    Size imageSize;
    Integer step ;
    String hinweis;
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


        /*
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewGameActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
        }
        */




        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if(!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            noGpsAlert();
        }

        locLis=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                myLastLocation.set(location);

                if(firstGpsSignal){
                    Toast toast = Toast.makeText(NewGameActivity.this, "GPS enabled", Toast.LENGTH_SHORT);
                    toast.show();
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

            }
        };


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locLis);
        }







        gamFi = new Gamefile();
        gamFiWri = new Gamefilewriter(gamFi);
        step =1 ;
       // getActionBar().setDisplayHomeAsUpEnabled(true);

        hThread = new HandlerThread("Background Handler");
        hThread.start();
        handler = new Handler(hThread.getLooper());

        Button foto = (Button) findViewById(R.id.button_setphoto);
        foto.setOnClickListener(this);

        Button gps = (Button) findViewById(R.id.button_setgps);
        gps.setOnClickListener(this);

        naechster = (Button) findViewById(R.id.button_nextstop);
        naechster.setOnClickListener(this);


        /*
        Button hinweis =(Button) findViewById(R.id.button_setText);
        hinweis.setOnClickListener(this);
        */

        ende = (Button) findViewById(R.id.button_abschließen);
        ende.setOnClickListener(this);

        tv1 = (TextureView) findViewById(R.id.tv1);
        tv1.setSurfaceTextureListener(tv1Listener);
        textCurrentCheckpoint = (TextView) findViewById(R.id.textViewCurrentCheckpoint);
        textCurrentCheckpoint.setText(String.valueOf(step));

        myLastLocation = new Location("schnitzeljagd");
        myLastLocation.set(locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        //checkPem();












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
                    if(step==1){
                        EditText edi = (EditText) findViewById(R.id.editText_Name);
                        name = edi.getEditableText().toString();
                        gamFi.setName(name);
                    }

                    takePicture();
                    //gamFi.setName(name);
                }
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_setgps:
                if(checkName())
                {

                    //getLoc();
                    //gamFi.setName(name);
                    speicherGPS(step);
                    String locations = "Longitude: "+String.valueOf(myLastLocation.getLongitude()) +"\nLatitude: " + String.valueOf(myLastLocation.getLatitude());
                    Toast toast = Toast.makeText(this, locations, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_nextstop:
                if(checkName()) {
                    if (checkHinweis()) {
                        if(step==1) {
                            EditText edi1 = (EditText) findViewById(R.id.editText_Name);
                            name = edi1.getEditableText().toString();
                            gamFi.setName(name);
                            Toast toast = Toast.makeText(this, name, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        EditText edi2 = (EditText) findViewById(R.id.editText_Hinweis);
                        hinweis = edi2.getEditableText().toString();
                        speicherHinweis(step, hinweis);
                        speicherGPS(step);

                        if(step<=5) {
                            if(step<6) {
                                step++;
                            }
                            if(step==5){
                                naechster.setEnabled(false);
                                naechster.setClickable(false);
                            }
                            textCurrentCheckpoint.setText(String.valueOf("Checkpoint: "+"\n" +step));
                            edi2.setText("");
                        }
                        else{
                            Toast toast = Toast.makeText(this, "Es wurden bereits 5 Checkpoints erstellt", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    } else {
                        Toast.makeText(NewGameActivity.this, "Verfasse einen Hinweis", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_abschließen:
                if(checkName()) {
                    if (checkHinweis()) {
                        //gamFi.setName(name);
                        if(step==1){
                            EditText edi1 = (EditText) findViewById(R.id.editText_Name);
                            name = edi1.getEditableText().toString();
                            gamFi.setName(name);
                        }
                        //gamFiWri.writeGameFile();
                        EditText edi2 = (EditText) findViewById(R.id.editText_Hinweis);
                        hinweis = edi2.getEditableText().toString();
                        speicherHinweis(step, hinweis);
                        speicherGPS(step);

                        if(runsOnEmulator()) {
                            showStringToWrite();
                        }
                        else{
                            gamFiWri.writeGameFile();
                        }
                    } else {
                        Toast.makeText(NewGameActivity.this, "Verfasse einen Hinweis", Toast.LENGTH_SHORT).show();
                    }
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
                        //gamFi.setName(name);
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


            File directory = new File(Environment.getExternalStorageDirectory() + "/schnitzeljagd", name);
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


    /*
    //Position herausfinden
    private void getLoc() {
         {
            @Override
            public void onLocationChanged(Location location) {
            speicherGPS(step, location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }

        };

    }
        */



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
    }

    public void speicherGPS (int i){

        Double j = myLastLocation.getLatitude();
        Double k = myLastLocation.getLongitude();
    switch (i){
        case 1:
            if( j != 0 && k !=0 ){
                gamFi.setLatitude1(j);
                gamFi.setLongitude1(k);
            }
            break;
        case 2:
            if( j != 0 && k !=0 ){
                gamFi.setLatitude2(j);
                gamFi.setLongitude2(k);
            }
            break;
        case 3:
            if( j != 0 && k !=0 ){
                gamFi.setLatitude3(j);
                gamFi.setLongitude3(k);
            }
            break;
        case 4:
            if( j != 0 && k !=0 ){
                gamFi.setLatitude4(j);
                gamFi.setLongitude4(k);
            }
            break;

        case 5:
            if( j != 0 && k !=0 ){
                gamFi.setLatitude5(j);
                gamFi.setLongitude5(k);
            }
            break;

    }
    }
    public void speicherHinweis(int i, String h){
        switch (i){
            case 1:
                gamFi.setHint1(h);
                break;
            case 2:
                gamFi.setHint2(h);
                break;
            case 3:
                gamFi.setHint3(h);
                break;
            case 4:
                gamFi.setHint4(h);
                break;
            case 5:
                gamFi.setHint5(h);
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



    /*
    @Override
    public void onLocationChanged(Location location) {
        //speicherGPS(step, location.getLatitude(), location.getLongitude());
        myLastLocation.set(location);

        if(firstGpsSignal){
            Toast toast = Toast.makeText(this, "GPS enabled", Toast.LENGTH_SHORT);
            toast.show();
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

        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);

    }
    */

    public void showStringToWrite(){


       new Thread(new Runnable(){
            public void run(){


                ///Checks for the information to write to the File and saves it in a String

                if(gamFi.getName()!=null){
                    toWrite="<name>\r\n" +gamFi.getName()+"\r\n</name>";
                }
                if(gamFi.getLongitude1()!=0){
                    toWrite+="\r\n<Longitude1>\r\n"+String.valueOf(gamFi.getLongitude1())+"\r\n</Longitude1>";
                }
                if(gamFi.getLongitude2()!=0){
                    toWrite+="\r\n<Longitude2>\r\n"+String.valueOf(gamFi.getLongitude2())+"\r\n</Longitude2>";
                }
                if(gamFi.getLongitude3()!=0){
                    toWrite+="\r\n<Longitude3>\r\n"+String.valueOf(gamFi.getLongitude3())+"\r\n</Longitude3>";
                }
                if(gamFi.getLongitude4()!=0){
                    toWrite+="\r\n<Longitude4>\r\n"+String.valueOf(gamFi.getLongitude4())+"\r\n</Longitude4>";
                }
                if(gamFi.getLongitude5()!=0){
                    toWrite+="\r\n<Longitude5>\r\n"+String.valueOf(gamFi.getLongitude5())+"\r\n</Longitude5>";
                }
                if(gamFi.getLatitude1()!=0){
                    toWrite+="\r\n<Latitude1>\r\n"+String.valueOf(gamFi.getLatitude1())+"\r\n</Latitude1>";
                }
                if(gamFi.getLatitude2()!=0){
                    toWrite+="\r\n<Latitude2>\r\n"+String.valueOf(gamFi.getLatitude2())+"\r\n</Latitude2>";
                }
                if(gamFi.getLatitude3()!=0){
                    toWrite+="\r\n<Latitude3>\r\n"+String.valueOf(gamFi.getLatitude3())+"\r\n</Latitude3>";
                }
                if(gamFi.getLatitude4()!=0){
                    toWrite+="\r\n<Latitude4>\r\n"+String.valueOf(gamFi.getLatitude4())+"\r\n</Latitude4>";
                }
                if(gamFi.getLatitude5()!=0){
                    toWrite+="\r\n<Latitude5>\r\n"+String.valueOf(gamFi.getLatitude5())+"\r\n</Latitude5>";
                }
                if(gamFi.getHint1()!=null){
                    toWrite+="\r\n<Hint1>\r\n"+gamFi.getHint1()+"\r\n</Hint1>";
                }
                if(gamFi.getHint2()!=null){
                    toWrite+="\r\n<Hint2>\r\n"+gamFi.getHint2()+"\r\n</Hint2>";
                }
                if(gamFi.getHint3()!=null){
                    toWrite+="\r\n<Hint3>\r\n"+gamFi.getHint3()+"\r\n</Hint3>";
                }
                if(gamFi.getHint4()!=null){
                    toWrite+="\r\n<Hint4>\r\n"+gamFi.getHint4()+"\r\n</Hint4>";
                }
                if(gamFi.getHint5()!=null){
                    toWrite+="\r\n<Hint5>\r\n"+gamFi.getHint5()+"\r\n</Hint5>";
                }

                Intent intent = new Intent(NewGameActivity.this, ShowHintActivity.class);
                String arsch = String.valueOf(myLastLocation.getLatitude());
                intent.putExtra(EXTRA_MESSAGE, toWrite);
                //intent.putExtra(EXTRA_MESSAGE, arsch);
                startActivity(intent);

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
}



