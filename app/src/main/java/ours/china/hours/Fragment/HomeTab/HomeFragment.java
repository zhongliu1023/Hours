package ours.china.hours.Fragment.HomeTab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import ours.china.hours.Activity.AttentionActivity;
import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.NewsActivity;
import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.fragment.UIFragment;
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Interfaces.ImageListener;
import ours.china.hours.Common.Interfaces.PageLoadInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.DownloadFile;
import ours.china.hours.Management.DownloadImage;
import ours.china.hours.Management.NewsManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.NewsItem;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;
import ours.china.hours.Utility.ConnectivityHelper;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liujie on 1/12/18.
 */

public class HomeFragment extends UIFragment<FileMeta> implements BookItemInterface, BookDetailsDialog.OnDownloadBookListenner, BookItemEditInterface, PageLoadInterface {

    private String tempPopupWindow2String = "默认";

    private ArrayList<Book> mBookList = new ArrayList<Book>(){};
    ArrayList<Book> selectedBookLists;
    private HomeBookAdapter adapter;
    private RelativeLayout maskLayer;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relTypeBook;
    private ImageView imgSort;
    private ImageView imgArrow;
    private TextView txtTypeBook;
    TextView allBooks;
    private QueryBook.OrderBy orderBy = QueryBook.OrderBy.PUBLISHDATE;
    private QueryBook.Order order = QueryBook.Order.ASC;
    private QueryBook.Category category = QueryBook.Category.ALL;
//    private int currentPage = 0;

    SharedPreferencesManager sessionManager;
    DBController db = null;
    JSONObject statistics = null;

    RelativeLayout mainToolbar, otherToolbar;
    RelativeLayout relSubTitle;
    TextView txtToolbarComplete, txtToolbarDownload;

    BookDetailsDialog bookDetailsDialog;

    public ArrayList<NewsItem> mNewsData = new ArrayList<>();
    public ArrayList<NewsItem> mLocalNewsData;
    ImageView imgNewsCircle;

    int totalCount = 0;
    int allCount = 0;
    int recommendCount = 0;
    int zhirenCount = 0;
    int renwenCount = 0;
    int wenxieCount = 0;
    int numberOfAttentionBookIds = 0;
    boolean isFirstLoading;

    int first = 0;


    public HomeFragment() {
        Log.v("Hello", "This is DB controller part.");
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstLoading = true;
        getAllDataFromLocal();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);

        init(rootView);
        popupWindowWork(inflater);
        event(rootView);

        Global.showLoading(getContext(),"generate_report");
        getAllDataFromServer(0);
        getAllNews();

