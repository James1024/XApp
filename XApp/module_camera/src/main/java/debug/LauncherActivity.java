package debug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import colin.com.module.camera.photo.CameraActivity;

/**
 * <p>组件开发模式下，用于传递数据的启动Activity，集成模式下无效</p>
 *
 * @author yiche
 * @name LauncherActivity
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在这里传值给需要调试的Activity
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("id", "9500116");
        startActivity(intent);
        finish();
    }

}
