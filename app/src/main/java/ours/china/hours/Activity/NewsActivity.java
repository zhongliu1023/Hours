package ours.china.hours.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Adapter.NewsListAdatper;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.DB.DatabaseManager;
import ours.china.hours.Management.NewsManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
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
    DBController db;
    SharedPreferencesManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initControls();
        event();

//        getAllDataFromServer();
    }

    private void initControls() {
        // for database operation.
        db = new DBController(NewsActivity.this);
        sessionManager = new SharedPreferencesManager(NewsActivity.this);

        // for listView
        mNewsData = NewsManagement.getFoucsNews(sessionManager);

        mListView = findViewById(R.id.lstNewsContent);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        adatper = new NewsListAdatper(NewsActivity.this, mNewsData);
        mListView.setAdapter(adatper);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem cancelItem = new SwipeMenuItem(NewsActivity.this);
                cancelItem.setBackground(new ColorDrawable(getResources().getColor(R.color.lt_grey_alpha)));
                cancelItem.setWidth(200);
                cancelItem.setTitle("取消置顶");
                cancelItem.setTitleColor(getResources().getColor(R.color.white));
                cancelItem.setTitleSize(17);

                menu.addMenuItem(cancelItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(NewsActivity.this);
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                deleteItem.setWidth(200);
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
//                        Toast.makeText(NewsActivity.this, "Hello, this is cancel action", Toast.LENGTH_LONG).show();
                        mListView.smoothCloseMenu();
                        break;
                    case 1:
                        mNewsData.remove(position);
                        if (mNewsData == null || mNewsData.size() == 0) {
                            noNews.setVisibility(View.VISIBLE);
                        } else {
                            noNews.setVisibility(View.GONE);
                        }

                        db.insertNewsData(mNewsData.get(position));
                        NewsManagement.saveFoucsNews(mNewsData, sessionManager);
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

        for (NewsItem one : mNewsData) {
            db.insertNewsData(one);
        }
        NewsManagement.saveFoucsNews(mNewsData, sessionManager);

        noNews.setVisibility(View.VISIBLE);
    }

    public void getAllDataFromServer() {
        mNewsData = new ArrayList<>();

        Global.showLoading(NewsActivity.this,"generate_report");
        Ion.with(NewsActivity.this)
                .load(Url.get_notify)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {

                                    JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<NewsItem>>() {}.getType();
                                    mNewsData = gson.fromJson(dataArray.toString(), type);
                                    if (mNewsData == null || mNewsData.size() == 0) {
                                        noNews.setVisibility(View.VISIBLE);
                                    } else {
                                        noNews.setVisibility(View.GONE);
                                    }
                                    adatper.reloadNews(mNewsData);
                                } else {
                                    Toast.makeText(NewsActivity.this, "错误", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(NewsActivity.this, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
