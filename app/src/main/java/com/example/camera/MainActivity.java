package com.example.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jpegkit.Jpeg;
import com.jpegkit.JpegImageView;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{

    private CameraKitView camKitView;
    private JpegImageView jpg_img_view;
    private FloatingActionButton btn_Capture;
    private Toolbar tb_toolbar;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //init View

        tb_toolbar = findViewById(R.id.tb_main_toolbar);
        tb_toolbar.inflateMenu(R.menu.main);
        tb_toolbar.setOnMenuItemClickListener(this);

        camKitView = findViewById(R.id.kit_main_cam_view);

        btn_Capture = findViewById(R.id.btn_main_capture);
        btn_Capture.setOnClickListener(photoOnClickListener);

        jpg_img_view = findViewById(R.id.jpeg_main_image);

        //permission

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
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + "/" + "DCIM" + "/" + "Photo"+  "/" + "MyApp" + System.currentTimeMillis()
                                    + "photo.jpg");
                            try {
                                FileOutputStream outputStream = new FileOutputStream(file.getPath());
                                outputStream.write(photo);
                                outputStream.close();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                            final Jpeg jpeg = new Jpeg(photo);
                            jpg_img_view.post(new Runnable() {
                                @Override
                                public void run() {
                                    jpg_img_view.setJpeg(jpeg);
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.kit_main_cam_view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivity(intent);
            return true;
        }
        return false;
    }
}