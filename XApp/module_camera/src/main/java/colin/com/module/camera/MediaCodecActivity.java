package colin.com.module.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

import butterknife.BindView;
import colin.com.common.base.BaseActivity;

/**
 * @author wanglr
 * @date 2018/11/29
 */
public class MediaCodecActivity extends BaseActivity {
    @BindView(R2.id.surfacePre)
    SurfaceView surfacePre;
    @BindView(R2.id.surfaceDisplay)
    SurfaceView surfaceDisplay;
    Camera camera;


    @Override
    public int getLayout() {
        return R.layout.layout_mediacodec;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},100);
            return;
        }
        initCamera();
        initSurface();
    }

    private void initSurface() {
        surfaceDisplay.getHolder().addCallback(new DisplayCallBack());
        surfacePre.getHolder().addCallback(new PreCallBack());
    }
    @RequiresPermission(Manifest.permission.CAMERA)
    private void initCamera() {
        camera= Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters=camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewSize(400,300);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
    }


    class PreCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.release();
        }
    }

    SurfaceHolder disPlayHolder;
    class DisplayCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            disPlayHolder=holder;
            Canvas canvas=holder.lockCanvas();
            Paint paint=new Paint();
            paint.setColor(getResources().getColor(R.color.divider_color));
            canvas.drawCircle(100,100,100,paint);
            holder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public void stop(View view) {

    }

    public void start(View view) {

    }
}
