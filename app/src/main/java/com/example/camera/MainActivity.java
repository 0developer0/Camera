package com.example.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity{

    private CameraKitView camKitView;
    private JpegImageView jpg_img_view;

    private FloatingActionButton btn_Capture;
    private Button btn_gallery;

    private Toolbar tb_toolbar;

    private Button btn_flash;

    private SharedPre sharedPre;

    private static final String TAG = "MainActivity";

    private static final File file = new File(Environment.getExternalStorageDirectory()
            + "/" + "DCIM" + "/" + "Photo"+  "/" + "MyApp" + System.currentTimeMillis()
            + ".jpg");
    private boolean flash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //init View

        tb_toolbar = findViewById(R.id.tb_main_toolbar);

        camKitView = findViewById(R.id.kit_main_cam_view);

        btn_Capture = findViewById(R.id.btn_main_capture);
        btn_gallery = findViewById(R.id.btn_main_gallery);
        btn_flash = findViewById(R.id.btn_main_flash);

        btn_Capture.setOnClickListener(photoOnClickListener);
        btn_gallery.setOnClickListener(galleryOnClickListener);
        btn_flash.setOnClickListener(flashOnClickListener);

        jpg_img_view = findViewById(R.id.jpeg_main_image);

        sharedPre = new SharedPre(getApplicationContext());

        if(sharedPre.getFlash()){
            flash = true;
            try {
                camKitView.setFlash(CameraKit.FLASH_ON);
            } catch (RuntimeException e){
                Log.e(TAG, "onCreate: flashOn");
            }
        } else{
            btn_flash.setEnabled(false);
            flash = false;
            try {
                camKitView.setFlash(CameraKit.FLASH_OFF);
            } catch (RuntimeException e){
                Log.e(TAG, "onCreate: flashOff");
            }
        }

        camKitView.requestPermissions(MainActivity.this);

        camKitView.setCameraListener(new CameraKitView.CameraListener() {
            @Override
            public void onOpened() {
                Log.e(TAG, "onOpened: ");
            }

            @Override
            public void onClosed() {
                Log.e(TAG, "onClosed: ");
            }
        });

        camKitView.setPreviewListener(new CameraKitView.PreviewListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart: ");
            }

            @Override
            public void onStop() {
                Log.e(TAG, "onStop: ");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        camKitView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        camKitView.onResume();
    }

    @Override
    public void onPause() {
        camKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        camKitView.onStop();
        super.onStop();
    }

    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView view, final byte[] photo) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Jpeg jpeg = new Jpeg(photo);
                            try {
                                jpg_img_view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        jpg_img_view.setJpeg(jpeg);
                                        try {
                                            FileOutputStream outputStream = new FileOutputStream(file.getPath());
                                            outputStream.write(photo);
                                            outputStream.close();
                                            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                                        } catch (IOException e) {
                                            Log.e(TAG, "run: save image");
                                        }
                                    }
                                });
                            } catch (RuntimeException ignored) {
                                Log.e(TAG, "run: cant take picture");
                            }
                        }
                    }).start();
                }
            });
        }
    };

    private View.OnClickListener flashOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            flash = !flash;
            sharedPre.setFlash(flash);

            if(flash){
                try {
                    btn_flash.setEnabled(true);
                    camKitView.setFlash(CameraKit.FLASH_ON);
                } catch (RuntimeException e){
                    Log.e(TAG, "onCreate: flashOn");
                }
            } else{
                try {
                    btn_flash.setEnabled(false);
                    camKitView.setFlash(CameraKit.FLASH_OFF);
                } catch (RuntimeException e){
                    Log.e(TAG, "onCreate: flashOff");
                }
            }
        }
    };

    private View.OnClickListener galleryOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivity(intent);
        }
    };
}