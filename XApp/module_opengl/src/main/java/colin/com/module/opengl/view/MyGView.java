package colin.com.module.opengl.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import colin.com.module.opengl.render.Circle;
import colin.com.module.opengl.render.MyRender;
import colin.com.module.opengl.render.Shape;

/**
 * @author wanglr
 * @date 2018/12/3
 */

public class MyGView extends GLSurfaceView {
    private MyRender renderer;

    public MyGView(Context context) {
        this(context,null);
    }

    public MyGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        setRenderer(renderer=new MyRender(this));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setShape(Class<? extends Shape> clazz){
        try {
            renderer.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
