package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.util.Size;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private Gamefile gamFi;
    private Gamefilewriter gamFiWri;

    public final int REQUEST_ID = 200;
    private TextureView tv1;



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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NewGameActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
            }
            camMan.openCamera(camID, stateCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
       // getActionBar().setDisplayHomeAsUpEnabled(true);

        hThread = new HandlerThread("Background Handler");
        hThread.start();
        handler = new Handler(hThread.getLooper());

        Button foto = (Button) findViewById(R.id.button_setphoto);
        foto.setOnClickListener(this);

        Button gps = (Button) findViewById(R.id.button_setgps);
        gps.setOnClickListener(this);

        Button naechster = (Button) findViewById(R.id.button_nextstop);
        foto.setOnClickListener(this);

        Button hinweis =(Button) findViewById(R.id.button_setText);
        hinweis.setOnClickListener(this);

        Button ende = (Button) findViewById(R.id.button_abschließen);
        foto.setOnClickListener(this);




        tv1 = (TextureView) findViewById(R.id.tv1);
        tv1.setSurfaceTextureListener(tv1Listener);

    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_setphoto:
                if(checkName())
                takePicture();

                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_setgps:
                if(checkName())
                    getLoc();
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_nextstop:
                if(checkName())
                    // bla bla methode
                    ;
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_abschließen:
                if(checkName())
                    // bla bla Methode
                   ;
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_setText:
                if(checkName())
                    // bla bla Methode
                   ;
                else
                    Toast.makeText(NewGameActivity.this, "Bitte gebe dem Spiel einen Namen" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewGameActivity.this, HinweisActivity.class));
                break;

        }
    }

    public boolean checkName (){
        String chname ;
        EditText name = (EditText) findViewById(R.id.editText_Name);
        chname = name.getEditableText().toString();

        if (chname.equalsIgnoreCase("Name des Spiels eingeben"))
        return false;
        else{
            return true;
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

            final File file = new File(Environment.getExternalStorageDirectory() + "/meinBild.jpg");
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

    //Position herausfinden
    private void getLoc() {
        locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        locLis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            // hier reinschreiben wo die location gespeichert werden soll :D
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
    checkPem();
    }
    void checkPem(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
                locMan.requestLocationUpdates("gps", 5000, 0, locLis);
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
    }


}



