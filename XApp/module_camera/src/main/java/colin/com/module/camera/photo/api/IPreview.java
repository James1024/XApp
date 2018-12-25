package colin.com.module.camera.photo.api;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * @author nie yunlong
 * @description 预览的接口
 * @date 2018/10/30
 */
public interface IPreview {
    /**
     * 预览的类名 根据类名判断 surface
     *
     * @return
     */
    Class getOutputClass();

    /**
     * @return
     */
    Surface getSurface();

    /**
     * 对于surfaceView
     *
     * @return
     */
    SurfaceHolder getSurfaceHolder();

    /**
     * textureView 以及 GlSurfaceView
     *
     * @return
     */
    SurfaceTexture getSurfaceTexture();

    /**
     * 获取View
     * @return
     */
    View getView();
}
