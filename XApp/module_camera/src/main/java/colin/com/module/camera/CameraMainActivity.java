package colin.com.module.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

import colin.com.common.base.BaseActivity;
import colin.com.module.camera.photo.CameraActivity;

/**
 * @author wanglr
 * @date 2018/11/27
 */
@Route(path="/camera/main")
public class CameraMainActivity extends BaseActivity {


    @Override
    public int getLayout() {
        return R.layout.layout_camera_main;
    }

    @Override
    public void initView() {

    }

    public void Camera1Api(View view){
        startActivity(new Intent(CameraMainActivity.this,CameraActivity.class));
    }

    public void CameraPre(View view){
        startActivity(new Intent(CameraMainActivity.this,CameraPreActivity.class));

    }

    public void AudioRecord(View view) {
        startActivity(new Intent(CameraMainActivity.this,AudioRecordActivity.class));
         }

    public void MediaRecorder(View view) {
        startActivity(new Intent(CameraMainActivity.this,MediaRecorderActivity.class));

    }

    public void MediaExtractor(View view) {
        startActivity(new Intent(CameraMainActivity.this,MediaExtractorActivity.class));

    }

    public void MediaCodeC(View view) {
        startActivity(new Intent(CameraMainActivity.this,MediaCodecActivity.class));

    }
    public void AudioDecoder(View view) {
        startActivity(new Intent(CameraMainActivity.this,AudioDecoderActivity.class));

    }
}
