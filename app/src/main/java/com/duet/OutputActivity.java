package com.duet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class OutputActivity extends AppCompatActivity {
    VideoView video_view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        video_view1 = findViewById(R.id.video_view);
        Bundle bundle = getIntent().getExtras();
        String firstVideoPath = bundle.getString("url");
        video_view1.setVideoPath(firstVideoPath);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video_view1);
        video_view1.setMediaController(mediaController);
        video_view1.seekTo(1);
        TextView tv_show = findViewById(R.id.tv_show);
        tv_show.setText(firstVideoPath);
    }
}