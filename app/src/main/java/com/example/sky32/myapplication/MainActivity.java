package com.example.sky32.myapplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.hardware.Camera;

import android.hardware.Camera.CameraInfo;

import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;

import android.view.SurfaceHolder;

import android.view.SurfaceView;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity

{
    Button takepic, stop_timer;
    Button choosePic;                  // the button for choosing the pics

    ImageView preview;
    SurfaceView vf;
    SurfaceHolder shvf;

    Camera camera;
    private android.hardware.Camera.PictureCallback pictureCallback;

    boolean working = false;
    ///
    TextView text1, textview, textUri;

    String mCurrentPhotoPath;
    private static final String FORMAT = "%02d:%02d:%02d";
    ///timeset milliseconds
    final int time = 5000;

    @Override

    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.textView1);
        textUri = (TextView) findViewById(R.id.URL); // store the photo url

        vf = (SurfaceView) findViewById(R.id.camerapreview);
        shvf = vf.getHolder();

        shvf.addCallback(surfaceListener);

        shvf.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        takepic = (Button) findViewById(R.id.takepicture);
        choosePic = (Button) findViewById(R.id.choosePhoto);

        preview = (ImageView) findViewById(R.id.preview);
        stop_timer = (Button) findViewById(R.id.button2);

        ///input
        //  final EditText edittext = (EditText)findViewById(R.id.editText2);
        //  String a = edittext.getText().toString();
        // int timeset = Integer.parseInt(a);
        //    public int setTime(int t) {
        //       time = t;
        //}
        // final int time = timeset;
        //int a = Integer.parseInt((TextView)findViewById(R.id.textview).getText());


        // setListener
        takepic.setOnClickListener(onClickListener_takepic);
        stop_timer.setOnClickListener(onClickListener_stop);
        choosePic.setOnClickListener(onClickListener_choosePic); // the listener for choosing pic

        //gal
        pictureCallback = new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (b!= null) {
                    File outPut = new File(Environment.getExternalStorageDirectory()+"/MyPhotos/");
                    if(!outPut.isDirectory()){
                        outPut.mkdir();
                    }

                    outPut = new File(outPut,Long.toString(System.currentTimeMillis()) + ".jpg");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(outPut);
                        b.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                MainActivity.this.camera.startPreview();
            }
        };
    }


    private String currentDataFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTime = dateFormat.format(new Date());
        return currentTime;
    }

    public SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {
        @Override

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)

        {
            Camera.Parameters parameters = camera.getParameters();

            parameters.setPreviewSize(width, height);
            camera.startPreview();
        }

        @Override

        public void surfaceCreated(SurfaceHolder holder)

        {
            int int_cameraID = 0;
            int numberOfCameras = Camera.getNumberOfCameras();

            CameraInfo cameraInfo = new CameraInfo();

            for (int i = 0; i < numberOfCameras; i++)

            {
                Camera.getCameraInfo(i, cameraInfo);

                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)

                    int_cameraID = i;

            }

            camera = Camera.open(int_cameraID);

            try {
                camera.setPreviewDisplay(shvf);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override

        public void surfaceDestroyed(SurfaceHolder holder)

        {
            camera.stopPreview();
            ;
            camera.release();

            camera = null;
        }

    };

    ///countdown section
    public void cd() {
        if (working) {
            //final int time = 5000;
            ///
            new CountDownTimer(time, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {

                    text1.setText("" + String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    //  text1.setText("Saved");
                    working = true;
                    cd();
                }
            }.start();

        }
    }

    public void start() {
        // cd();

        if (camera != null && !working)

        {
            Timer timer = new Timer();

            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    start();
                }
            };

            timer.schedule(t, time);
            camera.takePicture(null, null, takePicture);

            working = true;
        }

    }

    View.OnClickListener onClickListener_takepic = new OnClickListener() {

        @Override

        public void onClick(View v)

        {
            working = false;
            start();
            cd();

        }

    };

    // the listener for choosing pic
    View.OnClickListener onClickListener_choosePic = new OnClickListener() {

        @Override

        public void onClick(View v)

        {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 0);
        }

    };


    View.OnClickListener onClickListener_stop = new OnClickListener() {
        @Override

        public void onClick(View v)

        {
            working = true;

            //   start();
            //   cd();

        }

    };
    ////
@Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            textUri.setText(targetUri.toString());
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                preview.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

        public Camera.PictureCallback takePicture = new Camera.PictureCallback()

        {

            @Override

            public void onPictureTaken(byte[] data, Camera camera)

            {
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    preview.setImageBitmap(bitmap);
                    camera.startPreview();

                    working = false;

                }

            }
        };
    }

