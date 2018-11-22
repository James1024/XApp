package colin.com.common.base;

import android.support.annotation.Keep;

/**
 * <p>类说明</p>
 * @author yiche
 * @name ApplicationDelegate
 */
@Keep
public interface IApplicationDelegate {

    void onCreate();

    void onTerminate();

    void onLowMemory();

    void onTrimMemory(int level);

}
