package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import ours.china.hours.Activity.FavoritesDetailActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.SelectFavoriteAdapter;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.SelectFavorite;
import ours.china.hours.R;

public class SelectFavoriteDialog extends Dialog {
    private final String TAG = "SelectFavoriteDialog";

    private Context context;
    SharedPreferencesManager sessionManager;
    ArrayList<Book> focusBooks = new ArrayList<>();

    DBController db = null;

    TextView txtCancel, txtConfirm;
    ListView lvFavoritesCollection;
    SelectFavoriteAdapter adapter;

    ArrayList<String> mFavoriteCollections;
    ArrayList<String> favorites;

    int i = 0;

    public SelectFavoriteDialog(Context context) {
        super(context);
        this.context = context;

        setContentView(R.layout.dialog_select_favorite);
        init();
        event();
    }

    public void init() {
        // for
        db = new DBController(context);
        sessionManager = new SharedPreferencesManager(context);
        focusBooks = BookManagement.getBooks(sessionManager);

        // init process
        txtCancel = findViewById(R.id.txtCancel);
        txtConfirm = findViewById(R.id.txtConfirm);

        favorites = new ArrayList<>();
        mFavoriteCollections = new ArrayList<>();
        String[] temp = Global.fullFavorites.split(",");
        mFavoriteCollections.addAll(Arrays.asList(temp));

        Log.i(TAG, mFavoriteCollections.toString());

        lvFavoritesCollection = findViewById(R.id.favoritesCollection);
        adapter = new SelectFavoriteAdapter(context, mFavoriteCollections);
        lvFavoritesCollection.setAdapter(adapter);
    }

    public void event() {
        lvFavoritesCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "selected position => " + i);
                CheckedTextView checkedTextView = view.findViewById(R.id.checkTextView);

                if (checkedTextView.isChecked()) {
                    favorites.remove(mFavoriteCollections.get(i));
                    checkedTextView.setChecked(false);
                } else {
                    checkedTextView.setChecked(true);
                    favorites.add(mFavoriteCollections.get(i));
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.showLoading(context, "generate_report");

                for (Book one : focusBooks) {
                    for (String favorite : favorites) {
                        if (one.bookStatus.collection.contains(favorite)) {

                        } else {
                            one.bookStatus.collection += "," + favorite;
                        }
                    }
                }

                apiWork();
            }
        });
    }

    public void apiWork() {
        if (i == focusBooks.size()) {
            Global.hideLoading();
            Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
            dismiss();
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
}
