package com.example.mediarecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private MediaRecorder mediarecorder;
    SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = null;
        mediarecorder = null;
    }

    @Override
    public void onClick(View view) {
        if (R.id.button == view.getId()) {
            mediarecorder = new MediaRecorder();// 创建mediarecorder对象
            // 设置录制视频源为Camera(相机)
            mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
            mediarecorder
                    .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置录制的视频编码h263 h264
            mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            mediarecorder.setVideoSize(176, 144);
            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            mediarecorder.setVideoFrameRate(40);
            mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());

            Camera.Parameters parameters = getCameraInstance().getParameters();
            parameters.setPreviewSize(176, 144);
            getCameraInstance().setParameters(parameters);
            // 设置视频文件输出的路径
            mediarecorder.setOutputFile("/sdcard/love.mp4");
            try {
                // 准备录制
                mediarecorder.prepare();
                // 开始录制
                mediarecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (R.id.button2 == view.getId()) {
            if (mediarecorder != null) {
                // 停止录制
                mediarecorder.stop();
                // 释放资源
                mediarecorder.release();
                mediarecorder = null;
            }
        } else {
            Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);//画质0.5
            mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 70000);//70s
            startActivityForResult(mIntent, 1);//CAMERA_ACTIVITY = 1
        }
    }

    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }
}
