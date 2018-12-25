package colin.com.module.camera.photo.api;

import android.hardware.Camera;

/**
 * @author nie yunlong
 * @description 预览数据
 * @date 2018/10/30
 */
public interface ICamera {
    /**
     * 预览大小
     *
     * @param picWidth  预览的宽度
     * @param picHeight 预览的高度
     */
    void callBackPreSize(int picWidth, int picHeight);

    /**
     * 预览大小
     *
     * @param data
     * @param camera
     */
    void onPreviewFrame(byte[] data, Camera camera);

    /**
     * 拍摄图片
     *
     * @param data
     */
    void onPictureTaken(byte[] data);
}
