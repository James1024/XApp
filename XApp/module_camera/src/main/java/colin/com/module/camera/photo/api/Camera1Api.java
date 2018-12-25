package colin.com.module.camera.photo.api;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;
import java.util.List;
import java.util.SortedSet;

import colin.com.module.camera.BuildConfig;
import colin.com.module.camera.photo.base.AspectRatio;
import colin.com.module.camera.photo.base.Constants;
import colin.com.module.camera.photo.base.Size;
import colin.com.module.camera.photo.base.SizeMap;

/**
 * @author nie yunlong
 * @description camera1 api
 * @date 2018/10/30
 */
public class Camera1Api implements ICamera1Api, Camera.PreviewCallback {


    private static final int INVALID_CAMERA_ID = -1;
    /**
     * 分辨率模式
     */
    private static final int CAMERA_ASPECT_RATIO = 0;
    /**
     * 具体数值模式
     */
    private static final int CAMERA_SPECIFIC_VALUE = 1;

    /**
     * 预览的窗口
     */
    private IPreview mPreview;
    /**
     * 异步线程
     */
    private HandlerThread mHandlerThread;
    /**
     * 接收异步任务的handler
     */
    private Handler mCameraHandler;
    /**
     * 摄像头句柄
     */
    private Camera mCamera = null;
    /**
     * 选中的摄像头
     */
    private int mCameraId;
    /**
     * cameraInfo
     */
    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    /**
     * 预览的size
     */
    private final SizeMap mPreviewSizes = new SizeMap();
    /**
     * 图片的size
     */
    private final SizeMap mPictureSizes = new SizeMap();
    /**
     * 默认是16：9
     */
    private AspectRatio mAspectRatio;

    //没有适合的比例 使用默认的预览大小
    private static final int DEFAULT_PREWIDTH = 640;

    private static final int DEFAULT_PREHEIGHT = 480;
    /**
     * camera参数
     */
    private Camera.Parameters mCameraParams;
    /**
     * 预览方向
     */
    private int mOrientation = 0;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * callBack buffer 内存复用
     */
    private byte[] mYuvBuffer;
    /**
     * 展示预览
     */
    private boolean mShowingPreview;
    /**
     * 闪光灯
     */
    private boolean mLanternEnable;

    /**
     * 模式
     */
    private @CameraModel
    int mCameraModel = CAMERA_ASPECT_RATIO;
    /**
     * 预览的宽度 只有在具体数值模式才能使用{@link CameraModel#CAMERA_SPECIFIC_VALUE}
     */
    private int mPreWidth = 0;
    /**
     * 预览的高度 只有在具体数值模式才能使用 {@link CameraModel#CAMERA_SPECIFIC_VALUE}
     */
    private int mPreHeight = 0;
    /**
     * 回调
     */
    private ICamera mCameraListener;

    @IntDef({CAMERA_ASPECT_RATIO, CAMERA_SPECIFIC_VALUE})
    public @interface CameraModel {

    }

    public Camera1Api(Context context, IPreview preview) {
        this.mContext = context;
        this.mPreview = preview;
    }


    public void setCameraListener(ICamera cameraListener) {
        mCameraListener = cameraListener;
    }

    @Override
    public void start(final int cameraId) {
        if (mCamera != null) {
            stop();
        }
        mHandlerThread = new HandlerThread("cameraThread");
        mHandlerThread.start();
        mCameraHandler = new Handler(mHandlerThread.getLooper());
        mCameraHandler.post(new Runnable() {
            @Override
            public void run() {
                chooseCamera(cameraId);
                open();
            }
        });
    }

    @Override
    public void stop() {
        release();
    }

    @Override
    public void switchCamera() {
        if (mCamera != null) {
            int number = Camera.getNumberOfCameras();
            for (int i = 0; i < number; i++) {
                if (mCameraId != i) {
                    mCameraId = i;
                    stop();
                    start(mCameraId);
                    return;
                }
            }
        }
    }

    @Override
    public AspectRatio getAspectRatio() {
        return mAspectRatio;
    }

