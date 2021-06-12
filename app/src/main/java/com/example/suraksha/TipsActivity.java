package com.example.suraksha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class TipsActivity extends AppCompatActivity {

    VideoView videoView; int videoClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        videoView = findViewById(R.id.tipsVideoView);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoClicked = getIntent().getIntExtra("video", 0);
        videoView.setVideoPath("android.resource://com.example.suraksha/" + videoClicked);
        videoView.setMediaController(mediaController);
        videoView.start();
    }

}