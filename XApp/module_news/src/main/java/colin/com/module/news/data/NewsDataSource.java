package colin.com.module.news.data;


import colin.com.common.base.InfoCallback;
import colin.com.module.news.data.bean.MessageDetail;
import colin.com.module.news.data.bean.StoryList;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name NewsDataSource
 */
public interface NewsDataSource {


    /**
     * 获取当天的新闻列表
     *
     * @param date     日期
     * @param callback 回调
     */
    void getNewsList(String date, InfoCallback<StoryList> callback);

    /**
     * 获取某条新闻详情
     *
     * @param id       新闻Id
     * @param callback 回调
     */
    void getNewsDetail(String id, InfoCallback<MessageDetail> callback);

}

