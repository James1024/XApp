package colin.com.module.news.detail;


import colin.com.common.base.BasePresenter;
import colin.com.common.base.BaseView;
import colin.com.module.news.data.bean.MessageDetail;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name NewsContract
 */
public interface NewsDetailContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showNewsDetail(MessageDetail detail);

    }

    interface Presenter extends BasePresenter {

        /**
         * 获取最新列表
         *
         * @param newsId 新闻id
         */
        void getNewsDetail(String newsId);

    }

}
