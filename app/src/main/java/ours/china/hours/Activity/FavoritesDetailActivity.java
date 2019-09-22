package ours.china.hours.Activity;

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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ours.china.hours.Adapter.FavoritesDetailsAdatper;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalBookReadingActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.FavoriteDetailBook;
import ours.china.hours.Model.Favorites;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;
import ours.china.hours.Utility.AlertDelete;

public class FavoritesDetailActivity extends AppCompatActivity implements FavoritesDetailsAdatper.addActionListener, AlertDelete.deleteButtonListener {

    RecyclerView recyclerFavoritesDetailView;
    TextView txtFavoriteDetailTitle;
    TextView txtEdit;
    LinearLayout linFooter;
    RelativeLayout relTitle;
    ImageView imgBack, imgSort, imgPlus;

    RelativeLayout mainToolbar, otherToolbar, myBookShelfToolbar;
    TextView txtComplete, txtDelete, txtAddFavorite, txtDownload;

    TextView txtCancel, txtFooterAddFavorite;

    SearchView favorSearchView;
    FavoritesDetailsAdatper adatper;
    ArrayList<Book> mBookList;
    ArrayList<Book> bookListFromLocal;
    ArrayList<Book> selectedBookLists;
    ArrayList<Book> searchedFavoritesList;

    String favorite = "";

    AlertDelete alertDelete;
    SharedPreferencesManager sessionManager;
    DBController db = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);

        init();
        event();
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
        ArrayList<Favorites> selectedBookList = BookManagement.getFavorites(sessionManager);
        for (Favorites favorites : selectedBookList){
            if (favorites.favorite.equals(favorite)){
                mBookList = (ArrayList<Book>)favorites.bookList.clone();
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
    }

    public void event() {
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
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });

        txtComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
//
//                for (FavoriteDetailBook one : mBookList) {
//                    one.setStateClick("noClicked");
//                }
                adatper.notifyDataSetChanged();
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
    private void reloadFavorites(String keywords){
        searchedFavoritesList.clear();
        for (Book book : mBookList){
            if (book.bookName.contains(keywords)){
                searchedFavoritesList.add(book);
            }
        }
        adatper.reloadBookList(searchedFavoritesList);
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Global.editStateOfFavoritesDetails = "no";
    }

    @Override
    public void onDelete() {

        for (int i = 0; i < mBookList.size(); i++) {
//            if (mBookList.get(i).getStateClick().equals("clicked")) {
//                mBookList.remove(i);
//                i--;
//            }
        }
        adatper.notifyDataSetChanged();

        txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
        txtAddFavorite.setTextColor(getResources().getColor(R.color.alpa_40));
        txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

        // toolbar's primary state
        txtDelete.setEnabled(false);
        txtAddFavorite.setEnabled(false);
        txtDownload.setEnabled(false);
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
}
