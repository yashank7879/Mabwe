package mabwe.com.mabwe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import mabwe.com.mabwe.R;

public class VideoClipActivity extends AppCompatActivity {

    private MediaController mediaController;
    private VideoView video;
    private int position = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoclip);

        initView();
    }

    private void initView() {

        video = findViewById(R.id.videoView);
        findViewById(R.id.ll_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(VideoClipActivity.this, LoginActivity.class);
                startActivity(in);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
            }
        });

        PlayVideoInBackground();
    }

    private void PlayVideoInBackground() {
        mediaController = new MediaController(this);
        mediaController.setAnchorView(video);
        Uri uri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        //Uri uri = Uri.parse("http://www.ebookfrenzy.com/android_book/movie.mp4");

        video.setMediaController(null);
        video.setVideoURI(uri);
        video.requestFocus();
        //video.setVideoPath("http://www.ebookfrenzy.com/android_book/movie.mp4");

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        video.start();

    }
}
