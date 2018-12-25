package colin.com.module.camera.photo.ui;

import android.graphics.Bitmap;
import android.hardware.Camera;

/**
 * @author nie yunlong
 * @description 预览UI 需要数据回调
 * @date 2018/10/31
 */
public interface ISurfaceCallBack {

    /**
     * 预览数据 NV21
     *
     * @param data
     */
    void onPreviewFrame(byte[] data, Camera camera);

    /**
     * 拍照数据
     */
    void onPictureTaken(Bitmap bitmap);

    /**
     * 预览大小
     *
     * @param preWidth  预览的宽度
     * @param preHeight 预览的高度
     */
    void callBackPreSize(int preWidth, int preHeight);
}