        return rootView;
    }



    public void init(View view) {
        // for toolbar
        mainToolbar = view.findViewById(R.id.mainToolbar);
        otherToolbar = view.findViewById(R.id.otherToolbar);
        relSubTitle = view.findViewById(R.id.relSubTitle);
        txtToolbarDownload = view.findViewById(R.id.txtToolbarDownload);
        txtToolbarComplete = view.findViewById(R.id.txtToolbarComplete);

        // for selected booklists
        selectedBookLists = new ArrayList<Book>();

        db = new DBController(getActivity());
        sessionManager = new SharedPreferencesManager(getActivity());

        // recyclerViewWork.
        RecyclerView recyclerBooksView = view.findViewById(R.id.recycler_books);
        adapter = new HomeBookAdapter(mBookList, getActivity(), this, this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerBooksView.setLayoutManager(gridLayoutManager);
        recyclerBooksView.setAdapter(adapter);

//        recyclerBooksView.setLayoutManager(gridLayoutManager);
//        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.temp_spaec);

        // popupWindowWork.
        relTypeBook = view.findViewById(R.id.relTypeBook);
        imgSort = view.findViewById(R.id.imgSort);
        txtTypeBook = view.findViewById(R.id.txtTypeBook);
        maskLayer = view.findViewById(R.id.maskLayer);

//        txtTypeBook.setText("全都");
        imgArrow = view.findViewById(R.id.imgArrow);

        //
        imgNewsCircle = view.findViewById(R.id.imgNewsCircle);
    }

    List<FileMeta> localBooks;
    public void getAllDataFromLocal() {
        localBooks = AppDB.get().getAll();
        Log.v("localBooks count", String.valueOf(localBooks.size()));
    }

    void fetchBooksStatistics(){
        Ion.with(getActivity())
                .load(Url.booksStatistics)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());
                                if (resObj.getString("res").toLowerCase().equals("success")) {
                                    statistics = new JSONObject(resObj.getString("statistics"));
                                    reloadStatistics();
                                } else {
                                    Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        removeSelectedState();
        super.onPause();
    }

    public void getAllDataFromServer(final int currentPage) {

        if (ConnectivityHelper.isConnectedToNetwork(getContext())) {
            if (currentPage == 0) {
                mBookList = new ArrayList<>();
            }

            Map<String, List<String>> params = new HashMap<String, List<String>>();
            params.put("order_by", Collections.singletonList(orderBy.toString()));
            params.put("order", Collections.singletonList(order.toString()));
            params.put("page", Collections.singletonList(Integer.toString(currentPage)));
            params.put("perPage", Collections.singletonList(Integer.toString(Global.perPage)));
            String keywords = "";
            if (!keywords.isEmpty()){
                params.put("keyword", Collections.singletonList(keywords));
            }

            if (category == QueryBook.Category.ATEENTION) {
                params.put("category", Collections.singletonList(QueryBook.Category.ALL.toString()));
            } else {
                params.put("category", Collections.singletonList(category.toString()));
            }

            Ion.with(getActivity())
                    .load(Url.query_books)
                    .setTimeout(10000)
                    .setBodyParameter(Global.KEY_token, Global.access_token)
                    .setBodyParameters(params)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception error, JsonObject result) {
                            Log.i("HomeFragment", "result => " + result);
                            Global.hideLoading();
                            swipeRefreshLayout.setRefreshing(false);

                            if (error == null) {
                                JSONObject resObj = null;
                                try {
                                    resObj = new JSONObject(result.toString());

                                    if (resObj.getString("res").toLowerCase().equals("success")) {
                                        fetchBooksStatistics();

                                        JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<ArrayList<Book>>() {}.getType();

                                        ArrayList<Book> tempBookList = gson.fromJson(dataArray.toString(), type);
                                        mBookList.addAll(tempBookList);

                                        getTotalData();
                                    } else {
                                        Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                            } else {
                                Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            swipeRefreshLayout.setEnabled(false);
            recyclerView.setEnabled(true);
        }


    }

    public void getAllNews() {
        mLocalNewsData = db.getAllNews();

//        Global.showLoading(getContext(),"generate_report");
        Ion.with(getContext())
                .load(Url.get_notify)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Log.i("HomeFragment", "result => " + result);
//                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {

                                    JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                    mNewsData.clear();

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject tempObject = dataArray.getJSONObject(i);

                                        NewsItem one = new NewsItem();
                                        one.newsId = tempObject.getString("id");
                                        one.releaseTime = tempObject.getString("releaseTime");
                                        one.title = tempObject.getString("title");
                                        one.content = tempObject.getString("content");

                                        mNewsData.add(one);
                                    }

                                    newsSaveSessionWork();

                                } else {
//                                    Toast.makeText(getContext(), "错误", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
//                            Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void newsSaveSessionWork() {
        if (mNewsData  == null || mNewsData.size() == 0) {
            imgNewsCircle.setVisibility(View.GONE);
            NewsManagement.saveFoucsNews(mNewsData, sessionManager);
            return;
        }

        ArrayList<NewsItem> tempNewsData = new ArrayList<>();

        if (mLocalNewsData != null && mLocalNewsData.size() != 0) {
            for (NewsItem serverItem : mNewsData) {
                for (NewsItem localItem : mLocalNewsData) {
                    if (serverItem.newsId.equals(localItem.newsId)) {
                        tempNewsData.add(serverItem);
                        break;
                    }

                }
            }
        }

        mNewsData.removeAll(tempNewsData);

        NewsManagement.saveFoucsNews(mNewsData, sessionManager);

    }

    public void newsReferenceUIWork() {
        mNewsData.clear();
        mNewsData = NewsManagement.getFoucsNews(sessionManager);

        if (mNewsData.size() == 0) {
            imgNewsCircle.setVisibility(View.GONE);
        } else {
            imgNewsCircle.setVisibility(View.VISIBLE);
        }
    }

    // combine with local db data and make total data.
    public void getTotalData() {
        ArrayList<Book> books = db.getAllData();
        if (books == null) {
            adapter.reloadBookList(mBookList);
            return;
        }

        for (int i = 0; i < mBookList.size(); i++) {
            for (int j = 0; j < books.size(); j++) {
                Book one = mBookList.get(i);
                Book oneLocal = books.get(j);

                if (one.bookId != null && oneLocal.bookId != null && one.bookId.equals(oneLocal.bookId)) {
                    one.bookLocalUrl = oneLocal.bookLocalUrl;
                    one.bookImageLocalUrl = oneLocal.bookImageLocalUrl;
                    one.libraryPosition = oneLocal.libraryPosition;
                    break;
                }
            }
        }

        // only in case of attention.
        if (category == QueryBook.Category.ATEENTION) {
            ArrayList<Book> tempAttentionBooks = new ArrayList<>();
            ArrayList<String> collectionIds = new ArrayList<>();
            try {
                JSONArray tempObject = new JSONArray(Global.currentUser.attentionBookIds);
                for (int i = 0; i < tempObject.length(); i++) {
                    collectionIds.add(tempObject.getString(i));
                }

                Log.i("HomeFragment", "CollectionIds => " + collectionIds.toString());

                for (Book one : mBookList) {
                    if (Global.currentUser.attentionBookIds.contains(one.bookId)) {
                        tempAttentionBooks.add(one);
                        break;
                    }
                }

                mBookList = tempAttentionBooks;
                Log.i("HomeFragment", "CollectionIds mBookList Count => " + mBookList.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter.reloadBookList(mBookList);
    }


    @Override
    public Pair<Integer, Integer> getNameAndIconRes() {
        return null;
    }

    @Override
    public void notifyFragment() {

    }

    @Override
    public void resetFragment() {

    }

    @Override
    public void onResume() {
        Log.i("HomeFragment", "onResume function");
        newsReferenceUIWork();
        super.onResume();
    }

    void reloadStatistics(){
        try {
            allCount = Integer.valueOf(statistics.getString("totalBooks"));
            totalCount = allCount;
            if (first == 0) {
                txtTypeBook.setText(getString(R.string.popup1_all, String.valueOf(totalCount)));
                first = 1;
            }

            recommendCount = Integer.valueOf(statistics.getString("recommended"));
            zhirenCount = Integer.valueOf(statistics.getString("recommended"));
            renwenCount = Integer.valueOf(statistics.getString("renwen"));
            wenxieCount = Integer.valueOf(statistics.getString("wenxie"));

            txtAllBook.setText(getString(R.string.popup1_all, String.valueOf(totalCount)));
            txtRecommended.setText(getString(R.string.popup1_recommned, String.valueOf(recommendCount)));
            txtNatural.setText(getString(R.string.popup1_natural, String.valueOf(zhirenCount)));
            txtHuman.setText(getString(R.string.popup1_human, String.valueOf(renwenCount)));
            txtLiterature.setText(getString(R.string.popup1_literature, String.valueOf(wenxieCount)));

            if (Global.currentUser.attentionBookIds.equals("")) {
                txtFollow.setText(getString(R.string.popup1_follow, String.valueOf(0)));
            } else {
                JSONArray tempAttentionBookIds = new JSONArray(Global.currentUser.attentionBookIds);
                numberOfAttentionBookIds  = tempAttentionBookIds.length();
                Log.i("HomeFragment", "tempAttenBookIds => " + numberOfAttentionBookIds);
                txtFollow.setText(getString(R.string.popup1_follow, String.valueOf(numberOfAttentionBookIds)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    PopupWindow popupWindow1;
    PopupWindow popupWindow2;
    TextView txtAllBook, txtRecommended, txtNatural, txtHuman, txtLiterature, txtFollow;
    LinearLayout linAllBooks, linRecommended, linNatural, linHuman, linLiterature;
    RelativeLayout relFollow;
    LinearLayout linDefault, linRecent;
    RelativeLayout relTitle, relAuthor;
    TextView txtDefault, txtRecent, txtTitle, txtAuthor;
    ImageView imgTitle, imgAuthor;

    public void popupWindowWork(LayoutInflater inflater) {

        popupWindowInit(inflater);

        relTypeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskLayer.setVisibility(View.VISIBLE);
                allTextPopupWindow1();
                judgePopupWindow1();
                popupWindow1.showAsDropDown(view);
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.retract_icon));
                popupWindow2.dismiss();
            }
        });
        // for popupWindow to float
        View view1 = inflater.inflate(R.layout.popup1, null);
        allBooks = view1.findViewById(R.id.txtAllBooks);
//        allBooks.setText("全都（" + 1000 + ")");
        final PopupWindow popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        relTypeBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow1.showAsDropDown(view);
//            }
//        });

        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTextImagePopupWindow2();
                judgePopupWindow2();
                popupWindow2.showAsDropDown(view);
                maskLayer.setVisibility(View.VISIBLE);
                popupWindow1.dismiss();
            }
        });

        // for event when we pressed item in popupWindow.
        linAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    txtTypeBook.setText(getString(R.string.popup1_all, statistics.getString("totalBooks")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                category= QueryBook.Category.ALL;

                totalCount = allCount;
                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                dismissPopupWindow1();
            }
        });

        linRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    txtTypeBook.setText(getString(R.string.popup1_recommned, statistics.getString("recommended")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                category= QueryBook.Category.RECOMMEND;

                totalCount = recommendCount;
                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                dismissPopupWindow1();
            }
        });

        linNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    txtTypeBook.setText(getString(R.string.popup1_natural, statistics.getString("zhiren")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                category= QueryBook.Category.ZHIREN;

                totalCount = zhirenCount;
                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                dismissPopupWindow1();
            }
        });

        linHuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    txtTypeBook.setText(getString(R.string.popup1_human, statistics.getString("renwen")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                category= QueryBook.Category.RENWEN;

                totalCount = renwenCount;
                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                dismissPopupWindow1();
            }
        });

        linLiterature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    txtTypeBook.setText(getString(R.string.popup1_literature, statistics.getString("wenxie")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                category= QueryBook.Category.WENXIE;

                totalCount = wenxieCount;
                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                dismissPopupWindow1();
            }
        });

        relFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                txtTypeBook.setText(getString(R.string.popup1_follow, String.valueOf(numberOfAttentionBookIds)));
