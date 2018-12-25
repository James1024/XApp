package colin.com.module.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import colin.com.common.base.BaseActivity;
import colin.com.module.opengl.render.Circle;
import colin.com.module.opengl.render.Shape;
import colin.com.module.opengl.view.MyGView;

/**
 * @author wanglr
 * @date 2018/12/3
 */
public class CircleActivity extends BaseActivity {
    @BindView(R2.id.glSurfaceView)
    MyGView glSurfaceView;

    @Override
    public int getLayout() {
        return R.layout.layout_circle;
    }

    @Override
    public void initView() {
        glSurfaceView.setShape(Circle.class);
    }





    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

}
