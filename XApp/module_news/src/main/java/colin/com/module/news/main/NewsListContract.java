package colin.com.module.news.main;


import colin.com.common.base.BasePresenter;
import colin.com.common.base.BaseView;
import colin.com.module.news.data.bean.StoryList;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name NewsContract
 */
public interface NewsListContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showNewsList(StoryList info);

    }

    interface Presenter extends BasePresenter {

        /**
         * 获取最新列表
         *
         * @param date
         */
        void getNewMessages(int page, int size, String date);

    }

}
