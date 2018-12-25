package colin.com.module.camera.photo.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import colin.com.module.camera.photo.base.MeasureHelper;
import colin.com.module.camera.photo.utils.LogUtils;


/**
 * @author nie yunlong
 * @description
 * @date 2018/10/30
 */
public class CameraOverloadView extends SurfaceView {
    /**
     * surfaceHolder
     */
    private SurfaceHolder mHolder;
    /**
     * 给百分比 或者大小 测量大小
     */
    private MeasureHelper mMeasureHelper;

    /**
     * 是否自适应
     */
    private boolean mAdjustViewBounds = true;
    /**
     * 预览大小宽度
     */
    private int mPreSizeWidth;
    /**
     * 预览大小 高度
     */
    private int mPreSizeHeight;



    public CameraOverloadView(Context context) {
        super(context);
        init();
    }

    public CameraOverloadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setZOrderOnTop(true);
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mMeasureHelper = new MeasureHelper(this);
    }

    /**
     * 设置预览具体数值
     * 如果是具体数值的话 表示 预览的就是这个宽度 高度 可以直接得到画布的宽高比
     * 如果是比例的话 需要直接使用{@link CameraSurfaceView#getSurfaceMatrix()}
     * @param preSizeWidth
     * @param preSizeHeight
     */
    public void setPreSize(int preSizeWidth, int preSizeHeight) {
        mPreSizeWidth=preSizeWidth;
        mPreSizeHeight=preSizeHeight;
        mMeasureHelper.setVideoSize(preSizeWidth, preSizeHeight);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.measure(widthMeasureSpec, heightMeasureSpec, mAdjustViewBounds);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
        LogUtils.log("===>onMeasure" + mMeasureHelper.getMeasuredWidth() + ",height" + mMeasureHelper.getMeasuredHeight());
    }

    public boolean isAdjustViewBounds() {
        return mAdjustViewBounds;
    }

    public void setAdjustViewBounds(boolean mAdjustViewBounds) {
        this.mAdjustViewBounds = mAdjustViewBounds;
    }
}
