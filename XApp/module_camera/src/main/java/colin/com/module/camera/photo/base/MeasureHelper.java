package colin.com.module.camera.photo.base;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @author nie yunlong
 * @description
 * @date 2018/7/13
 */
public class MeasureHelper {
    /**
     * 预览的view
     */
    private WeakReference<View> mWeakView;
    /**
     * 测量视频宽度
     */
    private int mMeasuredWidth;
    /**
     * 测量视频高度
     */
    private int mMeasuredHeight;
    /**
     * 原视频的高度
     */
    private int mVideoWidth;
    /**
     * 原视频的高度
     */
    private int mVideoHeight;

    public MeasureHelper(View view) {
        mWeakView = new WeakReference<>(view);
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    /**
     * 分辨率
     *
     * @param aspectRatio
     */
    public void setAspectRatio(AspectRatio aspectRatio) {
        mVideoWidth = aspectRatio.getWidthRatio();
        mVideoHeight = aspectRatio.getHeightRatio();
    }


    /**
     * 测量大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @param isAdjust
     */
    public void measure(int widthMeasureSpec, int heightMeasureSpec, boolean isAdjust) {
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (isAdjust) {
            if (getView() == null || mVideoWidth == 0 || mVideoHeight == 0) {
                mMeasuredWidth = widthSpecSize;
                mMeasuredHeight = heightSpecSize;
            }
            if (widthMode == View.MeasureSpec.EXACTLY && heightMode != View.MeasureSpec.EXACTLY) {
                int height = (int) (View.MeasureSpec.getSize(widthMeasureSpec));
                if (heightMode == View.MeasureSpec.AT_MOST) {
                    height = Math.min(height, View.MeasureSpec.getSize(heightMeasureSpec));
                }
                mMeasuredWidth = widthSpecSize;
                mMeasuredHeight = height;
            } else if (widthMode != View.MeasureSpec.EXACTLY && heightMode == View.MeasureSpec.EXACTLY) {
                int width = (int) (View.MeasureSpec.getSize(heightMeasureSpec));
                if (widthMode == View.MeasureSpec.AT_MOST) {
                    width = Math.min(width, View.MeasureSpec.getSize(widthMeasureSpec));
                }
                mMeasuredWidth = width;
                mMeasuredHeight = heightSpecSize;


            } else {
                mMeasuredWidth = widthSpecSize;
                mMeasuredHeight = heightSpecSize;
            }
            if (mMeasuredWidth > mMeasuredHeight) { //横屏
                mMeasuredWidth = (int) ((mMeasuredHeight * mVideoWidth) / (float) mVideoHeight);
                mMeasuredHeight = mMeasuredHeight;
            } else {
                mMeasuredWidth = mMeasuredWidth;
                mMeasuredHeight = (int) (mMeasuredWidth * mVideoWidth / (float) mVideoHeight);
            }

        } else {
            mMeasuredWidth = widthSpecSize;
            mMeasuredHeight = heightSpecSize;
        }

    }

    /**
     * 获取测量的宽度
     *
     * @return
     */
    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }


    /**
     * 获取测量的高度
     *
     * @return
     */
    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }


    /**
     * 获取View
     *
     * @return
     */
    public View getView() {
        if (mWeakView == null) {
            return null;
        }
        return mWeakView.get();
    }

    public int getmVideoWidth() {
        return mVideoWidth;
    }

    public void setmVideoWidth(int mVideoWidth) {
        this.mVideoWidth = mVideoWidth;
    }

    public int getmVideoHeight() {
        return mVideoHeight;
    }

    public void setmVideoHeight(int mVideoHeight) {
        this.mVideoHeight = mVideoHeight;
    }
}
