package com.myduetlib.fun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.myduetlib.FFmpeg;
import com.myduetlib.FFmpegExecuteResponseHandler;
import com.myduetlib.LoadBinaryResponseHandler;
import com.myduetlib.R;
import com.myduetlib.exceptions.FFmpegCommandAlreadyRunningException;
import com.myduetlib.exceptions.FFmpegNotSupportedException;

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
    WorkDone workDone;

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

    public void funDute(String firstVideoPath, String secondVideoPath, String outputFolderName, String progressBarMsg, int duetType, WorkDone workDone) {
        this.firstVideoPath = firstVideoPath;
        this.secondVideoPath = secondVideoPath;
        this.folderName = outputFolderName;
        this.progressMsg = progressBarMsg;
        this.duetType = duetType;
        this.workDone = workDone;
//        init();
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
        String[] command = {"-i", firstVideoPath, "-vf", "scale=360:740", newfirstVideoPath};
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

    private void makesameHieghtVideo2() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(progressMsg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        newsecondVideoPath = getVideoFilePath();
        String[] command = {"-i", secondVideoPath, "-vf", "scale=360:740", newsecondVideoPath};
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
//                    Intent intent = new Intent(DuetActivity.this, OutPutActivity.class);
//                    intent.putExtra("url", outputVideo);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", outputVideo);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    setResult(3, intent);
//                    workDone.onDone(outputVideo, true);
                    finish();
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
