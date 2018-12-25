package colin.com.module.camera.photo.base;


/**
 * Created by nie yunlong on 2018/5/15.
 * 分辨率 16:9 4:3
 */
public class AspectRatio {

    //宽度比例
    private int mWidthRatio = 16;
    //高度比例
    private int mHeightRatio = 9;

    /**
     * 分辨率的比例
     *
     * @param x 4
     * @param y 3
     * @return
     */
    public static AspectRatio of(int x, int y) {
        AspectRatio aspectRatio = new AspectRatio(x, y);
        return aspectRatio;
    }

    private AspectRatio(int x, int y) {
        mWidthRatio = x;
        mHeightRatio = y;
    }


    /**
     * 匹配此分辨率的
     *
     * @param size
     * @return
     */
    public boolean matches(Size size) {
        float ratioPreSize = (float) size.width / size.height;
        float ratioReal = (float) mWidthRatio / mHeightRatio;
        return ratioReal == ratioPreSize;
    }

    public int getWidthRatio() {
        return mWidthRatio;
    }

    public void setWidthRatio(int mWidthRatio) {
        this.mWidthRatio = mWidthRatio;
    }

    public int getHeightRatio() {
        return mHeightRatio;
    }

    public void setHeightRatio(int mHeightRatio) {
        this.mHeightRatio = mHeightRatio;
    }
}
