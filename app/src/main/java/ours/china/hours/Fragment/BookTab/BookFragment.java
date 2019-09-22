package ours.china.hours.Fragment.BookTab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.FavoritesActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.BooksService;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.ItemOffsetDecoration;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.BookStatus;
import ours.china.hours.Model.Favorites;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;

/**
 * Created by liujie on 1/12/18.
 */

public class BookFragment extends Fragment implements BookItemInterface {

    String tempPopupWindow2String = "默认";

    ArrayList<Book> localBookList;
    ArrayList<Book> mBookList;
    ArrayList<Book> searchedBookList;
    ArrayList<Favorites> mFavorites;
    HomeBookAdapter adapter;
    RelativeLayout maskLayer;

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerBooksView;
    private RelativeLayout relTypeBook;
    ImageView imgSort;
    ImageView imgSearch;
    ImageView imgArrow;
    TextView txtTypeBook;

    private QueryBook.OrderBy orderBy = QueryBook.OrderBy.PUBLISHDATE;
    private QueryBook.Order order = QueryBook.Order.ASC;
    private QueryBook.Category category = QueryBook.Category.ALL;
    private int currentPage = 0;

    SharedPreferencesManager sessionManager;
    DBController db = null;

    JSONObject statistics = null;
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAllDataFromLocalDB();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_tab, container, false);

        init(rootView);
        popupWindowWork(inflater);
        event(rootView);
        getAllDataFromServer();

        return rootView;
    }

    public void init(View view) {

        sessionManager = new SharedPreferencesManager(getActivity());
        // recyclerViewWork.
        recyclerBooksView = view.findViewById(R.id.recycler_books);

        adapter = new HomeBookAdapter(mBookList, getActivity(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerBooksView.setLayoutManager(gridLayoutManager);
        recyclerBooksView.setAdapter(adapter);

        // popupWindowWork.
        relTypeBook = view.findViewById(R.id.relTypeBook);
        imgSort = view.findViewById(R.id.imgSort);
        txtTypeBook = view.findViewById(R.id.txtTypeBook);
        maskLayer = view.findViewById(R.id.maskLayer);

        txtTypeBook.setText(getString(R.string.popup3_all, "90"));
        imgArrow = view.findViewById(R.id.imgArrow);
        mFavorites = new ArrayList<Favorites>(){};
    }

    public void getAllDataFromLocalDB() {
        db = new DBController(getActivity());
        localBookList = new ArrayList<Book>();
        localBookList = db.getAllData();

    }
    void fetchBooksStatistics(){
        Ion.with(getActivity())
                .load(Url.mybooksStatistics)
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
    public void getAllDataFromServer() {
        fetchBooksStatistics();
        mBookList = new ArrayList<>();
        searchedBookList = new ArrayList<>();

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("order_by", Collections.singletonList(orderBy.toString()));
        params.put("order", Collections.singletonList(order.toString()));
        params.put("page", Collections.singletonList(Integer.toString(currentPage)));
        params.put("statusOnly", Collections.singletonList("0"));
        String keywords = "";
        if (!keywords.isEmpty()){
            params.put("keyword", Collections.singletonList(keywords));
        }
        params.put("category", Collections.singletonList(category.toString()));

        Ion.with(getActivity())
                .load(Url.searchMyBookwithMobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameters(params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        Log.i("HomeFragment", "result => " + result);
                        Global.hideLoading();

                        if (error == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").toLowerCase().equals("success")) {

                                    JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                    String favorite = resObj.getString("collections");
                                    BookManagement.saveFullFavorites(favorite, sessionManager);
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<Book>>() {}.getType();
                                    mBookList = gson.fromJson(dataArray.toString(), type);

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject oneObject = dataArray.getJSONObject(i);
                                        BookStatus bookStatus = new BookStatus();

                                        bookStatus.pages = oneObject.getString("pages");
                                        bookStatus.time = oneObject.getString("time");
                                        bookStatus.progress = oneObject.getString("progress");
                                        bookStatus.lastRead = oneObject.getString("lastRead");
                                        bookStatus.isRead = oneObject.getString("isRead");
                                        bookStatus.isAttention = oneObject.getString("isAttention");
                                        bookStatus.collection = oneObject.getString("collection");
                                        bookStatus.notes = oneObject.getString("notes");
                                        bookStatus.bookmarks = oneObject.getString("bookmarks");
                                        mBookList.get(i).bookStatus = bookStatus;

//                                        if (db.getBookData(mBookList.get(i).bookId) == null){
//                                            db.insertData(mBookList.get(i));
//                                        }else{
////                                            db.updateBookData(mBookList.get(i));
//                                        }
                                        BookStatus localBookStatus= db.getBookStateData(mBookList.get(i).bookId);
                                        if (localBookStatus == null){
                                            db.insertBookStateData(bookStatus, mBookList.get(i).bookId);
                                        }else{
                                            if (Long.parseLong(localBookStatus.time) < Long.parseLong(bookStatus.time)){
                                                db.updateBookStateData(bookStatus, mBookList.get(i).bookId);
                                            }
                                        }
                                    }

                                    searchedBookList = (ArrayList<Book>) mBookList.clone();
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
    }
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
        adapter.reloadBookList(mBookList);

        txtTypeBook.setText(getString(R.string.popup3_all, Integer.toString(mBookList.size())));

        txtAllBooks.setText(getString(R.string.popup3_all, Integer.toString(mBookList.size())));

        int downloads=0, reads=0, unreads = 0;
        for (Book oneBook : mBookList){
            if (!oneBook.bookLocalUrl.isEmpty()){
                downloads++;
            }
            if (oneBook.bookStatus != null && oneBook.bookStatus.isRead.equals("1")){
                reads++;
            }else{
                unreads++;
            }
        }
        txtDownloaded.setText(getString(R.string.popup3_downloaded, Integer.toString(downloads)));
        txtRead.setText(getString(R.string.popup3_read, Integer.toString(reads)));
        txtUnread.setText(getString(R.string.popup3_unread, Integer.toString(unreads)));

        mFavorites.clear();
        String[] collections = Global.fullFavorites.split(",");
        for (String aCollection : collections){
            Favorites favorites = new Favorites();
            favorites.favorite = aCollection;
            for (Book book : mBookList){
                if (book.bookStatus == null || book.bookStatus.collection.isEmpty()) continue;
                if (book.bookStatus.collection.contains(aCollection)){
                    favorites.bookList.add(book);
                }
            }
            mFavorites.add(favorites);
        }

        txtFavorites.setText(getString(R.string.popup3_favorites, Integer.toString(mFavorites.size())));
        BookManagement.saveFavorites(mFavorites, sessionManager);
    }

    @Override
    public void onResume() {
        super.onResume();

//        this.mBookList = db.getAllData();
//        adapter.notifyDataSetChanged();
    }


    void reloadStatistics(){

    }
    PopupWindow popupWindow1;
    PopupWindow popupWindow2;
    TextView txtAllBooks, txtRead, txtUnread, txtDownloaded, txtFavorites;
    LinearLayout linAllBooks, linRead, linUnread, linDownloaded, linFavorites;
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
                txtTypeBook.setText("全都(1000)");
                searchedBookList = mBookList;
                adapter.reloadBookList(searchedBookList);
                dismissPopupWindow1();
            }
        });

        linDownloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("已下载");
                searchedBookList.clear();
                for (Book oneBook : mBookList){
                    if (!oneBook.bookLocalUrl.isEmpty()){
                        searchedBookList.add(oneBook);
                    }
                }
                adapter.reloadBookList(searchedBookList);
                dismissPopupWindow1();
            }
        });

        linRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("已读");
                searchedBookList.clear();
                for (Book oneBook : mBookList){
                    if (oneBook.bookStatus != null && oneBook.bookStatus.isRead.equals("1")){
                        searchedBookList.add(oneBook);
                    }
                }
                adapter.reloadBookList(searchedBookList);
                dismissPopupWindow1();
            }
        });

        linUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchedBookList.clear();
                for (Book oneBook : mBookList){
                    if (oneBook.bookStatus == null || !oneBook.bookStatus.isRead.equals("1")){
                        searchedBookList.add(oneBook);
                    }
                }
                adapter.reloadBookList(searchedBookList);
                dismissPopupWindow1();
            }
        });


        linFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("收藏夹");
                maskLayer.setVisibility(View.GONE);
                dismissPopupWindow1();

                Intent intent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(intent);
            }
        });


        linRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "最近";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.PUBLISHDATE;
                getAllDataFromServer();
                popupWindow2.dismiss();

            }
        });

        relTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "标题";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.BOOKNAME;
                getAllDataFromServer();
                popupWindow2.dismiss();
            }
        });

        relAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "作者";
                maskLayer.setVisibility(View.GONE);
                orderBy= QueryBook.OrderBy.AUTH0R;
                getAllDataFromServer();
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
        txtAllBooks.setTextColor(getResources().getColor(R.color.alpa_90));
        txtDownloaded.setTextColor(getResources().getColor(R.color.alpa_90));
        txtRead.setTextColor(getResources().getColor(R.color.alpa_90));
        txtUnread.setTextColor(getResources().getColor(R.color.alpa_90));
        txtFavorites.setTextColor(getResources().getColor(R.color.alpa_90));
    }

    public void allTextImagePopupWindow2() {
        txtRecent.setTextColor(getResources().getColor(R.color.alpa_90));
        txtTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtAuthor.setTextColor(getResources().getColor(R.color.alpa_90));

        imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.reverse_order_icon)));
    }

    public void popupWindowInit(LayoutInflater inflater) {

        // for popupWindow 1
        View view1 = inflater.inflate(R.layout.popup3, null);

        txtAllBooks = view1.findViewById(R.id.txtAllBooks);
        txtDownloaded = view1.findViewById(R.id.txtDownloaded);
        txtRead = view1.findViewById(R.id.txtRead);
        txtUnread = view1.findViewById(R.id.txtUnread);
        txtFavorites = view1.findViewById(R.id.txtFavorites);

        linAllBooks = view1.findViewById(R.id.linAllBooks);
        linDownloaded = view1.findViewById(R.id.linDownloaded);
        linRead = view1.findViewById(R.id.linRead);
        linUnread = view1.findViewById(R.id.linUnread);
        linFavorites = view1.findViewById(R.id.linFavorites);

        popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow1.setAnimationStyle(R.style.popupwindowAnimation);

        // for popupWindow 2
        View view2 = inflater.inflate(R.layout.popup4, null);

        linRecent = view2.findViewById(R.id.linRecent);
        relTitle = view2.findViewById(R.id.relTitle);
        relAuthor = view2.findViewById(R.id.relAuthor);

        txtRecent = view2.findViewById(R.id.txtRecent);
        txtTitle = view2.findViewById(R.id.txtTitle);
        txtAuthor = view2.findViewById(R.id.txtAuthor);

        imgTitle = view2.findViewById(R.id.imgTitle);
        imgAuthor = view2.findViewById(R.id.imgAuthor);

        popupWindow2 = new PopupWindow(view2, 180, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow2.setAnimationStyle(R.style.popupwindowAnimation);
    }

    public void judgePopupWindow1() {

        String tempStr = txtTypeBook.getText().toString();
        if (tempStr.contains("全都")) {
            allTextPopupWindow1();
            txtAllBooks.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("已下载")) {
            allTextPopupWindow1();
            txtDownloaded.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("已读")) {
            allTextPopupWindow1();
            txtRead.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("未读")) {
            allTextPopupWindow1();
            txtUnread.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("收藏夹")) {
            allTextPopupWindow1();
            txtFavorites.setTextColor(getResources().getColor(R.color.pink));
        }
    }

    public void judgePopupWindow2() {
        if (tempPopupWindow2String.equals("最近")) {
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
        imgSearch = rootView.findViewById(R.id.search_image);
        imgSearch.setOnClickListener(new View.OnClickListener() {
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }

    @Override
    public void onClickBookItem(Book selectedBook, int position) {
        if (!selectedBook.bookLocalUrl.equals("") && !selectedBook.bookImageLocalUrl.equals("")) {
            BookManagement.saveFocuseBook(selectedBook, sessionManager);
            gotoReadingViewFile();
        }
    }
    void gotoReadingViewFile(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);
        List<FileMeta> localBooks = AppDB.get().getAll();
        int tempLibraryPosition = Integer.parseInt(focuseBook.libraryPosition);

        ExtUtils.openFile(getActivity(), localBooks.get(tempLibraryPosition));
    }
}
