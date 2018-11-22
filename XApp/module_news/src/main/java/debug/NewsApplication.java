package debug;


import com.orhanobut.logger.Logger;

import colin.com.common.base.BaseApplication;
import colin.com.common.http.DataType;
import colin.com.common.http.HttpClient;
import colin.com.common.http.OnResultListener;
import colin.com.module.news.Constants;
import colin.com.module.news.data.bean.StoryList;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name NewsApplication
 */
public class NewsApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        login();
    }


    /**
     * 在这里模拟登陆，然后拿到sessionId或者Token
     * 这样就能够在组件请求接口了
     */
    private void login() {
        HttpClient client = new HttpClient.Builder()
                .baseUrl(Constants.ZHIHU_DAILY_BEFORE_MESSAGE)
                .url("20170419")
                .bodyType(DataType.JSON_OBJECT, StoryList.class)
                .build();
        client.get(new OnResultListener<StoryList>() {

            @Override
            public void onSuccess(StoryList result) {
                Logger.e(result.toString());
            }

            @Override
            public void onError(int code, String message) {
                Logger.e(message);
            }

            @Override
            public void onFailure(String message) {
                Logger.e(message);
            }
        });
    }

}
