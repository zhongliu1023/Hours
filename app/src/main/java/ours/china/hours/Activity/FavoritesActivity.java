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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ours.china.hours.Activity.Auth.RegisterActivity;
import ours.china.hours.Activity.Personality.UpdateinforActivity;
import ours.china.hours.Adapter.FavoritesAdapter;
import ours.china.hours.BookLib.foobnix.pdf.info.view.BookmarkPanel;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.Favorites;
import ours.china.hours.R;
import ours.china.hours.Utility.AlertAddFavorites;
import ours.china.hours.Utility.AlertDelete;
import ours.china.hours.Utility.SessionManager;

public class FavoritesActivity extends AppCompatActivity implements AlertAddFavorites.addFavoriteListener,
        FavoritesAdapter.addActionListener, AlertDelete.deleteButtonListener {

    RecyclerView recyclerFavorites;
    TextView addFavorite, favoriteTitle, txtEdit;
    ImageView imgBack;

    TextView txtComplete, txtDelete, txtRename, txtDownload;
    RelativeLayout mainToolbar, otherToolbar;

    SearchView favorSearchView;
    FavoritesAdapter adapter;
    ArrayList<Favorites> favoritesList;
    ArrayList<Favorites> searchedFavoritesList;

    AlertAddFavorites alertAdd, alertRename;
    AlertDelete alertDelete;

    String optionAddOrRename = "";
    SharedPreferencesManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        init();
        event();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null){
            reloadFavorites();
        }
    }

    public void reloadFavorites(){
        favoritesList = BookManagement.getFavorites(sessionManager);
        searchedFavoritesList = (ArrayList<Favorites>)favoritesList.clone();
        adapter.reloadFavorites(favoritesList);
    }

    public void init() {

        // for recycler View
        sessionManager = new SharedPreferencesManager(this);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);

        favoritesList = new ArrayList<Favorites>();
        searchedFavoritesList = new ArrayList<Favorites>();
        favoritesList = BookManagement.getFavorites(sessionManager);
        searchedFavoritesList = (ArrayList<Favorites>)favoritesList.clone();
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

        favorSearchView = findViewById(R.id.favorSearchView);
        favorSearchView.setQueryHint("搜索书名、作者、出版社");
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
                    one.isChecked = false;
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
                String changingName = "";

                for (Favorites one : favoritesList) {
                    if (one.isChecked) {
                        changingName = one.favorite;
                        break;
                    }
                }
                alertRename.setEditText(changingName);
                alertRename.show();
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
        for (Favorites favorites : favoritesList){
            if (favorites.favorite.contains(keywords)){
                searchedFavoritesList.add(favorites);
            }
        }
        adapter.reloadFavorites(searchedFavoritesList);
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
            if (one.isChecked) {
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
            if (!Global.fullFavorites.contains(str)){
                Favorites favorites = new Favorites();
                favorites.favorite = str;
                favoritesList.add(favorites);
                BookManagement.saveFavorites(favoritesList, sessionManager);

                if (Global.fullFavorites.isEmpty()){
                    Global.fullFavorites = str;
                }else{
                    Global.fullFavorites += ","+str;
                }
                BookManagement.saveFullFavorites(Global.fullFavorites, sessionManager);
                updateFavorites();

                optionAddOrRename = "";
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(FavoritesActivity.this, "Already exists!", Toast.LENGTH_SHORT).show();

            }
        }
        if (optionAddOrRename.equals("rename")) {
            for (Favorites one : favoritesList) {
                if (one.isChecked) {

                    String[] favors = Global.fullFavorites.split(",");
                    String updatedFavorites = "";
                    for (String aFavor : favors){
                        boolean isMatch = false;
                        if (aFavor.equals(one.favorite)){
                            isMatch = true;
                        }
                        if (updatedFavorites.isEmpty()){
                            updatedFavorites = aFavor;
                        }else{
                            updatedFavorites += "," + (isMatch ? str : aFavor);
                        }
                    }
                    BookManagement.saveFullFavorites(updatedFavorites, sessionManager);
                    updateFavorites();

                    one.favorite = str;
                    break;
                }
            }

            BookManagement.saveFavorites(favoritesList, sessionManager);
            optionAddOrRename = "";
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDelete() {

        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).isChecked) {
                favoritesList.remove(i);
                i = i - 1;
            }
        }

        String updatedFavorites = "";
        for (int i = 0; i < favoritesList.size(); i++) {
            if (updatedFavorites.isEmpty()){
                updatedFavorites = favoritesList.get(i).favorite;
            }else{
                updatedFavorites += "," + favoritesList.get(i).favorite;
            }
        }
        BookManagement.saveFullFavorites(updatedFavorites, sessionManager);
        updateFavorites();
        // for change color alpha
        txtDelete.setTextColor(getResources().getColor(R.color.alpa_40));
        txtRename.setTextColor(getResources().getColor(R.color.alpa_40));
        txtDownload.setTextColor(getResources().getColor(R.color.alpa_40));

        // for disable action
        txtDownload.setEnabled(false);
        txtRename.setEnabled(false);
        txtDelete.setEnabled(false);

        //
        BookManagement.saveFavorites(favoritesList, sessionManager);
        adapter.notifyDataSetChanged();
        alertDelete.dismiss();
    }

    void updateFavorites(){
        Ion.with(FavoritesActivity.this)
                .load(Url.update_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .setBodyParameter("collections", Global.fullFavorites)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Global.hideLoading();
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());

                                if (resObj.getString("res").equals("success")) {

                                } else {
                                    Toast.makeText(FavoritesActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Toast.makeText(FavoritesActivity.this, "正确注册你的脸", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
