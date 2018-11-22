package colin.com.module.news.main;


import colin.com.common.base.InfoCallback;
import colin.com.module.news.data.NewsDataSource;
import colin.com.module.news.data.bean.StoryList;
import colin.com.module.news.data.source.RemoteNewsDataSource;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name GirlsPresenter
 */
public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;
    private NewsDataSource mDataSource;

    public NewsListPresenter(NewsListContract.View view) {
        mView = view;
        mDataSource = new RemoteNewsDataSource();
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void getNewMessages(int page, int size, String date) {
        mDataSource.getNewsList(date, new InfoCallback<StoryList>() {
            @Override
            public void onSuccess(StoryList info) {
                if (mView.isActive()) {
                    mView.showNewsList(info);
                }
            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }
}
