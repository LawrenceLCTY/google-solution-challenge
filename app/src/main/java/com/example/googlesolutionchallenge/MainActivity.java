package com.example.googlesolutionchallenge;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


public class MainActivity extends AppCompatActivity {

    String appId = getString(R.string.agora_app_id);
    /*String token = getString(R.string.agora_access_token);*/

    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler mRtcEventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRtcEventHandler = new IRtcEngineEventHandler() {
            @Override
            public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
                super.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
                Log.i("uid video", uid+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(uid);
                    }
                });
            }
        };
        initializeAgoraEngine();
    }

    private void initializeAgoraEngine(){
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
            joinChannel();
            setupLocalVideo();
            setupVideoProfile();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupVideoProfile(){
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration());
    }

    private void setupLocalVideo(){
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FILL, 0));
    }

    private void setupRemoteVideo(int uid){
        FrameLayout container = findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FILL, uid));
        surfaceView.setTag(uid);
    }

    private void joinChannel(){
        mRtcEngine.joinChannel(null, "aye", "Extra Optional Data", new Random().nextInt(10000000)+1);
    }

    private void leaveChannel(){
        mRtcEngine.leaveChannel();
    }



}