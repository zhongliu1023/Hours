package ours.china.hours.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.FavoritesDetailsAdatper;
import ours.china.hours.Model.FavoriteDetailBook;
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

    FavoritesDetailsAdatper adatper;
    ArrayList<FavoriteDetailBook> mBookList;

    AlertDelete alertDelete;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);

        init();
        event();
    }

    public void init() {

        // for recyclerView
        recyclerFavoritesDetailView = findViewById(R.id.recyclerFavoritesDetail);

        mBookList = new ArrayList<>();
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独1", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独2", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独3", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独4", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独5", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独6", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独7", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独8", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独9", "downloaded", "nonRead", "noClicked"));
        mBookList.add(new FavoriteDetailBook("hello", "百年孤独0", "downloaded", "nonRead", "noClicked"));

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

                for (FavoriteDetailBook one : mBookList) {
                    one.setStateClick("noClicked");
                }
                adatper.notifyDataSetChanged();
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // for visible process
                myBookShelfToolbar.setVisibility(View.VISIBLE);
                mainToolbar.setVisibility(View.GONE);
                otherToolbar.setVisibility(View.GONE);
                relTitle.setVisibility(View.GONE);

                txtFooterAddFavorite.setVisibility(View.VISIBLE);
                imgPlus.setVisibility(View.GONE);

                // for enable select item
                Global.editStateOfFavoritesDetails = "yes";
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

                // temp action instead of API action
                for (FavoriteDetailBook one : mBookList) {
                    one.setStateClick("noClicked");
                }
                adatper.notifyDataSetChanged();
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

                // for disable select item
                Global.editStateOfFavoritesDetails = "no";

                // temp action instead of API action
                for (FavoriteDetailBook one : mBookList) {
                    one.setStateClick("noClicked");
                }
                adatper.notifyDataSetChanged();
            }
        });
    }

    public boolean isExistCilckedItem() {
        boolean result = false;
        for (FavoriteDetailBook one : mBookList) {
            if (one.getStateClick().equals("clicked")) {
                result = true;
                break;
            }
        }

        return result;
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }



    @Override
    public void enableComponentAction() {
        if (isExistCilckedItem()) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Global.editStateOfFavoritesDetails = "no";
    }

    @Override
    public void onDelete() {

        for (int i = 0; i < mBookList.size(); i++) {
            if (mBookList.get(i).getStateClick().equals("clicked")) {
                mBookList.remove(i);
                i--;
            }
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

}
