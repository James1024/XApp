package colin.com.common.base;


import android.support.annotation.Keep;
import android.view.View;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name IFragmentDelegate
 */
@Keep
public interface IViewDelegate {

    BaseFragment getFragment(String name);

    View getView(String name);

}
