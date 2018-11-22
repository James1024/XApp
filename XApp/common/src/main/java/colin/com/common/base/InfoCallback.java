package colin.com.common.base;

import android.support.annotation.Keep;

/**
 * <p>数据回调接口</p>
 *
 * @author yiche
 * @name InfoCallback
 */
@Keep
public interface InfoCallback<T> {

    void onSuccess(T info);

    void onError(int code, String message);

}
