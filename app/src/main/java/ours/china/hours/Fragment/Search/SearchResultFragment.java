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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.LibraryBookAdapter;
import ours.china.hours.Adapter.MyShelfBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Dialog.BookDetailsDialog;
import ours.china.hours.Fragment.BookTab.BookFragment;
import ours.china.hours.Fragment.FragmentUtil;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;
import ours.china.hours.Services.DownloadingService;
import ours.china.hours.Utility.SessionManager;
import pub.devrel.easypermissions.EasyPermissions;

import static ours.china.hours.Constants.ActivitiesCodes.CONNECTED_FILE_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.FINISHED_DOWNLOADING_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.PROGRESS_UPDATE_ACTION;

public class SearchResultFragment extends Fragment implements BookItemInterface, BookDetailsDialog.OnDownloadBookListenner {
    private Context mContext;
    private Activity mActivity;

    private LinearLayout mainContent, noSearchBooks;

    ArrayList<Book> mLibraryBooks;
    ArrayList<Book> myShelfBooks;

    private RecyclerView libraryRecyclerView, myShelfRecyclerView;
    private LibraryBookAdapter libraryBookAdapter;
    private MyShelfBookAdapter myShelfBookAdapter;

    TextView txtMoreSearch;
    String keyWords = "";

    SharedPreferencesManager sessionManager;
    DBController db = null;
    BookDetailsDialog bookDetailsDialog;

    public SearchResultFragment(String keywords){
        keyWords = keywords;
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
        myShelfBookAdapter.reloadbookwithDownloadStatus(Global.mBookFiles);
        libraryBookAdapter.reloadbookwithDownloadStatus(Global.mBookFiles);
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
        myShelfBookAdapter.reloadbookwithDownloadStatus(Global.mBookFiles);
        libraryBookAdapter.reloadbookwithDownloadStatus(Global.mBookFiles);
    }

