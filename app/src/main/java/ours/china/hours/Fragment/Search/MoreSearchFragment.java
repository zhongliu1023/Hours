package ours.china.hours.Fragment.Search;

import android.Manifest;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.MoreBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MoreBook;
import ours.china.hours.R;
import pub.devrel.easypermissions.EasyPermissions;

public class MoreSearchFragment extends Fragment implements BookItemInterface, BookDetailsDialog.OnDownloadBookListenner {

    ArrayList<Book> moreBooks;
    MoreBookAdapter adapter;
    RecyclerView moreSearchRecyclerView;

    TextView txtBack;
    String keyWords = "";


    SharedPreferencesManager sessionManager;
    DBController db = null;
    BookDetailsDialog bookDetailsDialog;

    public MoreSearchFragment(String key){
        this.keyWords = key;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_book, container, false);

        recyclerViewInit(view);
        event(view);

        getAllLibraryBooksFromServer();
        return view;
    }

    public void recyclerViewInit(View view) {
        // for session management.
        db = new DBController(getActivity());
        sessionManager = new SharedPreferencesManager(getActivity());


        moreSearchRecyclerView = view.findViewById(R.id.more_search_result);

        moreBooks = new ArrayList<>();
        adapter = new MoreBookAdapter(getActivity(), moreBooks, this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        moreSearchRecyclerView.setLayoutManager(manager);

        moreSearchRecyclerView.setAdapter(adapter);
    }

    public void event(View rootView) {
        txtBack = rootView.findViewById(R.id.txtBack);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStack();

            }
        });
    }

    public void getAllLibraryBooksFromServer() {
        moreBooks = new ArrayList<>();

        Global.showLoading(getContext(),"generate_report");
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("keyword", Collections.singletonList(keyWords));

        Ion.with(getActivity())
                .load(Url.searchAllBookwithMobile)
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
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<Book>>() {}.getType();
                                    moreBooks = gson.fromJson(dataArray.toString(), type);
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
            adapter.reloadBookList(moreBooks);
            return;
        }

        for (int i = 0; i < moreBooks.size(); i++) {
            for (int j = 0; j < books.size(); j++) {
                Book one = moreBooks.get(i);
                Book oneLocal = books.get(j);

                if (one.bookId != null && oneLocal.bookId != null && one.bookId.equals(oneLocal.bookId)) {
                    one.bookLocalUrl = oneLocal.bookLocalUrl;
                    one.bookImageLocalUrl = oneLocal.bookImageLocalUrl;
                    one.libraryPosition = oneLocal.libraryPosition;
                    break;
                }
            }
        }

        adapter.reloadBookList(moreBooks);
    }

    @Override
    public void onClickBookItem(Book selectedBook, int position) {
        if (!selectedBook.bookLocalUrl.equals("") && !selectedBook.bookImageLocalUrl.equals("")) {
            BookManagement.saveFocuseBook(selectedBook, sessionManager);
            gotoReadingViewFile();
            return;
        }

        BookManagement.saveFocuseBook(selectedBook, sessionManager);

        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            bookDetailsDialog = new BookDetailsDialog(getActivity(), position,MoreSearchFragment.this);
            bookDetailsDialog.show();

            // set needed frame of dialog. Without this code, all the component of the dialog's layout don't have original size.
            Window rootWindow = getActivity().getWindow();
            Rect displayRect = new Rect();
            rootWindow.getDecorView().getWindowVisibleDisplayFrame(displayRect);
            bookDetailsDialog.getWindow().setLayout(displayRect.width(), displayRect.height());
            bookDetailsDialog.setCanceledOnTouchOutside(false);
        } else {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), 300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    void gotoReadingViewFile(){
        Book focuseBook = BookManagement.getFocuseBook(sessionManager);
        List<FileMeta> localBooks = AppDB.get().getAll();
        int tempLibraryPosition = Integer.parseInt(focuseBook.libraryPosition);

        ExtUtils.openFile(getActivity(), localBooks.get(tempLibraryPosition));
    }

    @Override
    public void onStartDownload(Book book, int position) {

    }
    @Override
    public void onFinishDownload(Book book, Boolean isSuccess, int position) {
        if (isSuccess) {
            for (int i = 0; i < moreBooks.size(); i++) {
                Book one = moreBooks.get(i);
                if (one.bookId != null && book.bookId != null && one.bookId.equals(book.bookId)) {
                    one.bookLocalUrl = book.bookLocalUrl;
                    one.bookImageLocalUrl = book.bookImageLocalUrl;
                    one.libraryPosition = book.libraryPosition;
                    adapter.notifyItemChanged(i);
                    break;
                }
            }

            adapter.reloadBookList(moreBooks);
        }
    }
}
