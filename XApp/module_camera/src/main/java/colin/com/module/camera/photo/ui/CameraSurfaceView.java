package colin.com.module.camera.photo.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import colin.com.module.camera.photo.api.IPreview;
import colin.com.module.camera.photo.utils.LogUtils;


/**
 * @author nie yunlong
 * @description
 * @date 2018/10/30
 */
public class CameraSurfaceView extends SurfaceView implements IPreview, SurfaceHolder.Callback {
    /**
     * surfaceHolder
     */
    private SurfaceHolder mHolder;

    /**
     * 是否自适应
     */
    private boolean mAdjustViewBounds = true;

    /**
     * surfaceView 宽度
     */
    private int mSurfaceWidth = 0;
    /**
     * surfaceView 高度
     */
    private int mSurfaceHeight = 0;
    /**
     * 根据图片的大小 与 View 大小 得到宽度 高度 画布比例
     */
    private Matrix mScaleMatrix = new Matrix();
    /**
     * camera api 操作通过代理使用
     */
    private ISurfaceWrapper mSurfaceWrapper;


    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);
        mSurfaceWrapper = new SurfaceWrapper();
        mSurfaceWrapper.init(getContext(), this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mSurfaceWrapper.measure(widthMeasureSpec, heightMeasureSpec, mAdjustViewBounds);
        setMeasuredDimension(mSurfaceWrapper.getMeasureWidth(), mSurfaceWrapper.getMeasureHeight());
        LogUtils.log("===>onMeasure" + mSurfaceWrapper.getMeasureWidth() + ",height" + mSurfaceWrapper.getMeasureHeight());
    }


    @Override
    public Class getOutputClass() {
        return SurfaceView.class;
    }

    @Override
    public Surface getSurface() {
        return mHolder.getSurface();
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return null;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceWrapper.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceWrapper.stop();
    }

    /**
     * 预览的大小 跟正常输出的图片大小 有可能不一样 但是比例是一样的
     * 获取 宽高缩放比
     * {@link ISurfaceCallBack#callBackPreSize(int, int)}
     * <pre>
     *      public void callBackPreSize(int preWidth, int preHeight) {
     * //        LogUtils.log("===>callBackPreSize" + preWidth + ",height" + preHeight);
     * //        setSurfaceChange(preWidth, preHeight);
     * //    }
     * </pre>
     */
    public void setSurfaceChange(int preWidth, int preHeight) {
        LogUtils.log("===>setSurfaceChange" + (mSurfaceWidth / (float) preWidth) + ",heightScale" + (mSurfaceHeight
                / (float) preHeight));
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            mScaleMatrix.setScale(mSurfaceWidth / (float) preWidth, mSurfaceHeight
                    / (float) preHeight);
        } else {
            mScaleMatrix.setScale(mSurfaceWidth / (float) preHeight, mSurfaceHeight
                    / (float) preWidth);
        }
    }

    public Matrix getSurfaceMatrix() {
        return mScaleMatrix;

    }


    public ISurfaceWrapper getSurfaceWrapper() {
        return mSurfaceWrapper;
    }

}
