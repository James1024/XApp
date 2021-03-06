package colin.com.module.news.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import colin.com.common.base.BaseActivity;
import colin.com.module.news.R;

/**
 * <p>类说明</p>
 *
 * @author yiche
 * @name NewsCenterActivity
 */
@Route(path = "/news/center")
public class NewsCenterActivity extends BaseActivity {

    protected Toolbar mToolBar;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    private NewsListViewAdapter mListAdapter;



    @Override
    public int getLayout() {
        return R.layout.activity_news;
    }

    @Override
    public void initView() {
        mToolBar = (Toolbar) findViewById(R.id.news_title_bar);
        mToolBar.setTitle("知乎日报");
        setupToolBar(mToolBar, false);
        mTabLayout = (TabLayout) findViewById(R.id.date_tab);
        mViewPager = (ViewPager) findViewById(R.id.message_pager);
        mListAdapter = new NewsListViewAdapter(getMessageListViews(), getWeekDate());
        mViewPager.setAdapter(mListAdapter);
        //setupWithViewPager必须在ViewPager.setAdapter()之后调用
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 获取ViewPager的viewList
     */
    private List<NewsListView> getMessageListViews() {
        List<NewsListView> viewList = new ArrayList<>();
        List<String> weekDate = getWeekDate();
        if (weekDate != null) {
            for (String tab : weekDate) {
                viewList.add(new NewsListView(this, tab));
            }
        }
        return viewList;
    }


    /**
     * 获取过去7天的时期，格式为yymmdd
     **/
    public static List<String> getWeekDate() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1 - i);
            dates.add(simpleDateFormat.format(calendar.getTime()));
        }
        return dates;
    }


}
