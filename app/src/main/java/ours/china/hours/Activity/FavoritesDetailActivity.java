package ours.china.hours.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ours.china.hours.Adapter.FavoritesDetailsAdatper;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalBookReadingActivity;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Dialog.SelectFavoriteDialog;
import ours.china.hours.Fragment.BookTab.BookFragment;
import ours.china.hours.Fragment.HomeTab.HomeFragment;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.NewsManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.FavoriteDetailBook;
import ours.china.hours.Model.Favorites;
import ours.china.hours.Model.NewsItem;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.Model.SortByAuthor;
import ours.china.hours.Model.SortByPublishDate;
import ours.china.hours.Model.SortByTitle;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;
import ours.china.hours.Services.DownloadingService;
import ours.china.hours.Utility.AlertDelete;
import ours.china.hours.Utility.ConnectivityHelper;
import pub.devrel.easypermissions.EasyPermissions;

import static ours.china.hours.Constants.ActivitiesCodes.CONNECTED_FILE_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.FINISHED_DOWNLOADING_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.PROGRESS_UPDATE_ACTION;

public class FavoritesDetailActivity extends AppCompatActivity implements FavoritesDetailsAdatper.addActionListener, AlertDelete.deleteButtonListener, BookDetailsDialog.OnDownloadBookListenner, SelectFavoriteDialog.SelectFavoriteDialogInterface {
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
    ArrayList<Book> forPlusBooks;
    ArrayList<Book> selectedBookLists;
    ArrayList<Book> searchedFavoritesList;
    ArrayList<Favorites> selectedFavorites;

    String favorite = "";
    String tempPopupWindow2String = "默认";
    PopupWindow popupWindow2;
    LinearLayout linDefault, linRecent;
    RelativeLayout relTitle1, relAuthor;
    TextView txtDefault, txtRecent, txtTitle, txtAuthor;
    ImageView imgTitle, imgAuthor;

