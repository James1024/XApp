package colin.com.module.news.detail;


import colin.com.common.base.InfoCallback;
import colin.com.module.news.data.NewsDataSource;
import colin.com.module.news.data.bean.MessageDetail;
import colin.com.module.news.data.source.RemoteNewsDataSource;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name GirlsPresenter
 */
public class NewsDetailPresenter implements NewsDetailContract.Presenter {

    private NewsDetailContract.View mView;
    private NewsDataSource mDataSource;

    public NewsDetailPresenter(NewsDetailContract.View view) {
        mView = view;
        mDataSource = new RemoteNewsDataSource();
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }


    @Override
    public void getNewsDetail(String newsId) {
        mDataSource.getNewsDetail(newsId, new InfoCallback<MessageDetail>() {
            @Override
            public void onSuccess(MessageDetail detail) {
                if (mView.isActive()) {
                    mView.showNewsDetail(detail);
                }
            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

}
