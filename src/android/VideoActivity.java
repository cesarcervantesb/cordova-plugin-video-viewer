package com.ccervantesb.videoviewer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    private static final String TAG = "VideoActivity";
    private static final String STR_ERR_LOAD_VIDEO = "An error occurred while loading video source.";
    private VideoView videoView;
    private TextView tvTitle;
    private ImageButton btnClose, btnShare;
    private ProgressBar progressBar;
    private String src, strTitle;
    private boolean showShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate VideoActivity...");

        String packageName = getPackageName();

        // find activity view
        setContentView(getResources().getIdentifier("activity_video", "layout", packageName));

        // find views
        videoView = (VideoView) findViewById(getResources().getIdentifier("videoView", "id", packageName));
        tvTitle = (TextView) findViewById(getResources().getIdentifier("txt_title", "id", packageName));
        btnClose = (ImageButton) findViewById(getResources().getIdentifier("btn_close", "id", packageName));
        btnShare = (ImageButton) findViewById(getResources().getIdentifier("btn_share", "id", packageName));
        progressBar = (ProgressBar) findViewById(getResources().getIdentifier("progressBar", "id", packageName));

        // get intent extras
        try {
            src = getIntent().getStringExtra("src");
            strTitle = getIntent().getStringExtra("title");
            showShareButton = getIntent().getBooleanExtra("share", false);
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        // change title
        if(strTitle == null || strTitle.isEmpty()) {
            strTitle = src.split("/")[src.split("/").length - 1];
        }
        tvTitle.setText(strTitle);

        // set share button visibility
        btnShare.setVisibility(View.INVISIBLE);

        // set button listeners
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick share button");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                if (src.startsWith("http") || src.startsWith("https")) {
                    shareIntent.setType("text/*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, src);
                }
                else {
                    shareIntent.setType("video/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, src);
                }

                startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

        // set video viewer listeners
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "MediaPlayer is prepared.");
                // set share button visibility and hide progressbar
                btnShare.setVisibility(showShareButton ? View.VISIBLE : View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                mediaPlayer.start();
            }
        });
        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                // hide progress bar
                progressBar.setVisibility(View.INVISIBLE);
                // notify
                Toast.makeText(getApplicationContext(), STR_ERR_LOAD_VIDEO, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        // Set default media controller
        videoView.setMediaController(new MediaController(this));

        // load video
        Log.d(TAG, "setVideoURI: " + src);
        videoView.setVideoURI(Uri.parse(src));
    }
}