package ours.china.hours.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.FavoritesAdapter;
import ours.china.hours.Model.Favorites;
import ours.china.hours.R;
import ours.china.hours.Utility.AlertAddFavorites;
import ours.china.hours.Utility.AlertDelete;

public class FavoritesActivity extends AppCompatActivity implements AlertAddFavorites.addFavoriteListener,
        FavoritesAdapter.addActionListener, AlertDelete.deleteButtonListener {

    RecyclerView recyclerFavorites;
    TextView addFavorite, favoriteTitle, txtEdit;
    ImageView imgBack;

    TextView txtComplete, txtDelete, txtRename, txtDownload;
    RelativeLayout mainToolbar, otherToolbar;

    FavoritesAdapter adapter;
    ArrayList<Favorites> favoritesList;

    AlertAddFavorites alertAdd, alertRename;
    AlertDelete alertDelete;

    String optionAddOrRename = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        init();
        event();
    }

    public void init() {

        // for recycler View
        recyclerFavorites = findViewById(R.id.recyclerFavorites);

        favoritesList = new ArrayList<>();
        favoritesList.add(new Favorites("hello", "hello", "hello", "人文", "noClicked"));
        favoritesList.add(new Favorites("hello", "hello", "hello", "地理", "noClicked"));
        favoritesList.add(new Favorites("hello", "hello", "hello", "人文", "noClicked"));
        favoritesList.add(new Favorites("hello", "hello", "hello", "地理", "noClicked"));

        adapter = new FavoritesAdapter(favoritesList, FavoritesActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FavoritesActivity.this, 2);
        recyclerFavorites.setLayoutManager(gridLayoutManager);
        recyclerFavorites.setAdapter(adapter);

        // for alert
        alertAdd = new AlertAddFavorites(FavoritesActivity.this, R.style.AppTheme_Alert, "添加收藏夹");
        alertAdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertRename = new AlertAddFavorites(FavoritesActivity.this, R.style.AppTheme_Alert, "重命名收藏夹");
        alertRename.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDelete = new AlertDelete(FavoritesActivity.this, R.style.AppTheme_Alert, "确定要收藏夹吗？");
        alertDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // for main component
        txtComplete = findViewById(R.id.txtComplete);
        txtDelete = findViewById(R.id.txtDelete);
        txtRename = findViewById(R.id.txtRename);
        txtDownload = findViewById(R.id.txtDownload);

        imgBack = findViewById(R.id.imgBack);
        favoriteTitle = findViewById(R.id.favoriteTitle);
        addFavorite = findViewById(R.id.txtAdd);
        txtEdit = findViewById(R.id.txtEdit);

        mainToolbar = findViewById(R.id.mainToolbar);
        otherToolbar = findViewById(R.id.otherToolbar);
    }

    public void event() {

        addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionAddOrRename = "add";
                alertAdd.show();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    FavoritesActivity.this.finish();
                } else {
                    FavoritesActivity.super.onBackPressed();
                }
            }
        });

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // for change color alpha
                imgBack.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_40), getResources().getDrawable(R.drawable.return_icon)));
                favoriteTitle.setTextColor(getResources().getColor(R.color.alpa_40));
                addFavorite.setTextColor(getResources().getColor(R.color.alpa_40));
                txtEdit.setTextColor(getResources().getColor(R.color.alpa_40));

                // visible process
                mainToolbar.setVisibility(View.GONE);
                otherToolbar.setVisibility(View.VISIBLE);

                // for disable action
                addFavorite.setEnabled(false);
                txtEdit.setEnabled(false);
                imgBack.setEnabled(false);

                // for enable edit
                Global.editStateOfFavorite = "yes";
            }
        });

        txtComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // visible process
                mainToolbar.setVisibility(View.VISIBLE);
                otherToolbar.setVisibility(View.GONE);

                // for change color alpha
                imgBack.setImageDrawable(getResources().getDrawable(R.drawable.return_icon));
                favoriteTitle.setTextColor(getResources().getColor(R.color.alpa_90));
                addFavorite.setTextColor(getResources().getColor(R.color.alpa_90));
                txtEdit.setTextColor(getResources().getColor(R.color.alpa_90));

                // for enable action
                addFavorite.setEnabled(true);
                txtEdit.setEnabled(true);
                imgBack.setEnabled(true);

                // for disable edit
                Global.editStateOfFavorite = "no";

                for (Favorites one : favoritesList) {
                    one.setStateClick("noClicked");
                }
                adapter.notifyDataSetChanged();
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDelete.show();
            }
        });

        txtRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionAddOrRename = "rename";
                alertRename.show();
            }
        });
    }

    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }

    public int numberOfClickedItem() {
        int count = 0;
        for (Favorites one : favoritesList) {
            if (one.getStateClick().equals("clicked")) {
                count = count + 1;
            }
        }

        return count;
    }

    @Override
    public void enableComponentAction() {
        if (numberOfClickedItem() == 1) {

            // for change color alpha
            txtDelete.setTextColor(getResources().getColor(R.color.alpa_90));
            txtRename.setTextColor(getResources().getColor(R.color.alpa_90));
            txtDownload.setTextColor(getResources().getColor(R.color.alpa_90));

            // for enable action
            txtDelete.setEnabled(true);
            txtRename.setEnabled(true);
            txtDownload.setEnabled(true);

        } else if (numberOfClickedItem() > 1) {

            // for change color alpha
            txtDelete.setTextColor(getResources().getColor(R.color.alpa_90));
            txtRename.setTextColor(getResources().getColor(R.color.alpa_40));
            txtDownload.setTextColor(getResources().getColor(R.color.alpa_90));

            // for action process
            txtDelete.setEnabled(true);
            txtRename.setEnabled(false);
            txtDownload.setEnabled(true);

        } else if (numberOfClickedItem() == 0) {

            // for change color alpha
            txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
            txtRename.setTextColor(getResources().getColor(R.color.alpa_40));
            txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

            // for disable action
            txtDownload.setEnabled(false);
            txtRename.setEnabled(false);
            txtDelete.setEnabled(false);
        }
    }

    @Override
    public void addFavorite(String str) {
        if (optionAddOrRename.equals("add")) {
            favoritesList.add(new Favorites(str, "noClicked"));
            optionAddOrRename = "";
            adapter.notifyDataSetChanged();
        }
        if (optionAddOrRename.equals("rename")) {
            for (Favorites one : favoritesList) {
                if (one.getStateClick().equals("clicked")) {
                    one.setFavoriteType(str);
                    break;
                }
            }

            optionAddOrRename = "";
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDelete() {

        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getStateClick().equals("clicked")) {
                favoritesList.remove(i);
                i = i - 1;
            }
        }

        // for change color alpha
        txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
        txtRename.setTextColor(getResources().getColor(R.color.alpa_40));
        txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

        // for disable action
        txtDownload.setEnabled(false);
        txtRename.setEnabled(false);
        txtDelete.setEnabled(false);

        //
        adapter.notifyDataSetChanged();
        alertDelete.dismiss();
    }
}
