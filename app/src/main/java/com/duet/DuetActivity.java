package com.duet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;


import com.duet.exceptions.FFmpegCommandAlreadyRunningException;
import com.duet.exceptions.FFmpegNotSupportedException;
import com.duet.lib.FFmpeg;
import com.duet.lib.FFmpegExecuteResponseHandler;
import com.duet.lib.LoadBinaryResponseHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DuetActivity extends AppCompatActivity {

    private VideoView andExoPlayerView, andExoPlayerView1;
    private TextView recordBtn;
    private FFmpeg ffmpeg;
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
            loadFFMpegBinary();
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


    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                    makesameHieghtVideo1();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }

    private void makesameHieghtVideo1() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(progressMsg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        newfirstVideoPath = getVideoFilePath();
        String[] command = {"-i", firstVideoPath, "-vf", "scale=160:500", newfirstVideoPath};
        try {
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    makesameHieghtVideo2();
                }

                @Override
                public void onProgress(String message) {
                    progressDialog.setMessage(progressMsg);
                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                }

                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    //if make vertical duet then make width same of both videos
//if make horizontal duet then make layout_height same of both videos
    private void makesameHieghtVideo2() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(progressMsg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        newsecondVideoPath = getVideoFilePath();
        String[] command = {"-i", secondVideoPath, "-vf", "scale=160:500", newsecondVideoPath};
        try {
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    addAddtwoVideo();
                }

                @Override
                public void onProgress(String message) {
                    progressDialog.setMessage(progressMsg);
                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                }

                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }


    protected String getVideoFilePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName + "/";
        File dir = new File(path);
        String fname = new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "duet.mp4";

        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            dir.mkdir();
        }
        return path + fname;
    }

    private void addAddtwoVideo() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(progressMsg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        String outputVideo = getVideoFilePath();
        if (duetType == 0) {
            type = "hstack";
        } else {
            type = "vstack";
        }
        String[] command = {"-i", newfirstVideoPath, "-i", newsecondVideoPath, "-filter_complex", type, outputVideo};
        try {
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
//                    Intent intent = new Intent(DuetActivity.this, OutputActivity.class);
//                    intent.putExtra("url", outputVideo);
//                    startActivity(intent);
//                    finish();
                    // here your result  in outputVideo uri

                }

                @Override
                public void onProgress(String message) {
                    progressDialog.setMessage(progressMsg);
                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                }

                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();


                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
