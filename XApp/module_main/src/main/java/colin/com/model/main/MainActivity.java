package colin.com.model.main;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import colin.com.common.base.BaseActivity;
import colin.com.common.base.ViewManager;
import colin.com.common.utils.ToastUtils;


/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name MainActivity
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R2.id.news_button)
    Button newsButton;
    @BindView(R2.id.camera_button)
    Button cameraButton;
    @BindView(R2.id.fragment_button)
    Button fragmentButton;

    private long mExitTime = 0;



    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        newsButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        fragmentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.news_button) {
            //跳转到NewsCenterActivity
            ARouter.getInstance().build("/news/center").navigation();
        } else if (view.getId() == R.id.camera_button) {
            //跳转到CameraActivity
            ARouter.getInstance().build("/camera/main").navigation();
        } else if (view.getId() == R.id.fragment_button) {
            startActivity(new Intent(this, BottomNavigationActivity.class));
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //两秒之内按返回键就会退出
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showShortToast(getString(R.string.app_exit_hint));
                mExitTime = System.currentTimeMillis();
            } else {
                ViewManager.getInstance().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void OpenGL(View view) {
        ARouter.getInstance().build("/opengl/main").navigation();
    }
    public void Opencv(View view) {
        ARouter.getInstance().build("/opencv/main").navigation();
    }
}
