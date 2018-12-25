package colin.com.module.camera.photo;

import android.Manifest;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import butterknife.BindView;
import colin.com.common.base.BaseActivity;
import colin.com.module.camera.R;
import colin.com.module.camera.R2;
import colin.com.module.camera.photo.base.AspectRatio;
import colin.com.module.camera.photo.ui.CameraSurfaceView;
import colin.com.module.camera.photo.ui.ISurfaceCallBack;
import colin.com.module.camera.photo.utils.BitmapUtils;

/**
 * @author yiche
 */

public class CameraActivity extends BaseActivity {
    @BindView(R2.id.surfaceView)
    CameraSurfaceView cameraSurfaceView;
    @BindView(R2.id.imageView)
    ImageView imageView;


    @Override
    public int getLayout() {
        return R.layout.activity_camera;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},100);
        }
//        cameraSurfaceView.getSurfaceWrapper().setPreSize(640,480);
        cameraSurfaceView.getSurfaceWrapper().setAspectRatio(AspectRatio.of(16,9));
        cameraSurfaceView.getSurfaceWrapper().setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        cameraSurfaceView.getSurfaceWrapper().setCameraListener(new ISurfaceCallBack() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }

            @Override
            public void onPictureTaken(final Bitmap bitmap) {
                BitmapUtils.save(bitmap, Environment.getExternalStorageDirectory() + File.separator + "aaa.jpg",70);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }

            @Override
            public void callBackPreSize(int picWidth, int picHeight) {
                cameraSurfaceView.setSurfaceChange(picWidth, picHeight);
            }
        });
    }

    public void openLattie(View view) {
        cameraSurfaceView.getSurfaceWrapper().openFlashLight();
    }

    public void closeLattie(View view) {
        cameraSurfaceView.getSurfaceWrapper().closeFlashLight();
    }

    public void switchCamera(View view) {
        cameraSurfaceView.getSurfaceWrapper().switchCamera();
    }

    public void takePhoto(View view) {
        cameraSurfaceView.getSurfaceWrapper().takePhoto();
    }


}