    @Override
    public void setAspectRatio(AspectRatio aspectRatio) {
        mAspectRatio = aspectRatio;
    }

    @Override
    public void openFlash() {

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes != null && !supportedFlashModes.isEmpty()) {
                if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                    mLanternEnable = true;
                } else {
                    log("Lantern unsupported");
                }
            }
        }
    }


    @Override
    public void closeFlash() {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mLanternEnable = false;
        }

    }

    /**
     * 打开摄像头
     */
    private void open() {
        try {
            mCamera = Camera.open(mCameraId);
            //摄像头参数
            mCameraParams = mCamera.getParameters();


            // AspectRatio
            if (getAspectRatio() == null) {
                mAspectRatio = Constants.DEFAULT_ASPECT_RATIO;
            }
            // Supported preview sizes
            mPreviewSizes.clear();
            for (Camera.Size size : mCameraParams.getSupportedPreviewSizes()) {
                mPreviewSizes.compareRatio(mAspectRatio, new Size(size.width, size.height));
            }
            // Supported picture sizes;
            mPictureSizes.clear();
            for (Camera.Size size : mCameraParams.getSupportedPictureSizes()) {
                mPictureSizes.compareRatio(mAspectRatio, new Size(size.width, size.height));
            }

            adjustCameraParameters();
            adjustFocusModel();
            adjustOrientation(mContext);

            mCamera.setDisplayOrientation(mOrientation);
            mCamera.setParameters(mCameraParams);

            if (mPreview != null) {
                mYuvBuffer = new byte[mCameraParams.getPreviewSize().width * mCameraParams.getPreviewSize().height * 3 / 2];
                if (mPreview.getOutputClass() == SurfaceView.class) {
                    mCamera.setPreviewDisplay(mPreview.getSurfaceHolder());
                    mCamera.addCallbackBuffer(mYuvBuffer);
                    mCamera.setPreviewCallbackWithBuffer(Camera1Api.this);

                } else if (mPreview.getOutputClass() == TextureView.class) {
                    mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
                    mCamera.addCallbackBuffer(mYuvBuffer);
                    mCamera.setPreviewCallbackWithBuffer(Camera1Api.this);
                } else if (mPreview.getOutputClass() == GLSurfaceView.class) {
                    mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
                }
            }
            mShowingPreview = true;
            mCamera.startPreview();
            if (mCameraListener != null) {
                mCameraListener.callBackPreSize(getPreWidth(), getPreHeight());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * camera 模式 自动对焦模式
     */
    private void adjustFocusModel() {
        List<String> supportedFocusModes = mCameraParams.getSupportedFocusModes();
        if (supportedFocusModes != null && !supportedFocusModes.isEmpty()) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else {
                mCameraParams.setFocusMode(supportedFocusModes.get(0));
            }
        }
    }

    /**
     * 方向
     *
     * @param context
     */
    private void adjustOrientation(Context context) {
        Camera.getCameraInfo(mCameraId, mCameraInfo);
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();  //获得显示器件角度
        int degrees = 0;
        log("---->" + "getRotation's rotation is " + String.valueOf(rotation));
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mOrientation = (mCameraInfo.orientation + degrees) % 360;
            mOrientation = (360 - mOrientation) % 360;  // compensate the mirror
        } else {  // back-facing
            mOrientation = (mCameraInfo.orientation - degrees + 360) % 360;
        }
    }

    /**
     * 适合的分辨率
     */
    private void adjustCameraParameters() {
        if (getCameraModel() == CAMERA_SPECIFIC_VALUE) {
            boolean isFindPreSize = checkParamsIsOk();
            if (!isFindPreSize) {
                mPreWidth = DEFAULT_PREWIDTH;
                mPreHeight = DEFAULT_PREHEIGHT;
            }
            mCameraParams.setPreviewSize(mPreWidth, mPreHeight);
            mCameraParams.setPictureSize(mPreWidth, mPreHeight);
            log("===>预览参数==>具体数值模式" + mPreWidth + ",height" + mPreHeight + ",pic" + mCameraParams.getPictureSize().width + ",picHeight" + mCameraParams.getPictureSize().height);
            return;
        }
        SortedSet<Size> preSizesSortSet = mPreviewSizes.getCompareSize(mAspectRatio);
        //获取比例最适合的预览大小
        Size preViewSize = preSizesSortSet.size() > 0 ? preSizesSortSet.last() : (new Size(DEFAULT_PREWIDTH, DEFAULT_PREHEIGHT));

        SortedSet<Size> pictureSizeSortSet = mPictureSizes.getCompareSize(mAspectRatio);
        //获取比例最适合的图片
        Size pictureSize = pictureSizeSortSet.size() > 0 ? pictureSizeSortSet.last() : (new Size(DEFAULT_PREWIDTH, DEFAULT_PREHEIGHT));

        log("===>预览参数" + preViewSize.width + ",height" + preViewSize.height + ",pic" + pictureSize.width + ",height" + pictureSize.height);
        mCameraParams.setPreviewSize(preViewSize.width, preViewSize.height);
        mCameraParams.setPictureSize(pictureSize.width, pictureSize.height);


    }

    /**
     * 检验具体数值 预览宽度 高度 有没有 没有默认一个数值
     */
    private boolean checkParamsIsOk() {
        for (Camera.Size size : mCameraParams.getSupportedPreviewSizes()) {
            if (size.width == mPreWidth && size.height == mPreHeight) {
                return true;
            }
        }
        return false;
    }

    /**
     * 释放内存 以及camera句柄
     */
    private void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.release();
            mCamera = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mCameraHandler != null) {
            mCameraHandler.removeCallbacksAndMessages(null);
            mCameraHandler = null;
        }
        mShowingPreview = false;
    }

    /**
     * This rewrites {@link #mCameraId} and {@link #mCameraInfo}.
     */
    private void chooseCamera(@Camera1Facing int cameraId) {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == cameraId) {
                mCameraId = i;
                return;
            }
        }
        mCameraId = INVALID_CAMERA_ID;
    }

    /**
     * 是否正在预览
     *
     * @return
     */
    public boolean isShowingPreview() {
        return mShowingPreview;
    }

    public int getCameraModel() {
        return mCameraModel;
    }

    public void setCameraModel(@CameraModel int mCameraModel) {
        this.mCameraModel = mCameraModel;
    }

    /**
     * 预览大小
     *
     * @param preViewSizeWidth
     * @param preViewSizeHeight
     * @param isUserAspectRatio true 按照百分比 去取预览的数值 false 预览大小直接取数值
     */
    public void setPreViewSizeAndPicSize(int preViewSizeWidth, int preViewSizeHeight, boolean isUserAspectRatio) {
        mPreWidth = preViewSizeWidth;
        mPreHeight = preViewSizeHeight;
        if (isUserAspectRatio) {
            setAspectRatio(AspectRatio.of(mPreWidth, mPreHeight));
        } else {
            setCameraModel(CAMERA_SPECIFIC_VALUE);
        }

    }

    /**
     * 拍摄图片
     * 使用取一帧图片
     */
    private void takePhoto() {
        if (mCamera == null) {
            return;
        }
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();
                if (mCameraListener != null) {
                    mCameraListener.onPictureTaken(data);
                }
            }
        });
    }

    /**
     * log
     *
     * @param hintMsg
     */
    private static void log(String hintMsg) {
        if (BuildConfig.DEBUG) {
            Log.e("Camera1Api", hintMsg);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mCameraListener != null) {
            mCameraListener.onPreviewFrame(data, camera);
        }
        camera.addCallbackBuffer(mYuvBuffer);
    }

    //=============================//

    public int getCameraId() {
        return mCameraId;
    }

    public int getCameraOrientation() {
        return mCameraInfo.orientation;
    }


    public int getPreWidth() {
        return mCameraParams.getPreviewSize().width;
    }

    public int getPreHeight() {
        return mCameraParams.getPreviewSize().height;
    }


}
