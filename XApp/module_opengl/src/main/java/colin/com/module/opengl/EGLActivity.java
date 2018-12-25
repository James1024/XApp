package colin.com.module.opengl;

import android.Manifest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import colin.com.common.base.BaseActivity;
import colin.com.module.opengl.render.GLRenderer;

/**
 * @author wanglr
 * @date 2018/12/6
 */
public class EGLActivity extends BaseActivity implements SurfaceHolder.Callback {
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    GLRenderer glRenderer;

    @Override
    public int getLayout() {
        return R.layout.layout_egl;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView() {

        requestPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 100);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = 300;


        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText("test.......");

        linearLayout.addView(textView);

        getWindowManager().addView(linearLayout, params);

        surfaceView.getHolder().addCallback(this);
        glRenderer = new GLRenderer();
        glRenderer.start();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TextView textView=new TextView(getApplicationContext());
        ImageView imageView=new ImageView(getBaseContext());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        glRenderer.render(holder.getSurface(), width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


}
