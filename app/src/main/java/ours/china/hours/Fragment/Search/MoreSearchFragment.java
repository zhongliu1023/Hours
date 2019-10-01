package ours.china.hours.Fragment.Search;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.MoreBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Interfaces.PageLoadInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MoreBook;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;
import ours.china.hours.Services.DownloadingService;
import ours.china.hours.Utility.ConnectivityHelper;
import pub.devrel.easypermissions.EasyPermissions;

import static ours.china.hours.Constants.ActivitiesCodes.CONNECTED_FILE_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.FINISHED_DOWNLOADING_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.PROGRESS_UPDATE_ACTION;

public class MoreSearchFragment extends Fragment implements BookItemInterface, BookDetailsDialog.OnDownloadBookListenner, PageLoadInterface {
    private final String TAG = "MoreSearchFragment";

    private Context mContext;
    private Activity mActivity;

    ArrayList<Book> moreBooks;
    MoreBookAdapter adapter;
    RecyclerView moreSearchRecyclerView;
    SwipeRefreshLayout swipe_refresh_layout;

    TextView txtBack;
    String keyWords = "";
    int totalCount;

    SharedPreferencesManager sessionManager;
    DBController db = null;
    BookDetailsDialog bookDetailsDialog;

    public MoreSearchFragment(String key){
        this.keyWords = key;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTED_FILE_ACTION);
        filter.addAction(PROGRESS_UPDATE_ACTION);
        filter.addAction(FINISHED_DOWNLOADING_ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
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
    }

    private void connectedFile(String bookID) {
        if (!Global.mBookFiles.containsKey(bookID)){
            BookFile bookFile = new BookFile(bookID, "");
            Global.mBookFiles.put(bookID, bookFile);
        }
        adapter.reloadbookwithDownloadStatus(Global.mBookFiles);
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
        adapter.reloadbookwithDownloadStatus(Global.mBookFiles);
    }
    private void finishedDownload(String bookID, String path) {

        Book focusBook = new Book();
        int itemPosition = 0;
        for (int i = 0; i < moreBooks.size(); i++) {
            Book one = moreBooks.get(i);
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
            adapter.notifyItemChanged(itemPosition);
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

        Ion.with(mContext)
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

                                    adapter.reloadBookList(moreBooks);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_book, container, false);

        recyclerViewInit(view);
        event(view);

        Global.showLoading(getContext(),"generate_report");
        getTotalCountFromServer();
        getAllLibraryBooksFromServer(0);
        return view;
    }

    public void recyclerViewInit(View view) {
        // for session management.
        db = new DBController(getActivity());
        sessionManager = new SharedPreferencesManager(getActivity());

        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        moreSearchRecyclerView = view.findViewById(R.id.more_search_result);

        moreBooks = new ArrayList<>();
        adapter = new MoreBookAdapter(getActivity(), moreBooks, this, this);
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

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllLibraryBooksFromServer(0);
            }
        });
    }

    public void getTotalCountFromServer() {
        if (ConnectivityHelper.isConnectedToNetwork(getContext())) {
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

                            if (error == null) {
                                JSONObject resObj = null;
                                try {
                                    resObj = new JSONObject(result.toString());

                                    if (resObj.getString("res").toLowerCase().equals("success")) {

                                        JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<ArrayList<Book>>() {
                                        }.getType();

                                        ArrayList<Book> tempBooks = gson.fromJson(dataArray.toString(), type);
                                        totalCount = tempBooks.size();

                                    } else {
//                                        Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                            } else {
//                                Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void getAllLibraryBooksFromServer(int currentPage) {
        if (ConnectivityHelper.isConnectedToNetwork(getContext())) {
            if (currentPage == 0) {
                moreBooks = new ArrayList<>();
            }

            Map<String, List<String>> params = new HashMap<String, List<String>>();
            params.put("page", Collections.singletonList(Integer.toString(currentPage)));
            params.put("perPage", Collections.singletonList(Integer.toString(Global.perPage)));
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
                            Log.i("Mo", "result => " + result);
                            Global.hideLoading();
                            swipe_refresh_layout.setRefreshing(false);

                            if (error == null) {
                                JSONObject resObj = null;
                                try {
                                    resObj = new JSONObject(result.toString());

                                    if (resObj.getString("res").toLowerCase().equals("success")) {

                                        JSONArray dataArray = new JSONArray(resObj.getString("list"));
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<ArrayList<Book>>() {
                                        }.getType();

                                        ArrayList<Book> tempBookList = gson.fromJson(dataArray.toString(), type);
                                        moreBooks.addAll(tempBookList);

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
        } else {
            swipe_refresh_layout.setEnabled(false);
            moreSearchRecyclerView.setEnabled(true);
        }
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
    public void onAttach(@NonNull Context context) {
        mContext = context;
        mActivity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onClickBookItem(Book selectedBook, int position) {
        if (!selectedBook.bookLocalUrl.equals("")) {
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
        if (!Global.mBookFiles.containsKey(book.bookId)){
            BookFile bookFile = new BookFile(book.bookId, Url.domainUrl + book.bookNameUrl);
            Global.mBookFiles.put(book.bookId, bookFile);
            Intent intent = new Intent(mContext, DownloadingService.class);
            intent.putParcelableArrayListExtra("bookFile", new ArrayList<BookFile>(Arrays.asList(bookFile)));
            mContext.startService(intent);
        }
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

    @Override
    public void scrollToLoad(int position) {
        if (position < totalCount) {

            Global.showLoading(getContext(),"generate_report");
            getAllLibraryBooksFromServer(position / Global.perPage);
        }
    }
}
