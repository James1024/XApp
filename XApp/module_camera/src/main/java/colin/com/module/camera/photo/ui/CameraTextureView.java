package colin.com.module.camera.photo.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;

import colin.com.module.camera.photo.api.IPreview;
import colin.com.module.camera.photo.utils.LogUtils;


/**
 * @author nie yunlong
 * @description
 * @date 2018/10/30
 */
public class CameraTextureView extends TextureView implements IPreview, TextureView.SurfaceTextureListener {
    /**
     * 画布
     */
    private Surface mSurface;


    /**
     * 是否自适应
     */
    private boolean mAdjustViewBounds = true;
    /**
     * camera api 操作通过代理使用
     */
    private ISurfaceWrapper mSurfaceWrapper;

    public CameraTextureView(Context context) {
        super(context);
        init();
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        setSurfaceTextureListener(this);
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
        return TextureView.class;
    }

    @Override
    public Surface getSurface() {
        return mSurface;
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return null;
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return super.getSurfaceTexture();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        mSurfaceWrapper.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceWrapper.stop();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    public ISurfaceWrapper getSurfaceWrapper() {
        return mSurfaceWrapper;
    }
}
