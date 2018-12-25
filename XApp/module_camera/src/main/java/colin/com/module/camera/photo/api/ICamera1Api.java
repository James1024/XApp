package colin.com.module.camera.photo.api;


import colin.com.module.camera.photo.base.AspectRatio;

/**
 * @author nie yunlong
 * @description camera 接口
 * @date 2018/10/30
 */
public interface ICamera1Api {
    /**
     * 打开摄像头
     *
     * @param cameraId 打开是哪个摄像头 {@link Camera1Facing}
     */
    void start(@Camera1Facing int cameraId);

    /**
     * 关闭摄像头
     */
    void stop();

    /**
     * 切换摄像头
     */
    void switchCamera();

    /**
     * 获取分辨率
     * @return
     */
    AspectRatio getAspectRatio();

    /**
     * 设置分辨率
     * @param aspectRatio 16:9
     */
    void setAspectRatio(AspectRatio aspectRatio);

    /**
     * 打开flash
     *  @required: <uses-permission android:name="android.permission.FLASHLIGHT"/>
     */
    void openFlash();

    /**
     * 关闭flash
     *  @required: <uses-permission android:name="android.permission.FLASHLIGHT"/>
     */
    void closeFlash();
}
