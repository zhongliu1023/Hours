package ours.china.hours.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ours.china.hours.Adapter.FavoritesDetailsAdatper;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalBookReadingActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.NewsManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.FavoriteDetailBook;
import ours.china.hours.Model.Favorites;
import ours.china.hours.Model.NewsItem;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;
import ours.china.hours.Utility.AlertDelete;
import ours.china.hours.Utility.ConnectivityHelper;

public class FavoritesDetailActivity extends AppCompatActivity implements FavoritesDetailsAdatper.addActionListener, AlertDelete.deleteButtonListener {
    private final String TAG = "FavoritesDetailActivity";

    RecyclerView recyclerFavoritesDetailView;
    TextView txtFavoriteDetailTitle;
    TextView txtEdit;
    LinearLayout linFooter;
    RelativeLayout relTitle;
    ImageView imgBack, imgSort, imgPlus;

    RelativeLayout mainToolbar, otherToolbar, myBookShelfToolbar;
    RelativeLayout relSearchView;
    TextView txtComplete, txtDelete, txtAddFavorite, txtDownload;

    TextView txtCancel, txtFooterAddFavorite;

    SearchView favorSearchView;
    FavoritesDetailsAdatper adatper;
    ArrayList<Book> mBookList;
    ArrayList<Book> bookListFromLocal;
    ArrayList<Book> selectedBookLists;
    ArrayList<Book> searchedFavoritesList;
    ArrayList<Favorites> selectedFavorites;

    String favorite = "";

    AlertDelete alertDelete;
    SharedPreferencesManager sessionManager;
    DBController db = null;

    public ArrayList<NewsItem> mNewsData = new ArrayList<>();
    public ArrayList<NewsItem> mLocalNewsData;

    ImageView imgNewsCircle;
    int deleteCount = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);

        init();
        event();
        getAllNews();
    }

    public void init() {

        favorite = getIntent().getStringExtra("favoriteType");
        sessionManager = new SharedPreferencesManager(this);
        db = new DBController(FavoritesDetailActivity.this);

        recyclerFavoritesDetailView = findViewById(R.id.recyclerFavoritesDetail);
        mBookList = new ArrayList<Book>();
        selectedBookLists = new ArrayList<Book>();
        bookListFromLocal = new ArrayList<Book>();
        searchedFavoritesList = new ArrayList<Book>();
        selectedFavorites = BookManagement.getFavorites(sessionManager);
        for (Favorites favorites : selectedFavorites){
            if (favorites.favorite.equals(favorite)){
                mBookList = (ArrayList<Book>)favorites.bookList.clone();
                selectedFavorites.remove(favorites);
                break;
            }
        }

        ArrayList<Book> tmpBookList = db.getAllData();
        for (Book book : tmpBookList){
            if (book.bookStatus != null && !book.bookStatus.collection.contains(favorite)){
                bookListFromLocal.add(book);
            }
        }

        adatper = new FavoritesDetailsAdatper(mBookList, FavoritesDetailActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FavoritesDetailActivity.this, 3);
        recyclerFavoritesDetailView.setLayoutManager(gridLayoutManager);
        recyclerFavoritesDetailView.setAdapter(adatper);

        // for title setting
        txtFavoriteDetailTitle = findViewById(R.id.favoriteDetailTitle);
        txtFavoriteDetailTitle.setText(getIntent().getStringExtra("favoriteType"));

        // other init
        linFooter = findViewById(R.id.footer);
        relTitle = findViewById(R.id.relTitle);
        imgBack = findViewById(R.id.imgBack);
//        imgSort = findViewById(R.id.imgSort);
        imgPlus = findViewById(R.id.imgPlus);

        // for toolbar
        mainToolbar = findViewById(R.id.mainToolbars);
        otherToolbar = findViewById(R.id.otherToolbar);
        txtComplete = findViewById(R.id.txtComplete);
        txtDelete = findViewById(R.id.txtDelete);
//        txtAddFavorite = findViewById(R.id.txtAddFavorite);
        txtDownload = findViewById(R.id.txtDownload);
        txtCancel = findViewById(R.id.txtCancel);
        txtFooterAddFavorite = findViewById(R.id.txtFooterAddFavorite);
        myBookShelfToolbar = findViewById(R.id.myBookShelfToolbar);

        // for alert
        alertDelete = new AlertDelete(FavoritesDetailActivity.this, R.style.AppTheme_Alert, "确定要收藏夹吗？");
        alertDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        favorSearchView = findViewById(R.id.favorSearchView);
        favorSearchView.setQueryHint("搜索书名、作者、出版社");

        // for news
        imgNewsCircle = findViewById(R.id.imgNewsCircle);

        // for searchView
        relSearchView = findViewById(R.id.relSearchView);
    }

    public void event() {
        // for news
        ImageView newsImage = findViewById(R.id.imageNews);
        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavoritesDetailActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        // for search view
        relSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorSearchView.onActionViewExpanded();
            }
        });

        txtEdit = findViewById(R.id.txtEdit);
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // general UI operation
                linFooter.setVisibility(View.GONE);

                imgBack.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_40), getResources().getDrawable(R.drawable.return_icon)));
