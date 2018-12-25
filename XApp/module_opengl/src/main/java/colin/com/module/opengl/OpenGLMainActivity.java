package colin.com.module.opengl;

import android.content.Intent;
import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import colin.com.common.base.BaseActivity;

@Route(path = "/opengl/main")
public class OpenGLMainActivity extends BaseActivity {


    @Override
    public int getLayout() {
        return R.layout.activity_opengl_main;
    }

    @Override
    public void initView() {

    }

    public void triangle1(View view) {
        startActivity(new Intent(OpenGLMainActivity.this,TriangleActivity.class));
    }

    public void square(View view) {
        startActivity(new Intent(OpenGLMainActivity.this,SquareActivity.class));

    }

    public void circle(View view) {
        startActivity(new Intent(OpenGLMainActivity.this,CircleActivity.class));

    }
    public void cubic(View view) {
        startActivity(new Intent(OpenGLMainActivity.this,CubicActivity.class));

    }
    public void EGL(View view) {
        startActivity(new Intent(OpenGLMainActivity.this,EGLActivity.class));

    }
}
