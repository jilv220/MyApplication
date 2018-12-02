package com.example.sky32.myapplication;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import android.app.Activity;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.hardware.Camera;

import android.hardware.Camera.CameraInfo;

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
    Button takepic , stop_timer;
    ImageView preview;
    SurfaceView vf;

    SurfaceHolder shvf;

    Camera camera;
    private android.hardware.Camera.PictureCallback pictureCallback;

    boolean working = false;
    ///
    TextView text1, textview;

    private static final String FORMAT = "%02d:%02d:%02d";
    ///timeset milliseconds
    final int time = 5000;

    @Override

    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView) findViewById(R.id.textView1);

        vf = (SurfaceView) findViewById(R.id.camerapreview);
        shvf = vf.getHolder();

        shvf.addCallback(surfaceListener);

        shvf.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        takepic = (Button) findViewById(R.id.takepicture);

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

        //gal

        pictureCallback = new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap c = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), null, true);
                String fn = currentDataFormat();
                storePhotoToStorage(c, fn);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                MainActivity.this.camera.startPreview();
            }
        };

    }
    private String currentDataFormat () {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTime = dateFormat.format(new Date());
        return currentTime;
    }

    public SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback()
    {
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

            for(int i=0; i < numberOfCameras; i++)

            {
                Camera.getCameraInfo(i, cameraInfo);

                if(cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)

                    int_cameraID = i;

            }
            camera = Camera.open(int_cameraID);
            try
            {
                camera.setPreviewDisplay(shvf);

            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        @Override

        public void surfaceDestroyed(SurfaceHolder holder)

        { camera.stopPreview();;
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

                    text1.setText(""+String.format(FORMAT,
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
    public void start ()
    {
       // cd();

        if (camera != null && !working)

        { Timer timer = new Timer();

            TimerTask t = new TimerTask()
            {
                @Override
                public void run()
                {
                    start ();
                }
            };

            timer.schedule(t, time);
            camera.takePicture(null, null, takePicture);

            working = true;
        }

    }

    View.OnClickListener onClickListener_takepic = new OnClickListener()
    {

        @Override

        public void onClick(View v)

        {   working = false;
            start();
            cd();

        }

    };
    View.OnClickListener onClickListener_stop = new OnClickListener()
    {
        @Override

        public void onClick(View v)

        {   working = true;

         //   start();
         //   cd();

        }

    };
    ////////
    private void storePhotoToStorage(Bitmap c, String fn) {
        File output = new File(Environment.getExternalStorageDirectory(),
                "/P/" + "photo" + fn);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            c.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    ////

    public Camera.PictureCallback takePicture = new Camera.PictureCallback()

    {

        @Override

        public void onPictureTaken(byte[] data, Camera camera)

        {
            if (data != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,  0,  data.length);

                preview.setImageBitmap(bitmap);
                camera.startPreview();

                working = false;

            }

        }
    };
}
