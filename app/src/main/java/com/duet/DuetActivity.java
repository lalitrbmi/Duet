package com.duet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;


import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class DuetActivity extends AppCompatActivity {

    private VideoView andExoPlayerView, andExoPlayerView1;
    private TextView recordBtn;
    private String secondVideoPath, newsecondVideoPath, firstVideoPath, newfirstVideoPath, folderName, progressMsg;
    private int duetType;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duet);
        Bundle bundle = getIntent().getExtras();
        firstVideoPath = bundle.getString("firstVideoPath");
        secondVideoPath = bundle.getString("secondVideoPath");
        folderName = bundle.getString("outputFolderName");
        progressMsg = bundle.getString("progressBarMsg");
        duetType = bundle.getInt("duetType");
        init();
    }


    private void init() {
        recordBtn = findViewById(R.id.btn_record);
        andExoPlayerView = findViewById(R.id.video_view);
        andExoPlayerView1 = findViewById(R.id.video_view1);
        andExoPlayerView.setVideoPath(firstVideoPath);
        andExoPlayerView1.setVideoPath(secondVideoPath);
        recordBtn.setOnClickListener(v -> {
//            playVideo();
            makesameHieghtVideo1();
        });
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(andExoPlayerView);
        andExoPlayerView.setMediaController(mediaController);
        andExoPlayerView.seekTo(1);
        MediaController mediaController1 = new MediaController(this);
        mediaController1.setAnchorView(andExoPlayerView1);
        andExoPlayerView1.setMediaController(mediaController1);
        andExoPlayerView1.seekTo(1);
    }

    private void playVideo() {
        andExoPlayerView.start();
        andExoPlayerView1.start();
    }


    private void releasePlayer() {
        if (andExoPlayerView != null && andExoPlayerView1 != null) {
            andExoPlayerView.stopPlayback();
            andExoPlayerView1.stopPlayback();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        pauseplayer();
    }

    private void pauseplayer() {
        if (andExoPlayerView != null && andExoPlayerView1 != null) {
            andExoPlayerView.pause();
            andExoPlayerView1.pause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }


    private void makesameHieghtVideo1() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(progressMsg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        newfirstVideoPath = getVideoFilePath();
        String[] command = {"-y", "-i", firstVideoPath, "-preset", "ultrafast", "-vf", "scale=480:840", newfirstVideoPath};
        long rc = FFmpeg.execute(command);
        if (rc == RETURN_CODE_SUCCESS) {
            makesameHieghtVideo2(progressDialog);
        } else {
            progressDialog.dismiss();
        }
    }

    //if make vertical duet then make width same of both videos
//if make horizontal duet then make layout_height same of both videos
    private void makesameHieghtVideo2(ProgressDialog progressDialog) {
        newsecondVideoPath = getVideoFilePath();
        String[] command = {"-y", "-i", secondVideoPath, "-preset", "ultrafast", "-vf", "scale=480:840", newsecondVideoPath};
        long rc = FFmpeg.execute(command);
        if (rc == RETURN_CODE_SUCCESS) {
            addAddtwoVideo(progressDialog);
        } else {
            progressDialog.dismiss();
        }
    }

    String pathkya;

    protected String getVideoFilePath() {
        String fname = new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "duet.mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File appSpecificExternalDir = new File(getExternalFilesDir("DuetLK"), fname);
            pathkya = appSpecificExternalDir.getAbsolutePath();
        } else {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName + "/";
            File dir = new File(path);

            boolean isDirectoryCreated = dir.exists();
            if (!isDirectoryCreated) {
                dir.mkdir();
            }
            pathkya = path + fname;
        }
        return pathkya;
    }


    private void addAddtwoVideo(ProgressDialog progressDialog) {
        String outputVideo = getVideoFilePath();
        if (duetType == 0) {
            type = "hstack";
        } else {
            type = "vstack";
        }
        Config.enableLogCallback(message -> Log.e(Config.TAG, message.getText()));
        Config.enableStatisticsCallback(newStatistics -> progressDialog.setMessage("progress : video "));
        String[] command = {"-y", "-i", newfirstVideoPath, "-i", newsecondVideoPath, "-preset", "ultrafast", "-filter_complex", "hstack", outputVideo};
        long rc = FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                progressDialog.dismiss();
                Toast.makeText(this, "Duet  Done", Toast.LENGTH_LONG).show();
                //output is outputVideo
                Intent intent = new Intent(this, OutputActivity.class);
                intent.putExtra("url", outputVideo);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                if (progressDialog != null)
                    progressDialog.dismiss();
            } else {
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }
}
