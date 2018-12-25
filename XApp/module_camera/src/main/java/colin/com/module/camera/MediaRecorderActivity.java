package colin.com.module.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

import colin.com.common.base.BaseActivity;
import colin.com.common.utils.SdcardUtils;

/**
 * @author wanglr
 * @date 2018/11/28
 */
public class MediaRecorderActivity extends BaseActivity {

    MediaRecorder recorder;
    MediaPlayer mediaPlayer;
    File parent;



    @Override
    public int getLayout() {
        return R.layout.layout_mediarecorder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initView() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO}, 100);
            return;
        }
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE); audioManager.setMode(AudioManager.MODE_NORMAL);
        initRecord();
        parent = new File(SdcardUtils.getAppLocalPath());
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }


    @RequiresPermission(allOf = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO})
    private void initRecord() {

        try {
            mediaPlayer=new MediaPlayer();
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            File file=new File(SdcardUtils.getAppLocalPath() + "aaa.aac");
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.setOutputFile(file.getPath());
            recorder.prepare();
            recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    Log.e("--oninfo", "what=" + what);
                }
            });
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.e("--onError", "what=" + what);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void startRecord(View view) {
        recorder.start();

    }

    public void stopRecord(View view) {
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release();
    }

    public void play(View view) {

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(SdcardUtils.getAppLocalPath() + "aaa.aac");
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