//                imgSort.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_40), getResources().getDrawable(R.drawable.sort_icon)));
                txtFavoriteDetailTitle.setTextColor(getResources().getColor(R.color.alpa_40));
                txtEdit.setTextColor(getResources().getColor(R.color.alpa_40));

                // make title bar component inactive
                imgBack.setEnabled(false);
//                imgSort.setEnabled(false);
                txtEdit.setEnabled(false);

                mainToolbar.setVisibility(View.GONE);
                otherToolbar.setVisibility(View.VISIBLE);

                txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
//                txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_40));
                txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

                // toolbar's primary state
                txtDelete.setEnabled(false);
//                txtAddFavorite.setEnabled(false);
                txtDownload.setEnabled(false);

                // for select item
                Global.editStateOfFavoritesDetails = "yes";
                Global.bookAction = QueryBook.BookAction.SELECTTION;
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Favorites one = new Favorites();
                one.favorite = favorite;
                one.bookList = mBookList;
                selectedFavorites.add(one);
                BookManagement.saveFavorites(selectedFavorites, sessionManager);

                Intent intent = new Intent(FavoritesDetailActivity.this, FavoritesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDelete.show();
            }
        });

//        txtAddFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        txtComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSelectedState();
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Global.bookAction = QueryBook.BookAction.SELECTTION;
                // for visible process
                myBookShelfToolbar.setVisibility(View.VISIBLE);
                mainToolbar.setVisibility(View.GONE);
                otherToolbar.setVisibility(View.GONE);
                relTitle.setVisibility(View.GONE);

                txtFooterAddFavorite.setVisibility(View.VISIBLE);
                imgPlus.setVisibility(View.GONE);

                // for enable select item
                Global.editStateOfFavoritesDetails = "yes";
                ArrayList<Book> tmpBookList = db.getAllData();
                bookListFromLocal.clear();
                for (Book book : tmpBookList){
                    if (book.bookStatus != null && !book.bookStatus.collection.contains(favorite)){
                        bookListFromLocal.add(book);
                    }
                }

                adatper.reloadBookList(bookListFromLocal);
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainToolbar.setVisibility(View.VISIBLE);
                otherToolbar.setVisibility(View.GONE);
                myBookShelfToolbar.setVisibility(View.GONE);
                relTitle.setVisibility(View.VISIBLE);

                txtFooterAddFavorite.setVisibility(View.GONE);
                imgPlus.setVisibility(View.VISIBLE);
                adatper.reloadBookList(mBookList);
            }
        });

        txtFooterAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // for visible process
                mainToolbar.setVisibility(View.VISIBLE);
                otherToolbar.setVisibility(View.GONE);
                myBookShelfToolbar.setVisibility(View.GONE);
                relTitle.setVisibility(View.VISIBLE);

                txtFooterAddFavorite.setVisibility(View.GONE);
                imgPlus.setVisibility(View.VISIBLE);

                Global.bookAction = QueryBook.BookAction.NONE;
                mBookList.addAll(selectedBookLists);
                for (Book book : selectedBookLists){
                    book.bookStatus.collection += ","+favorite;
                    db.updateBookStateData(book.bookStatus, book.bookId);
                    Ion.with(FavoritesDetailActivity.this)
                            .load(Url.bookStateChangeOperation)
                            .setTimeout(10000)
                            .setBodyParameter("access_token", Global.access_token)
                            .setBodyParameter("bookId", book.bookId)
                            .setBodyParameter("collection", book.bookStatus.collection)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null) {
                                    }
                                }
                            });
                }

                ArrayList<Favorites> selectedFavorites = BookManagement.getFavorites(sessionManager);
                for (Favorites favorites: selectedFavorites){
                    if (favorites.favorite.equals(favorite)){
                        favorites.bookList = mBookList;
                        break;
                    }
                }
                BookManagement.saveFavorites(selectedFavorites, sessionManager);

                selectedBookLists.clear();
                adatper.reloadBookList(mBookList);
            }
        });

        favorSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                reloadFavorites(s);
                return false;
            }
        });
    }

    public void removeSelectedState() {
        imgBack.setImageDrawable(getResources().getDrawable(R.drawable.return_icon));
//                imgSort.setImageDrawable(getResources().getDrawable(R.drawable.sort_icon));
        txtFavoriteDetailTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtEdit.setTextColor(getResources().getColor(R.color.alpa_90));

        // make title bar component inactive
        imgBack.setEnabled(true);
//                imgSort.setEnabled(true);
        txtFavoriteDetailTitle.setEnabled(true);
        txtEdit.setEnabled(true);

        mainToolbar.setVisibility(View.VISIBLE);
        otherToolbar.setVisibility(View.GONE);
        linFooter.setVisibility(View.VISIBLE);

        Global.editStateOfFavoritesDetails = "no";
        Global.bookAction = QueryBook.BookAction.NONE;
//
//                for (FavoriteDetailBook one : mBookList) {
//                    one.setStateClick("noClicked");
//                }
        adatper.notifyDataSetChanged();
    }

    private void reloadFavorites(String keywords){
        searchedFavoritesList.clear();
        for (Book book : mBookList){
            if (book.bookName.contains(keywords)){
                searchedFavoritesList.add(book);
            }
        }
        adatper.reloadBookList(searchedFavoritesList);
    }

    @Override
    protected void onPause() {
        removeSelectedState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        newsReferenceUIWork();
        super.onResume();
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Global.editStateOfFavoritesDetails = "no";
    }

    @Override
    public void onDelete() {

        for (Book one : selectedBookLists) {
            bookStateChangeApiOperation(one);
        }
    }
    @Override
    public void onClickBookItem(Book selectedBook, int position) {
        if (Global.bookAction == QueryBook.BookAction.NONE){

        }else if (Global.bookAction == QueryBook.BookAction.SELECTTION){
            if (selectedBookLists.contains(selectedBook)){
                selectedBookLists.remove(selectedBook);
            }else{
                selectedBookLists.add(selectedBook);
            }
            adatper.reloadBookListWithSelection(selectedBookLists);
            if (selectedBookLists.size() > 0) {
                txtDelete.setEnabled(true);
//                txtAddFavorite.setEnabled(true);
                txtDownload.setEnabled(true);

                txtDelete.setTextColor(getResources().getColor(R.color.alpa_90));
//                txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_90));
                txtDownload.setTextColor(getResources().getColor(R.color.alpa_90));

            } else {
                txtDelete.setEnabled(false);
//                txtAddFavorite.setEnabled(false);
                txtDownload.setEnabled(false);

                txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
//                txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_40));
                txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));
            }
        }

    }

    public void getAllNews() {
        mLocalNewsData = db.getAllNews();

//        Global.showLoading(getContext(),"generate_report");
        Ion.with(FavoritesDetailActivity.this)
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

    public void bookStateChangeApiOperation(Book one) {

        if (ConnectivityHelper.isConnectedToNetwork(FavoritesDetailActivity.this)) {

            int selectedCount = selectedBookLists.size();
            Global.showLoading(FavoritesDetailActivity.this,"generate_report");
            Ion.with(FavoritesDetailActivity.this)
                    .load(Url.bookStateChangeOperation)
                    .setTimeout(10000)
                    .setBodyParameter("access_token", Global.access_token)
                    .setBodyParameter("bookId", one.bookId)
                    .setBodyParameter("collection", "")
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Log.i(TAG, "real time api result" + result);
                            String temp;
                            deleteCount++;

                            if (e == null) {
                                JSONObject resObj = null;
                                try {
                                    resObj = new JSONObject(result.toString());
                                    if (resObj.getString("res").equals("success")) {            // if operation is done successfully, get "succe".  if not, get "false"
                                        mBookList.remove(one);
                                        selectedBookLists.remove(one);
                                    } else {
                                        if (deleteCount == selectedCount) {
                                            Global.hideLoading();
                                            adatper.notifyDataSetChanged();
                                        }
                                    }

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                if (deleteCount == selectedCount) {
                                    Global.hideLoading();
                                    adatper.notifyDataSetChanged();
                                }
                            }

                            if (deleteCount == selectedCount) {
                                Global.hideLoading();
                                adatper.notifyDataSetChanged();

                                if (selectedBookLists.size() == 0) {
                                    txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
                                    txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

                                    // toolbar's primary state
                                    txtDelete.setEnabled(false);
                                    txtDownload.setEnabled(false);
                                }
                            }
                        }
                    });

        } else {
//            Toast.makeText(HorizontalBookReadingActivity.this, "Network is disconnected.", Toast.LENGTH_SHORT).show();
        }


    }


}