//
//                maskLayer.setVisibility(View.GONE);
//                category = QueryBook.Category.ATEENTION;
//
//                Global.showLoading(getContext(),"generate_report");
//                getAllDataFromServer(0);
//                dismissPopupWindow1();

                Intent intentFav = new Intent(getContext(), AttentionActivity.class);
                startActivity(intentFav);
            }
        });

        linDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "默认";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.PUBLISHDATE;

                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                popupWindow2.dismiss();
            }
        });

        linRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "最近";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.PUBLISHDATE;

                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                popupWindow2.dismiss();
            }
        });

        relTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "标题";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.BOOKNAME;
                order = QueryBook.Order.ASC;

                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                popupWindow2.dismiss();
            }
        });

        relAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "作者";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.AUTH0R;
                order = QueryBook.Order.DESC;

                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(0);
                popupWindow2.dismiss();
            }
        });

        maskLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopupWindow1();
                popupWindow2.dismiss();
            }
        });

    }

    public void dismissPopupWindow1() {
        popupWindow1.dismiss();
        maskLayer.setVisibility(View.GONE);
        imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.pulldown_icon));
    }

    public void allTextPopupWindow1() {
        txtAllBook.setTextColor(getResources().getColor(R.color.alpa_90));
        txtRecommended.setTextColor(getResources().getColor(R.color.alpa_90));
        txtNatural.setTextColor(getResources().getColor(R.color.alpa_90));
        txtHuman.setTextColor(getResources().getColor(R.color.alpa_90));
        txtLiterature.setTextColor(getResources().getColor(R.color.alpa_90));
        txtFollow.setTextColor(getResources().getColor(R.color.alpa_90));
    }

    public void allTextImagePopupWindow2() {
        txtDefault.setTextColor(getResources().getColor(R.color.alpa_90));
        txtRecent.setTextColor(getResources().getColor(R.color.alpa_90));
        txtTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtAuthor.setTextColor(getResources().getColor(R.color.alpa_90));

        imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.reverse_order_icon)));
    }

    public void popupWindowInit(LayoutInflater inflater) {

        // for popupWindow 1
        View view1 = inflater.inflate(R.layout.popup1, null);

        txtAllBook = view1.findViewById(R.id.txtAllBooks);
        txtRecommended = view1.findViewById(R.id.txtRecommended);
        txtNatural = view1.findViewById(R.id.txtNatural);
        txtHuman = view1.findViewById(R.id.txtHuman);
        txtLiterature = view1.findViewById(R.id.txtLiterature);
        txtFollow = view1.findViewById(R.id.txtFollow);

        linAllBooks = view1.findViewById(R.id.linAllBooks);
        linRecommended = view1.findViewById(R.id.linRecommended);
        linNatural = view1.findViewById(R.id.linNatural);
        linHuman = view1.findViewById(R.id.linHuman);
        linLiterature = view1.findViewById(R.id.linLiterature);
        relFollow = view1.findViewById(R.id.relFollow);

        popupWindow1 = new PopupWindow(view1, (int) getResources().getDimension(R.dimen.popup_window_width), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow1.setAnimationStyle(R.style.popupwindowAnimation);

        // for popupWindow 2
        View view2 = inflater.inflate(R.layout.popup2, null);

        linDefault = view2.findViewById(R.id.linDefault);
        linRecent = view2.findViewById(R.id.linRecent);
        relTitle = view2.findViewById(R.id.relTitle);
        relAuthor = view2.findViewById(R.id.relAuthor);

        txtDefault = view2.findViewById(R.id.txtDefault);
        txtRecent = view2.findViewById(R.id.txtRecent);
        txtTitle = view2.findViewById(R.id.txtTitle);
        txtAuthor = view2.findViewById(R.id.txtAuthor);

        imgTitle = view2.findViewById(R.id.imgTitle);
        imgAuthor = view2.findViewById(R.id.imgAuthor);

        popupWindow2 = new PopupWindow(view2, (int) getResources().getDimension(R.dimen.popup_window_width), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow2.setAnimationStyle(R.style.popupwindowAnimation);
    }

    public void judgePopupWindow1() {

        String tempStr = txtTypeBook.getText().toString();
        if (tempStr.contains("全都")) {
            allTextPopupWindow1();
            txtAllBook.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("推荐")) {
            allTextPopupWindow1();
            txtRecommended.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("自然科学")) {
            allTextPopupWindow1();
            txtNatural.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("人文社科")) {
            allTextPopupWindow1();
            txtHuman.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("文学艺术")) {
            allTextPopupWindow1();
            txtLiterature.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("关注")) {
            allTextPopupWindow1();
            txtFollow.setTextColor(getResources().getColor(R.color.pink));
        }
    }

    public void judgePopupWindow2() {
        if (tempPopupWindow2String.equals("默认")) {
            allTextImagePopupWindow2();
            txtDefault.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempPopupWindow2String.equals("最近")) {
            allTextImagePopupWindow2();
            txtRecent.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempPopupWindow2String.equals("标题")) {
            allTextImagePopupWindow2();
            txtTitle.setTextColor(getResources().getColor(R.color.pink));
            imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.pink), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        } else if (tempPopupWindow2String.equals("作者")) {
            allTextImagePopupWindow2();
            txtAuthor.setTextColor(getResources().getColor(R.color.pink));
            imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.pink), getResources().getDrawable(R.drawable.reverse_order_icon)));
        }
    }

    public void event(View rootView) {

        RelativeLayout searchLayout = rootView.findViewById(R.id.searchLayout);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllDataFromServer(0);
            }
        });

        txtToolbarComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedState();
            }
        });

        txtToolbarDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Book> tempArrayList = new ArrayList<>();

                for (Book one : selectedBookLists) {
                    if (!one.bookImageLocalUrl.equals("") && !one.bookLocalUrl.equals("")) {
                        continue;
                    } else {
                        tempArrayList.add(one);
                    }
                }
            }
        });

        ImageView newsImage = rootView.findViewById(R.id.imageNews);
        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                startActivity(intent);
            }
        });

        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.pulldown_icon));
                maskLayer.setVisibility(View.GONE);
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.pulldown_icon));
                maskLayer.setVisibility(View.GONE);
            }
        });
    }

    public void removeSelectedState() {

        mainToolbar.setVisibility(View.VISIBLE);
        otherToolbar.setVisibility(View.GONE);
        relSubTitle.setVisibility(View.VISIBLE);

        selectedBookLists.clear();
        adapter.reloadBookList(mBookList);

        swipeRefreshLayout.setEnabled(true);
        Global.bookAction = QueryBook.BookAction.NONE;
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }

    @Override
    public void onClickBookItem(Book selectedBook, int position) {

        if (Global.bookAction == QueryBook.BookAction.SELECTTION){
            if (selectedBookLists.contains(selectedBook)){
                selectedBookLists.remove(selectedBook);
            }else{
                selectedBookLists.add(selectedBook);
            }
            adapter.reloadBookListWithSelection(selectedBookLists);
            toolbarWork();

        } else {
            if (!selectedBook.bookLocalUrl.equals("") && !selectedBook.bookImageLocalUrl.equals("")) {
                BookManagement.saveFocuseBook(selectedBook, sessionManager);
                gotoReadingViewFile();
                return;
            }
            BookManagement.saveFocuseBook(selectedBook, sessionManager);

            if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                bookDetailsDialog = new BookDetailsDialog(getActivity(), HomeFragment.this);
                bookDetailsDialog.show();

                // set needed frame of dialog. Without this code, all the component of the dialog's layout don't have original size.
                Window rootWindow = getActivity().getWindow();
                Rect displayRect = new Rect();
                rootWindow.getDecorView().getWindowVisibleDisplayFrame(displayRect);
                bookDetailsDialog.getWindow().setLayout(displayRect.width(), displayRect.height());
                bookDetailsDialog.setCanceledOnTouchOutside(false);
            }else{
                EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), 300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

    }

    void gotoReadingViewFile(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);
        List<FileMeta> localBooks = AppDB.get().getAll();
        int tempLibraryPosition = Integer.parseInt(focuseBook.libraryPosition);

        ExtUtils.openFile(getActivity(), localBooks.get(tempLibraryPosition));
    }

    @Override
    public void onFinishDownload(Book book, Boolean isSuccess) {
        if (isSuccess){
            for (int i = 0; i < mBookList.size(); i++) {
                Book one = mBookList.get(i);
                if (one.bookId != null && book.bookId != null && one.bookId.equals(book.bookId)) {
                    one.bookLocalUrl = book.bookLocalUrl;
                    one.bookImageLocalUrl = book.bookImageLocalUrl;
                    one.libraryPosition = book.libraryPosition;
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
            gotoReadingViewFile();
        }
    }

    @Override
    public void onLongClickBookItem(Book selectedBook) {
        Global.bookAction = QueryBook.BookAction.SELECTTION;

        selectedBookLists.clear();
        selectedBookLists.add(selectedBook);

        swipeRefreshLayout.setEnabled(false);
        adapter.reloadBookListWithSelection(selectedBookLists);

        toolbarWork();

        relSubTitle.setVisibility(View.GONE);
        mainToolbar.setVisibility(View.GONE);
        otherToolbar.setVisibility(View.VISIBLE);
    }

    public void toolbarWork() {
        if (isExistUnDownloadedBook()) {
            txtToolbarDownload.setEnabled(true);
            txtToolbarDownload.setTextColor(getResources().getColor(R.color.alpa_90));
        } else {
            txtToolbarDownload.setEnabled(false);
            txtToolbarDownload.setTextColor(getResources().getColor(R.color.alpa_40));
        }
    }

    public boolean isExistUnDownloadedBook() {
        if (selectedBookLists.size() == 0) {
            return false;
        }

        for (Book one : selectedBookLists) {
            if (one.bookLocalUrl.equals("") && one.bookImageLocalUrl.equals("")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void scrollToLoad(int position) {
        if (Global.bookAction == QueryBook.BookAction.NONE) {

            if (position < totalCount) {

                Global.showLoading(getContext(),"generate_report");
                getAllDataFromServer(position / Global.perPage);
            }
        }


    }
}