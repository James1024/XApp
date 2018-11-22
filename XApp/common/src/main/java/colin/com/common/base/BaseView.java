package colin.com.common.base;


import android.support.annotation.Keep;

/**
 * <p>View接口的基类</p>
 *
 * @author yiche
 * @name BaseView
 */
@Keep
public interface BaseView<T> {

    void setPresenter(T presenter);

}