    private void finishedDownload(String bookID, String path) {

        Book focusLibraryBook = new Book();
        Book focusMyShelfBook = new Book();
        int itemPosition = 0;
        for (int i = 0; i < mLibraryBooks.size(); i++) {
            Book one = mLibraryBooks.get(i);
            if (one.bookId.equals(bookID)) {
                focusLibraryBook = one;
//                itemPosition = i;
                break;
            }
        }

        for (int i = 0; i < myShelfBooks.size(); i++) {
            Book one = myShelfBooks.get(i);
            if (one.bookId.equals(bookID)) {
                focusMyShelfBook = one;
                break;
            }
        }

        if (Global.mBookFiles.containsKey(bookID)){
            Global.mBookFiles.remove(bookID);
        } else {
            int tempPosition = AppDB.get().getAll().size() - 1;
            focusLibraryBook.bookLocalUrl = path;
            focusLibraryBook.libraryPosition = String.valueOf(tempPosition);

            focusMyShelfBook.bookLocalUrl = path;
            focusMyShelfBook.libraryPosition = String.valueOf(tempPosition);
            myShelfBookAdapter.reloadBookList(myShelfBooks, keyWords);
            libraryBookAdapter.reloadBookList(mLibraryBooks);

            return;
        }

        int tempPosition = 0;
        if (AppDB.get().getAll() == null || AppDB.get().getAll().size() == 0) {
            tempPosition = 0;
        } else {
            tempPosition = AppDB.get().getAll().size();
        }

        focusLibraryBook.bookLocalUrl = path;
        focusLibraryBook.libraryPosition = String.valueOf(tempPosition);
        focusMyShelfBook.bookLocalUrl = path;
        focusMyShelfBook.libraryPosition = String.valueOf(tempPosition);

        if (db.getBookData(focusLibraryBook.bookId) == null){
            db.insertData(focusLibraryBook);
        }else{
            db.updateBookDataWithDownloadData(focusLibraryBook);
        }
        BookManagement.saveFocuseBook(focusLibraryBook, sessionManager);

        if (!ExtUtils.isExteralSD(focusLibraryBook.bookLocalUrl)) {
            FileMeta meta = AppDB.get().getOrCreate(focusLibraryBook.bookLocalUrl);
            AppDB.get().updateOrSave(meta);
            IMG.loadCoverPageWithEffect(meta.getPath(), IMG.getImageSize());
        }

        Ion.with(mContext)
                .load(Url.addToMybooks)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("bookId", focusLibraryBook.bookId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {
                        if (error == null) {
                            try {
                                JSONObject resObject = new JSONObject(result.toString());
                                if (resObject.getString("res").equals("success")||
                                        (resObject.getString("res").equals("fail") && resObject.getString("err_msg").equals("已添加"))) {

                                    myShelfBookAdapter.reloadBookList(myShelfBooks, keyWords);
                                    libraryBookAdapter.reloadBookList(mLibraryBooks);
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
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        init(view);
        event(view);

        getAllLibraryBooksFromServer();
        getAllShelfBooksFromServer();

        return view;
    }

    public void init(View rootView) {

        mainContent = rootView.findViewById(R.id.mainContent);
        noSearchBooks = rootView.findViewById(R.id.noSearchBooks);
        // for session management.
        db = new DBController(getActivity());
        sessionManager = new SharedPreferencesManager(mContext);

        mLibraryBooks = new ArrayList<>();
        myShelfBooks = new ArrayList<>();

        libraryRecyclerView = rootView.findViewById(R.id.library_search_result);
        myShelfRecyclerView = rootView.findViewById(R.id.my_bookshelf_search_result);

        libraryBookAdapter = new LibraryBookAdapter(getActivity(), mLibraryBooks, this);
        myShelfBookAdapter = new MyShelfBookAdapter(getActivity(), myShelfBooks, this);

        RecyclerView.LayoutManager libraryManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        libraryRecyclerView.setLayoutManager(libraryManager);
        libraryRecyclerView.setAdapter(libraryBookAdapter);

        RecyclerView.LayoutManager myShelfManager = new LinearLayoutManager(getActivity());
        myShelfRecyclerView.setLayoutManager(myShelfManager);
        myShelfRecyclerView.setAdapter(myShelfBookAdapter);
    }

    public void event(View view) {
        txtMoreSearch = view.findViewById(R.id.txtMoreSearch);
        txtMoreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = FragmentUtil.getFragmentByTagName(getActivity().getSupportFragmentManager(), "moreSearchFragment");
                if (fragment == null) {
                    fragment = new MoreSearchFragment(keyWords);
                }

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, fragment, "moreSearchFragment");
                transaction.addToBackStack(null);
                transaction.commit();

                FragmentUtil.printActivityFragmentList(getActivity().getSupportFragmentManager());
            }
        });
    }

    private void getAllLibraryBooksFromServer() {
        mLibraryBooks = new ArrayList<>();

        Global.showLoading(getContext(),"generate_report");
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("keyword", Collections.singletonList(keyWords));
        params.put("page", Collections.singletonList(Integer.toString(0)));
        params.put("perPage", Collections.singletonList(Integer.toString(Global.perPage)));

        Ion.with(mContext)
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
                                    mLibraryBooks = gson.fromJson(dataArray.toString(), type);
                                    getTotalData();
                                } else {
                                    Toast.makeText(mContext, "错误", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mContext, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getAllShelfBooksFromServer() {
        myShelfBooks = new ArrayList<>();

        Ion.with(mContext)
                .load(Url.searchMyBookwithMobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("statusOnly", "0")
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
                                    myShelfBooks = gson.fromJson(dataArray.toString(), type);
                                    getTotalData();

                                } else {
                                    Toast.makeText(mContext, "错误", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            Toast.makeText(mContext, "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getTotalData() {
        ArrayList<Book> books = db.getAllData();
        if (books == null) {
            libraryBookAdapter.reloadBookList(mLibraryBooks);
            myShelfBookAdapter.reloadBookList(myShelfBooks, keyWords);
            return;
        }

        if (mLibraryBooks.size() == 0 && myShelfBooks.size() == 0) {
            mainContent.setVisibility(View.GONE);
            noSearchBooks.setVisibility(View.VISIBLE);
        } else {
            mainContent.setVisibility(View.VISIBLE);
            noSearchBooks.setVisibility(View.GONE);
        }

        for (int i = 0; i < mLibraryBooks.size(); i++) {
            for (int j = 0; j < books.size(); j++) {
                Book one = mLibraryBooks.get(i);
                Book oneLocal = books.get(j);

                if (one.bookId != null && oneLocal.bookId != null && one.bookId.equals(oneLocal.bookId)) {
                    one.bookLocalUrl = oneLocal.bookLocalUrl;
                    one.bookImageLocalUrl = oneLocal.bookImageLocalUrl;
                    one.libraryPosition = oneLocal.libraryPosition;
                    break;
                }
            }
        }

        for (int i = 0; i < myShelfBooks.size(); i++) {
            for (int j = 0; j < books.size(); j++) {
                Book one = myShelfBooks.get(i);
                Book oneLocal = books.get(j);

                if (one.bookId != null && oneLocal.bookId != null && one.bookId.equals(oneLocal.bookId)) {
                    one.bookLocalUrl = oneLocal.bookLocalUrl;
                    one.bookImageLocalUrl = oneLocal.bookImageLocalUrl;
                    one.libraryPosition = oneLocal.libraryPosition;
                    break;
                }
            }
        }

        libraryBookAdapter.reloadBookList(mLibraryBooks);
        myShelfBookAdapter.reloadBookList(myShelfBooks, keyWords);
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

        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            bookDetailsDialog = new BookDetailsDialog(mContext, position, SearchResultFragment.this);
            bookDetailsDialog.show();

            // set needed frame of dialog. Without this code, all the component of the dialog's layout don't have original size.
            Window rootWindow = mActivity.getWindow();
            Rect displayRect = new Rect();
            rootWindow.getDecorView().getWindowVisibleDisplayFrame(displayRect);
            bookDetailsDialog.getWindow().setLayout(displayRect.width(), displayRect.height());
            bookDetailsDialog.setCanceledOnTouchOutside(false);
        }else{
            EasyPermissions.requestPermissions(mContext, getString(R.string.write_file), 300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
            for (int i = 0; i < mLibraryBooks.size(); i++) {
                Book one = mLibraryBooks.get(i);
                if (one.bookId != null && book.bookId != null && one.bookId.equals(book.bookId)) {
                    one.bookLocalUrl = book.bookLocalUrl;
                    one.bookImageLocalUrl = book.bookImageLocalUrl;
                    one.libraryPosition = book.libraryPosition;
                    libraryBookAdapter.notifyItemChanged(i);
                    break;
                }
            }

            for (int i = 0; i < myShelfBooks.size(); i++) {
                Book one = myShelfBooks.get(i);
                if (one.bookId != null && book.bookId != null && one.bookId.equals(book.bookId)) {
                    one.bookLocalUrl = book.bookLocalUrl;
                    one.bookImageLocalUrl = book.bookImageLocalUrl;
                    one.libraryPosition = book.libraryPosition;
//                    myShelfBookAdapter.notifyItemChanged(i);
//                    break;
                }
            }

            myShelfBookAdapter.reloadBookList(myShelfBooks, keyWords);
            libraryBookAdapter.reloadbookwithDownloadStatus(Global.mBookFiles);
        }
    }
}
