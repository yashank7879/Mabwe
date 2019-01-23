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
import android.widget.ProgressBar;
import android.widget.VideoView;

import mabwe.com.mabwe.R;
import mabwe.com.mabwe.utils.Utils;

import static mabwe.com.mabwe.utils.ToastClass.showToast;

public class VideoViewActivtiy extends AppCompatActivity {

    private VideoView videoView;
    private String setVideoURI="";
    private ProgressBar progressBar_detail;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);


         dialog = new ProgressDialog(VideoViewActivtiy.this);
        dialog.setMessage("loading...");


        videoView=findViewById(R.id.videoView);
        progressBar_detail=findViewById(R.id.progressBar_detail);
        Intent intent=getIntent();

        if(intent!=null){
            setVideoURI=intent.getStringExtra("setVideoURI");
            setVideo();
        }

        else {
            showToast(VideoViewActivtiy.this,getString(R.string.video_not_available));
        }
    }


    public void setVideo(){
        dialog.show();
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(setVideoURI);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        progressBar_detail.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dialog.dismiss();
                progressBar_detail.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                mp.setLooping(true);
            }


        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                dialog.dismiss();
                Utils.openAlertDialog(VideoViewActivtiy.this, getString(R.string.video_not_supported));
                progressBar_detail.setVisibility(View.GONE);
                return false;
            }
        });


        videoView.start();
        if(null!=videoView){
            videoView.stopPlayback();
        }
    }
}
