package colin.com.module.news.detail;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import colin.com.common.base.BaseActivity;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name NewsDetailActivity
 */
@Route(path = "/news/detail")
public class NewsDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsDetailView detailView = new NewsDetailView(this);
        setContentView(detailView);
        String id = getIntent().getStringExtra("id");
        new NewsDetailPresenter(detailView).getNewsDetail(id);
    }

    @Override
    public int getLayout() {
        return -1;
    }

    @Override
    public void initView() {

    }

}
