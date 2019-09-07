package ours.china.hours.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import ours.china.hours.Adapter.NewsListAdatper;
import ours.china.hours.Model.NewsItem;
import ours.china.hours.R;
import ours.china.hours.Utility.AlertDelete;

public class NewsActivity extends AppCompatActivity implements AlertDelete.deleteButtonListener {

    SwipeMenuListView mListView;
    TextView txtEmptyNews;
    ImageView imgBack;
    LinearLayout noNews;
    private ArrayList<NewsItem> mNewsData = new ArrayList<>();
    private NewsListAdatper adatper;

    AlertDelete alert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initControls();
        event();
    }

    private void initControls() {
        // for listView
        mListView = findViewById(R.id.lstNewsContent);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        mNewsData.add(new NewsItem("系统通知", "2019-09-05 15:30", "what are you going to do?"));
        mNewsData.add(new NewsItem("系统通知", "2019-09-05 15:30", "what are you going to do?"));
        mNewsData.add(new NewsItem("系统通知", "2019-09-05 15:30", "what are you going to do?"));
        mNewsData.add(new NewsItem("系统通知", "2019-09-05 15:30", "what are you going to do?"));

        adatper = new NewsListAdatper(NewsActivity.this, mNewsData);
        mListView.setAdapter(adatper);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem cancelItem = new SwipeMenuItem(NewsActivity.this);
                cancelItem.setBackground(new ColorDrawable(getResources().getColor(R.color.lt_grey_alpha)));
                cancelItem.setWidth(120);
                cancelItem.setTitle("取消置顶");
                cancelItem.setTitleColor(getResources().getColor(R.color.white));
                cancelItem.setTitleSize(17);

                menu.addMenuItem(cancelItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(NewsActivity.this);
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                deleteItem.setWidth(120);
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                deleteItem.setTitleSize(17);

                menu.addMenuItem(deleteItem);
            }
        };

        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Toast.makeText(NewsActivity.this, "Hello, this is cancel action", Toast.LENGTH_LONG).show();
                        mListView.smoothCloseMenu();
                        break;
                    case 1:
                        mNewsData.remove(position);
                        adatper.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        mListView.setCloseInterpolator(new BounceInterpolator());
        mListView.setOpenInterpolator(new AccelerateDecelerateInterpolator());

        // for alert
        alert = new AlertDelete(NewsActivity.this, R.style.AppTheme_Alert, "确定要清空所有消息吗？");
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // for no News
        noNews = findViewById(R.id.noNews);
        if (mNewsData == null || mNewsData.size() == 0) {
            noNews.setVisibility(View.VISIBLE);
        } else {
            noNews.setVisibility(View.GONE);
        }
    }

    public void event() {
        txtEmptyNews = findViewById(R.id.txtEmpty);
        txtEmptyNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    NewsActivity.this.finish();
                } else {
                    NewsActivity.super.onBackPressed();
                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onDelete() {
        mNewsData.clear();
        adatper.notifyDataSetChanged();

        noNews.setVisibility(View.VISIBLE);
    }
}
