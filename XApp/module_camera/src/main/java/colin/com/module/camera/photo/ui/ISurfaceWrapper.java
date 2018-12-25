package colin.com.module.camera.photo.ui;

import android.content.Context;

import colin.com.module.camera.photo.api.Camera1Api;
import colin.com.module.camera.photo.api.IPreview;
import colin.com.module.camera.photo.base.AspectRatio;


/**
 * @author nie yunlong
 * @description
 * @date 2018/10/31
 */
public abstract class ISurfaceWrapper {
    public ISurfaceWrapper() {

    }

    /**
     * 初始化
     *
     * @param context
     * @param preview
     */
    abstract void init(Context context, IPreview preview);

    /**
     * 打开 前置 还是后置
     *
     * @param cameraId
     */
    public abstract void setCameraId(int cameraId);

    /**
     * 获取打开cameraId
     *
     * @return
     */
    abstract int getCameraId();

    /**
     * 设置分辨率 16：9 4：3
     *
     * @param aspectRatio
     */
    public abstract void setAspectRatio(AspectRatio aspectRatio);

    /**
     * 预览大小 具体数值
     *
     * @param preSizeWidth
     * @param preSizeHeight
     */
    public abstract void setPreSize(int preSizeWidth, int preSizeHeight);

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @param mAdjustViewBounds
     */
    abstract void measure(int widthMeasureSpec, int heightMeasureSpec, boolean mAdjustViewBounds);


    /**
     * 打开摄像头
     */
    abstract void start();

    /**
     * 停止预览
     */
    abstract void stop();

    /**
     * 获取测量宽度
     *
     * @return
     */
    abstract int getMeasureWidth();

    /**
     * 获取测量高度
     *
     * @return
     */
    abstract int getMeasureHeight();

    /**
     * 获取camera1 api 句柄
     *
     * @return
     */
    public abstract Camera1Api getCamera1Api();

    /**
     * 设置camera 回调
     *
     * @param surfaceCallBack
     */
    public abstract void setCameraListener(ISurfaceCallBack surfaceCallBack);

    /**
     * 移除listener
     */
    public abstract void removeCameraListener();

    /**
     * 打开闪光灯
     */
    public abstract void openFlashLight();

    /**
     * 关闭闪光灯
     */
    public abstract void closeFlashLight();

    /**
     * 切换摄像头
     */
    public abstract void switchCamera();

    /**
     * 拍照
     */
    public abstract void takePhoto();

}
