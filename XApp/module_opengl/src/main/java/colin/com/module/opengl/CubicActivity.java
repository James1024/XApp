package colin.com.module.opengl;

import butterknife.BindView;
import colin.com.common.base.BaseActivity;
import colin.com.module.opengl.render.Cubic;
import colin.com.module.opengl.view.MyGView;

/**
 * @author wanglr
 * @date 2018/12/3
 */
public class CubicActivity extends BaseActivity {
    @BindView(R2.id.glSurfaceView21)
    MyGView glSurfaceView;

    @Override
    public int getLayout() {
        return R.layout.layout_cubic;
    }

    @Override
    public void initView() {
        glSurfaceView.setShape(Cubic.class);

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
