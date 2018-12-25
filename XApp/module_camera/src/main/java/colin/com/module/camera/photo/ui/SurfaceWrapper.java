package colin.com.module.camera.photo.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import colin.com.module.camera.photo.api.Camera1Api;
import colin.com.module.camera.photo.api.Camera1Facing;
import colin.com.module.camera.photo.api.ICamera;
import colin.com.module.camera.photo.api.IPreview;
import colin.com.module.camera.photo.base.AspectRatio;
import colin.com.module.camera.photo.base.MeasureHelper;
import colin.com.module.camera.photo.utils.YUVUtil;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;

/**
 * @author nie yunlong
 * @description
 * @date 2018/10/31
 */
public class SurfaceWrapper extends ISurfaceWrapper implements ICamera {
    /**
     * camera api
     */
    private Camera1Api mCamera1Api;
    /**
     * 测量View
     */
    private MeasureHelper mMeasureHelper;
    /**
     * 预览view
     */
    private IPreview mPreView;

    /**
     * 摄像头ID{@link Camera1Facing}
     */
    private @Camera1Facing
    int mCameraId = CAMERA_FACING_BACK;
    /**
     * cameraListener
     */
    private ISurfaceCallBack mSurfaceListener;
    /**
     * 是否获取当前的一帧
     */
    private boolean isTakePhoto = false;

    private Context mContext;

    private ExecutorService mExecutorService;

    @Override
    public void init(Context context, IPreview preview) {
        mContext = context;
        mPreView = preview;
        mCamera1Api = new Camera1Api(context, preview);
        mCamera1Api.setCameraListener(this);
        mMeasureHelper = new MeasureHelper(preview.getView());
    }

    @Override
    public void setCameraId(int cameraId) {
        mCameraId = cameraId;
    }

    @Override
    public int getCameraId() {
        return mCameraId;
    }

    @Override
    public void setAspectRatio(AspectRatio aspectRatio) {
        mCamera1Api.setAspectRatio(aspectRatio);
        mMeasureHelper.setAspectRatio(aspectRatio);
        mPreView.getView().requestLayout();
    }

    @Override
    public void setPreSize(int preSizeWidth, int preSizeHeight) {
        mCamera1Api.setPreViewSizeAndPicSize(preSizeWidth, preSizeHeight, false);
        mMeasureHelper.setVideoSize(preSizeWidth, preSizeHeight);
        mPreView.getView().requestLayout();
    }

    @Override
    public void measure(int widthMeasureSpec, int heightMeasureSpec, boolean mAdjustViewBounds) {
        mMeasureHelper.measure(widthMeasureSpec, heightMeasureSpec, mAdjustViewBounds);
    }

    @Override
    public void start() {
        mCamera1Api.start(getCameraId());
    }

    @Override
    public void stop() {
        mCamera1Api.stop();
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
            mExecutorService = null;
        }
    }

    @Override
    public int getMeasureWidth() {
        return mMeasureHelper.getMeasuredWidth();
    }

    @Override
    public int getMeasureHeight() {
        return mMeasureHelper.getMeasuredHeight();
    }

    @Override
    public Camera1Api getCamera1Api() {
        return mCamera1Api;
    }

    @Override
    public void setCameraListener(ISurfaceCallBack surfaceCallBack) {
        mSurfaceListener = surfaceCallBack;
    }

    @Override
    public void removeCameraListener() {
        mSurfaceListener = null;
    }

    @Override
    public void openFlashLight() {
        mCamera1Api.openFlash();
    }

    @Override
    public void closeFlashLight() {
        mCamera1Api.closeFlash();
    }

    @Override
    public void switchCamera() {
        mCamera1Api.switchCamera();
    }

    @Override
    public void takePhoto() {
        isTakePhoto = true;
    }

    @Override
    public void callBackPreSize(int preWidth, int preHeight) {
        if (mSurfaceListener != null) {
            mSurfaceListener.callBackPreSize(preWidth, preHeight);
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        if (isTakePhoto) {
            isTakePhoto = false;
            if (mSurfaceListener != null) {
                //为什么使用异步去回调 主要是不会以次执行 影响效率
                if (mExecutorService == null) {
                    mExecutorService = Executors.newSingleThreadExecutor();
                }
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = YUVUtil.getBitmapFromNV21(mCamera1Api.getCameraId(), mCamera1Api.getCameraOrientation(), data, mCamera1Api.getPreWidth(), mCamera1Api.getPreHeight(), isLandOrientation());
                        mSurfaceListener.onPictureTaken(bitmap);
                    }
                });
            }
        }
        if (mSurfaceListener != null) {
            mSurfaceListener.onPreviewFrame(data, camera);
        }

    }

    @Override
    public void onPictureTaken(byte[] data) {

    }

    /**
     * 是否是横屏
     *
     * @return
     */
    public boolean isLandOrientation() {
        final int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

}