    BookDetailsDialog bookDetailsDialog;

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

        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTED_FILE_ACTION);
        filter.addAction(PROGRESS_UPDATE_ACTION);
        filter.addAction(FINISHED_DOWNLOADING_ACTION);
        LocalBroadcastManager.getInstance(FavoritesDetailActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String bookID = intent.getStringExtra("bookID");
                        switch (intent.getAction()) {
                            case CONNECTED_FILE_ACTION:
                                connectedFile(bookID);
                                break;
                            case PROGRESS_UPDATE_ACTION:
                                int progress = intent.getIntExtra("progress", -1);
                                publishProgress(bookID, progress);
                                break;
                            case FINISHED_DOWNLOADING_ACTION:
                                String path = intent.getStringExtra("path");
                                finishedDownload(bookID, path);
                                break;
                        }
                    }
                }, filter);

        init();
        popupWindowWork();
        event();
        getAllNews();
    }


    private void connectedFile(String bookID) {
        if (!Global.mBookFiles.containsKey(bookID)){
            BookFile bookFile = new BookFile(bookID, "");
            Global.mBookFiles.put(bookID, bookFile);
        }
        adatper.reloadbookwithDownloadStatus(Global.mBookFiles);
    }

    private void publishProgress(String bookID, int soFarBytes) {
        if (Global.mBookFiles.containsKey(bookID)){
            BookFile bookFile = Global.mBookFiles.get(bookID);
            bookFile.setProgress(soFarBytes);
        }else {
            BookFile bookFile = new BookFile(bookID, "");
            bookFile.setProgress(soFarBytes);
            Global.mBookFiles.put(bookID, bookFile);
        }
        adatper.reloadbookwithDownloadStatus(Global.mBookFiles);
    }

    private void finishedDownload(String bookID, String path) {

        Book focusBook = new Book();
        int itemPosition = 0;
        for (int i = 0; i < mBookList.size(); i++) {
            Book one = mBookList.get(i);
            if (one.bookId.equals(bookID)) {
                focusBook = one;
                itemPosition = i;
                break;
            }
        }

        if (Global.mBookFiles.containsKey(bookID)){
            Global.mBookFiles.remove(bookID);
        } else {
            int tempPosition = AppDB.get().getAll().size() - 1;
            focusBook.bookLocalUrl = path;
            focusBook.libraryPosition = String.valueOf(tempPosition);
            adatper.notifyItemChanged(itemPosition);
            return;
        }

        int tempPosition = 0;
        if (AppDB.get().getAll() == null || AppDB.get().getAll().size() == 0) {
            tempPosition = 0;
        } else {
            tempPosition = AppDB.get().getAll().size();
        }

        focusBook.bookLocalUrl = path;
        focusBook.libraryPosition = String.valueOf(tempPosition);

        if (db.getBookData(focusBook.bookId) == null){
            db.insertData(focusBook);
        }else{
            db.updateBookDataWithDownloadData(focusBook);
        }
        BookManagement.saveFocuseBook(focusBook, sessionManager);

        if (!ExtUtils.isExteralSD(focusBook.bookLocalUrl)) {
            FileMeta meta = AppDB.get().getOrCreate(focusBook.bookLocalUrl);
            AppDB.get().updateOrSave(meta);
            IMG.loadCoverPageWithEffect(meta.getPath(), IMG.getImageSize());
        }

        Ion.with(FavoritesDetailActivity.this)
                .load(Url.addToMybooks)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("bookId", focusBook.bookId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        if (error == null) {
                            try {
                                JSONObject resObject = new JSONObject(result.toString());
                                if (resObject.getString("res").equals("success")||
                                        (resObject.getString("res").equals("fail") && resObject.getString("err_msg").equals("已添加"))) {

                                    adatper.reloadBookList(mBookList);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void init() {

        favorite = getIntent().getStringExtra("favoriteType");
        sessionManager = new SharedPreferencesManager(this);
        db = new DBController(FavoritesDetailActivity.this);

        recyclerFavoritesDetailView = findViewById(R.id.recyclerFavoritesDetail);
        mBookList = new ArrayList<Book>();
        selectedBookLists = new ArrayList<Book>();
        bookListFromLocal = new ArrayList<Book>();
        forPlusBooks = new ArrayList<>();
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
        imgSort = findViewById(R.id.imgSort);
        imgPlus = findViewById(R.id.imgPlus);

        // for toolbar
        mainToolbar = findViewById(R.id.mainToolbars);
        otherToolbar = findViewById(R.id.otherToolbar);
        txtComplete = findViewById(R.id.txtComplete);
        txtDelete = findViewById(R.id.txtDelete);
        txtAddFavorite = findViewById(R.id.txtAddFavorite);
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

    public void popupWindowWork() {
        popupWindowInit();

        linRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "最近";
//                maskLayer.setVisibility(View.GONE);

                Collections.sort(mBookList, new SortByPublishDate());
//                Global.showLoading(getContext(),"generate_report");
//                getAllDataFromServer();
                adatper.reloadBookList(mBookList);
                popupWindow2.dismiss();

            }
        });

        relTitle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "标题";
//                maskLayer.setVisibility(View.GONE);

                Collections.sort(mBookList, new SortByTitle());
//                Global.showLoading(getContext(),"generate_report");
//                getAllDataFromServer();
                adatper.reloadBookList(mBookList);
                popupWindow2.dismiss();
            }
        });

        relAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "作者";
//                maskLayer.setVisibility(View.GONE);

                Log.i("BookFragment", "SearchedBookList => " + mBookList.toString());
                Collections.sort(mBookList, new SortByAuthor());
                Log.i("BookFragment", "SearchedBookList => " + mBookList.toString());
//                Global.showLoading(getContext(),"generate_report");
//                getAllDataFromServer();
                adatper.reloadBookList(mBookList);
                popupWindow2.dismiss();
            }
        });

