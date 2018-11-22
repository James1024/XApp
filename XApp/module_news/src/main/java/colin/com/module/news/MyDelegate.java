package colin.com.module.news;

import android.support.annotation.Keep;

import com.orhanobut.logger.Logger;

import colin.com.common.base.IApplicationDelegate;
import colin.com.common.base.ViewManager;

/**
 * <p>类说明</p>
 * @author yiche
 * @name MyDelegate
 */
@Keep
public class MyDelegate implements IApplicationDelegate {

    @Override
    public void onCreate() {
        Logger.init("pattern");
        //主动添加
        ViewManager.getInstance().addFragment(0, NewsFragment.newInstance());
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }
}
