package com.duet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.myduetlib.fun.DuetActivity;
import com.myduetlib.fun.WorkDone;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    VideoView video_view1, video_view2, video_view3;
    Button btn1, btn2, btn3;
    String firstVideoPath, secondVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermission();
        init();
    }

    private void init() {
        video_view1 = findViewById(R.id.video_view1);
        video_view2 = findViewById(R.id.video_view2);
        video_view3 = findViewById(R.id.video_view3);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a Video "), 1);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a Video "), 2);
            }
        });
        btn3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DuetActivity.class);
            intent.putExtra("firstVideoPath", firstVideoPath);
            intent.putExtra("secondVideoPath", secondVideoPath);
            intent.putExtra("outputFolderName", "Duet");
            intent.putExtra("progressBarMsg", "progressBarMsg wait..");
            intent.putExtra("duetType", 1);
            startActivityForResult(intent, 3);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedVideo = data.getData();
                try {
                    firstVideoPath = PathUtil.getPath(this, selectedVideo);
                    video_view1.setVideoPath(firstVideoPath);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(video_view1);
                    video_view1.setMediaController(mediaController);
                    video_view1.seekTo(1);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
            if (requestCode == 2) {
                Uri selectedVideo = data.getData();
                try {
                    secondVideoPath = PathUtil.getPath(this, selectedVideo);
                    video_view2.setVideoPath(secondVideoPath);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(video_view2);
                    video_view2.setMediaController(mediaController);
                    video_view2.seekTo(1);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
            if (requestCode == 3) {
                String message = data.getStringExtra("MESSAGE");
                video_view3.setVideoPath(message);
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(video_view3);
                video_view3.setMediaController(mediaController);
                video_view3.seekTo(1);
            }
        }
    }

    private boolean checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, 0);
            } else {
                //do here
            }
        } else {
            //do here
        }
        return true;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