//        maskLayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow2.dismiss();
//            }
//        });

    }

    public void popupWindowInit() {

        // for popupWindow 2
        View view2 = LayoutInflater.from(this).inflate(R.layout.popup4, null);

        linRecent = view2.findViewById(R.id.linRecent);
        relTitle1 = view2.findViewById(R.id.relTitle);
        relAuthor = view2.findViewById(R.id.relAuthor);

        txtRecent = view2.findViewById(R.id.txtRecent);
        txtTitle = view2.findViewById(R.id.txtTitle);
        txtAuthor = view2.findViewById(R.id.txtAuthor);

        imgTitle = view2.findViewById(R.id.imgTitle);
        imgAuthor = view2.findViewById(R.id.imgAuthor);

        popupWindow2 = new PopupWindow(view2, (int) getResources().getDimension(R.dimen.popup_window_width), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow2.setAnimationStyle(R.style.popupwindowAnimation);
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
                imgSort.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_40), getResources().getDrawable(R.drawable.sort_icon)));
                txtFavoriteDetailTitle.setTextColor(getResources().getColor(R.color.alpa_40));
                txtEdit.setTextColor(getResources().getColor(R.color.alpa_40));

                // make title bar component inactive
                imgBack.setEnabled(false);
                imgSort.setEnabled(false);
                txtEdit.setEnabled(false);

                mainToolbar.setVisibility(View.GONE);
                otherToolbar.setVisibility(View.VISIBLE);

                txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
                txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_40));
                txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

                // toolbar's primary state
                txtDelete.setEnabled(false);
                txtAddFavorite.setEnabled(false);
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

                FavoritesDetailActivity.super.onBackPressed();
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDelete.show();
            }
        });

        txtAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookManagement.setBooks(selectedBookLists, sessionManager);

                SelectFavoriteDialog dialog = new SelectFavoriteDialog(FavoritesDetailActivity.this, FavoritesDetailActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        txtDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Book> tempArrayList = new ArrayList<>();

                for (Book one : selectedBookLists) {
                    if (!one.bookLocalUrl.equals("")) {
                        continue;
                    } else {
                        tempArrayList.add(one);
                    }
                }

                mainToolbar.setVisibility(View.VISIBLE);
                otherToolbar.setVisibility(View.GONE);
                myBookShelfToolbar.setVisibility(View.GONE);

                selectedBookLists.clear();
                adatper.reloadBookList(mBookList);
                Global.bookAction = QueryBook.BookAction.NONE;
                ArrayList<BookFile> bookFiles = new ArrayList<BookFile>();
                for (Book book : tempArrayList){
                    BookFile bookFile = new BookFile(book.bookId, Url.domainUrl + book.bookNameUrl);
                    bookFiles.add(bookFile);

                    if (!Global.mBookFiles.containsKey(book.bookId)){
                        Global.mBookFiles.put(book.bookId, bookFile);
                    }
                }


                Intent intent = new Intent(FavoritesDetailActivity.this, DownloadingService.class);
                intent.putParcelableArrayListExtra("bookFile", bookFiles);
                startService(intent);
            }
        });

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
//                ArrayList<Book> tmpBookList = db.getAllData();
//                bookListFromLocal.clear();
//                for (Book book : tmpBookList){
//                    if (book.bookStatus != null && !book.bookStatus.collection.contains(favorite)){
//                        bookListFromLocal.add(book);
//                    }
//                }
//                adatper.reloadBookList(bookListFromLocal);

                forPlusBooks = BookManagement.getBooks(sessionManager);
                adatper.reloadBookList(forPlusBooks);
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainToolbar.setVisibility(View.VISIBLE);
                otherToolbar.setVisibility(View.GONE);
                myBookShelfToolbar.setVisibility(View.GONE);
                relTitle.setVisibility(View.VISIBLE);

                // added on 10.1
                selectedBookLists.clear();
                Global.bookAction = QueryBook.BookAction.NONE;

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

        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTextImagePopupWindow2();
                judgePopupWindow2();
                popupWindow2.showAsDropDown(view);
