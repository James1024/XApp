package colin.com.module.news;

import android.support.annotation.Keep;
import android.view.View;

import colin.com.common.base.BaseFragment;
import colin.com.common.base.IViewDelegate;


/**
 * <p>类说明</p>
 * @author yiche
 * @name MyViewDelegate
 */
@Keep
public class MyViewDelegate implements IViewDelegate {

    @Override
    public BaseFragment getFragment(String name) {
        return NewsFragment.newInstance();
    }

    @Override
    public View getView(String name) {
        return null;
    }
}
