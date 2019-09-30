package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.SelectFavoriteAdapter;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.Favorites;
import ours.china.hours.R;

public class SelectFavoriteDialog extends Dialog {
    private final String TAG = "SelectFavoriteDialog";

    private Context context;
    SelectFavoriteDialogInterface listener;

    SharedPreferencesManager sessionManager;
    ArrayList<Book> focusBooks = new ArrayList<>();

    DBController db = null;

    TextView txtCancel, txtConfirm;
    ListView lvFavoritesCollection;
    SelectFavoriteAdapter adapter;

    ArrayList<String> mFavoriteFolderNameCollections;
    ArrayList<String> favoriteFloderNameArrayList;

    EditText addFavoriteFolderName;
    TextView txtAddFolder;

    ArrayList<Favorites> tempFavorites;

    int i = 0;

    public SelectFavoriteDialog(Context context, SelectFavoriteDialogInterface listener) {
        super(context);
        this.context = context;
        this.listener = listener;

        setContentView(R.layout.dialog_select_favorite);
        init();
        event();
    }

    public void init() {
        // for
        db = new DBController(context);
        sessionManager = new SharedPreferencesManager(context);
        focusBooks = BookManagement.getBooks(sessionManager);

        // get all favorite book in favorite.
        tempFavorites = BookManagement.getFavorites(sessionManager);

        // init process
        txtCancel = findViewById(R.id.txtCancel);
        txtConfirm = findViewById(R.id.txtConfirm);

        favoriteFloderNameArrayList = new ArrayList<>();
        mFavoriteFolderNameCollections = new ArrayList<>();
        String[] temp = Global.fullFavorites.split(",");
        mFavoriteFolderNameCollections.addAll(Arrays.asList(temp));

        Log.i(TAG, mFavoriteFolderNameCollections.toString());

        lvFavoritesCollection = findViewById(R.id.favoritesCollection);
        adapter = new SelectFavoriteAdapter(context, mFavoriteFolderNameCollections);
        lvFavoritesCollection.setAdapter(adapter);

        // editText
        addFavoriteFolderName = findViewById(R.id.addFavoriteFolderName);
        txtAddFolder = findViewById(R.id.txtAddFolder);
    }

    public void event() {
        lvFavoritesCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "selected position => " + i);
                CheckedTextView checkedTextView = view.findViewById(R.id.checkTextView);

                if (checkedTextView.isChecked()) {
                    favoriteFloderNameArrayList.remove(mFavoriteFolderNameCollections.get(i));
                    checkedTextView.setChecked(false);
                } else {
                    checkedTextView.setChecked(true);
                    favoriteFloderNameArrayList.add(mFavoriteFolderNameCollections.get(i));
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.afterDialogDismissWork();
                dismiss();
            }
        });

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.showLoading(context, "generate_report");

                // update Favorites list.
                for (Book one : focusBooks) {
                    for (String favoriteFolderName : favoriteFloderNameArrayList) {
                        for (Favorites favorites : tempFavorites) {
                            if (favorites.favorite.equals(favoriteFolderName)) {
                                if (favorites.bookList.contains(one)) {
                                    continue;
                                } else {
                                    favorites.bookList.add(one);
                                }
                            }
                        }
                    }
                }

                // update book's collection property.
                for (Book one : focusBooks) {
                    for (String favoriteFolderName : favoriteFloderNameArrayList) {
                        if (one.bookStatus.collection.contains(favoriteFolderName)) {

                        } else {
                            one.bookStatus.collection += "," + favoriteFolderName;
                        }
                    }
                }

                apiWork();
            }
        });

        txtAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str = addFavoriteFolderName.getText().toString();
                if (str.equals("")) {
                    return;
                }

                if (!Global.fullFavorites.contains(str)){
                    Favorites favorites = new Favorites();
                    favorites.favorite = str;
                    tempFavorites.add(favorites);
                    BookManagement.saveFavorites(tempFavorites, sessionManager);

                    if (Global.fullFavorites.isEmpty()){
                        Global.fullFavorites = str;
                    }else{
                        Global.fullFavorites += ","+str;
                    }
                    BookManagement.saveFullFavorites(Global.fullFavorites, sessionManager);
                    updateFavorites();

                    mFavoriteFolderNameCollections.add(str);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(context, "已经存在", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void apiWork() {
        if (i == focusBooks.size()) {
            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
            dismiss();
            BookManagement.saveFavorites(tempFavorites, sessionManager);

            Global.hideLoading();
            return;
        } else {
            Book one = focusBooks.get(i);
            Ion.with(context)
                    .load(Url.bookStateChangeOperation)
                    .setTimeout(10000)
                    .setBodyParameter("access_token", Global.access_token)
                    .setBodyParameter("bookId", one.bookId)
                    .setBodyParameter("collection", one.bookStatus.collection)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            Log.i(TAG, "result => " + result);
                            if (e == null) {
                                try {
                                    JSONObject resObj = new JSONObject(result);
                                    if (resObj.getString("res").equals("success")) {
                                        db.updateBookStateData(one.bookStatus, one.bookId);
                                        i++;
                                        apiWork();
                                    } else {
                                        Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
                                        Global.hideLoading();
                                        return;
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                            } else {
                                Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
                                Global.hideLoading();
                                return;
                            }
                        }
                    });
        }
    }

    void updateFavorites(){
        Ion.with(context)
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
                                    Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public interface SelectFavoriteDialogInterface {
        public void afterDialogDismissWork();
    }

}