//                maskLayer.setVisibility(View.VISIBLE);
            }
        });
    }

    public void allTextImagePopupWindow2() {
        txtRecent.setTextColor(getResources().getColor(R.color.alpa_90));
        txtTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtAuthor.setTextColor(getResources().getColor(R.color.alpa_90));

        imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.reverse_order_icon)));
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


    public void removeSelectedState() {
        imgBack.setImageDrawable(getResources().getDrawable(R.drawable.return_icon));
        imgSort.setImageDrawable(getResources().getDrawable(R.drawable.sort_icon));
        txtFavoriteDetailTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtEdit.setTextColor(getResources().getColor(R.color.alpa_90));

        // make title bar component inactive
        imgBack.setEnabled(true);
        imgSort.setEnabled(true);
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
            if (!selectedBook.bookLocalUrl.equals("")) {
                BookManagement.saveFocuseBook(selectedBook, sessionManager);
                gotoReadingViewFile();
                return;
            }
            BookManagement.saveFocuseBook(selectedBook, sessionManager);

            if (EasyPermissions.hasPermissions(FavoritesDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                bookDetailsDialog = new BookDetailsDialog(FavoritesDetailActivity.this ,position, FavoritesDetailActivity.this);
                bookDetailsDialog.show();

                // set needed frame of dialog. Without this code, all the component of the dialog's layout don't have original size.
                Window rootWindow = this.getWindow();
                Rect displayRect = new Rect();
                rootWindow.getDecorView().getWindowVisibleDisplayFrame(displayRect);
                bookDetailsDialog.getWindow().setLayout(displayRect.width(), displayRect.height());
                bookDetailsDialog.setCanceledOnTouchOutside(false);
            }else{
                EasyPermissions.requestPermissions(FavoritesDetailActivity.this, getString(R.string.write_file), 300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else if (Global.bookAction == QueryBook.BookAction.SELECTTION){
            if (selectedBookLists.contains(selectedBook)){
                selectedBookLists.remove(selectedBook);
            }else{
                selectedBookLists.add(selectedBook);
            }
            adatper.reloadBookListWithSelection(selectedBookLists);
            if (selectedBookLists.size() > 0) {
                txtDelete.setEnabled(true);
                txtAddFavorite.setEnabled(true);
                txtDownload.setEnabled(true);

                txtDelete.setTextColor(getResources().getColor(R.color.alpa_90));
                txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_90));
                txtDownload.setTextColor(getResources().getColor(R.color.alpa_90));

            } else {
                txtDelete.setEnabled(false);
                txtAddFavorite.setEnabled(false);
                txtDownload.setEnabled(false);

                txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
                txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_40));
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
                                    txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_40));

                                    // toolbar's primary state
                                    txtDelete.setEnabled(false);
                                    txtDownload.setEnabled(false);
                                    txtAddFavorite.setEnabled(false);
                                }
                            }
                        }
                    });

        } else {
//            Toast.makeText(HorizontalBookReadingActivity.this, "Network is disconnected.", Toast.LENGTH_SHORT).show();
        }
    }

    void gotoReadingViewFile(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);
        List<FileMeta> localBooks = AppDB.get().getAll();
        int tempLibraryPosition = Integer.parseInt(focuseBook.libraryPosition);

        ExtUtils.openFile(FavoritesDetailActivity.this, localBooks.get(tempLibraryPosition));
    }


    @Override
    public void onFinishDownload(Book book, Boolean isSuccess, int position) {

    }

    @Override
    public void onStartDownload(Book book, int position) {

        if (!Global.mBookFiles.containsKey(book.bookId)){
            BookFile bookFile = new BookFile(book.bookId, Url.domainUrl + book.bookNameUrl);
            Global.mBookFiles.put(book.bookId, bookFile);
            Intent intent = new Intent(FavoritesDetailActivity.this, DownloadingService.class);
            intent.putParcelableArrayListExtra("bookFile", new ArrayList<BookFile>(Arrays.asList(bookFile)));
            startService(intent);
        }
    }

    @Override
    public void afterDialogDismissWork() {
        super.onBackPressed();
    }
}
