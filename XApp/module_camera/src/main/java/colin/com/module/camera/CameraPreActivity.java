package colin.com.module.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import butterknife.BindView;
import butterknife.ButterKnife;
import colin.com.common.base.BaseActivity;

/**
 * @author wanglr
 * @date 2018/11/27
 */
public class CameraPreActivity extends BaseActivity {
    @BindView(R2.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R2.id.textureView)
    TextureView textureView;
    @BindView(R2.id.glSurfaceView)
    GLSurfaceView glSurfaceView;
    Camera camera;
    SurfaceTexture mSurfaceTexture ;



    @Override
    public int getLayout() {
        return R.layout.layout_camera_pre;
    }

    @Override
    public void initView() {
        camera=Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters = camera.getParameters();
        List<Integer> list= parameters.getSupportedPreviewFormats();
        for (int i = 0; i <list.size() ; i++) {
            Log.e("---PreviewFormats",list.get(i)+"");
        }
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);
        camera.setPreviewCallback(new PicCallBack());

        surfaceView.getHolder().addCallback(new CallBack());
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer(){

            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig config) {

            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override
            public void onDrawFrame(GL10 gl) {

            }
        });
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    camera.setPreviewTexture(surface);
                    camera.startPreview();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                camera.release();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    class CallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            try {
//                camera.setPreviewDisplay(holder);
//                camera.startPreview();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            Canvas canvas=holder.lockCanvas();
//            Paint p=new Paint();
//            p.setAntiAlias(true);
//            p.setColor(Color.parseColor("#567894"));
//            canvas.drawCircle(100,100,100,p);
//            holder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
//            camera.release();
        }
    }

    class PicCallBack implements Camera.PreviewCallback{

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
//            Log.e("--data",data.length+"");

        }
    }
}
