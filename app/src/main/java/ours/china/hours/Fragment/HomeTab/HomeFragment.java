package ours.china.hours.Fragment.HomeTab;

import android.Manifest;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.NewsActivity;
import ours.china.hours.Activity.SearchActivity;
import ours.china.hours.Adapter.HomeBookAdapter;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.fragment.UIFragment;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Interfaces.UpdateDisplayInterface;
import ours.china.hours.DB.DBController;
import ours.china.hours.Management.DownloadFile;
import ours.china.hours.Management.DownloadImage;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liujie on 1/12/18.
 */

public class HomeFragment extends UIFragment<FileMeta> implements BookItemInterface, UpdateDisplayInterface {

    String tempPopupWindow2String = "默认";

    ArrayList<Book> mBookList;
    HomeBookAdapter adapter;
    RelativeLayout maskLayer;

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerBooksView;
    private RelativeLayout relTypeBook;
    ImageView imgSort;
    ImageView imgSearch;
    ImageView imgArrow;
    TextView txtTypeBook;

    ImageView newsImage;

    DBController db;
    SessionManager sessionManager;

    public HomeFragment() {
        Log.v("Hello", "This is DB controller part.");
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getContext());
        getAllDataFromLocal();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);

        getAllDataFromServer(rootView, inflater);
//        getAllDataFromServer();

        return rootView;
    }

    public void init(View view) {

        Global.updateDisplayInterface = this;

        // recyclerViewWork.
        recyclerBooksView = view.findViewById(R.id.recycler_books);
        adapter = new HomeBookAdapter(mBookList, getActivity(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerBooksView.setLayoutManager(gridLayoutManager);
        recyclerBooksView.setAdapter(adapter);

//        recyclerBooksView.setLayoutManager(gridLayoutManager);
//        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.temp_spaec);

        // popupWindowWork.
        relTypeBook = view.findViewById(R.id.relTypeBook);
        imgSort = view.findViewById(R.id.imgSort);
        txtTypeBook = view.findViewById(R.id.txtTypeBook);
        maskLayer = view.findViewById(R.id.maskLayer);

        txtTypeBook.setText(getString(R.string.popup1_all, "90"));
        imgArrow = view.findViewById(R.id.imgArrow);

    }

    List<FileMeta> localBooks;
    public void getAllDataFromLocal() {
        localBooks = AppDB.get().getAll();
        Log.v("localBooks count", String.valueOf(localBooks.size()));
    }

    public void getAllDataFromServer(View rootView, LayoutInflater inflater) {
        mBookList = new ArrayList<>();

        Global.showLoading(getContext(),"generate_report");
        Ion.with(getActivity())
                .load(Url.searchAllBookwithMobile)
                .setTimeout(10000)
                .setBodyParameter(Global.KEY_token, Global.access_token)
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

                                    JSONArray dataArray = new JSONArray(resObj.getString("data"));
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject oneObject = dataArray.getJSONObject(i);

                                        Book one = new Book();
                                        one.setBookID(oneObject.getString(Global.receive_json_key_bookId));
                                        one.setBookImageUrl(Url.baseUrl + oneObject.getString(Global.receive_json_key_imageUrl));
                                        one.setBookAuthor(oneObject.getString(Global.receive_json_key_author));
                                        one.setBookName(oneObject.getString(Global.receive_json_key_bookName));
                                        one.setBookUrl(Url.baseUrl + oneObject.getString(Global.receive_json_key_bookUrl));
                                        one.setReadState(oneObject.getString(Global.receive_json_key_readState));

                                        mBookList.add(one);
                                    }

                                    getTotalData();

                                    init(rootView);
                                    popupWindowWork(inflater);
                                    event(rootView);

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

    // combine with local db data and make total data.
    public void getTotalData() {
        db = new DBController(getActivity());
        ArrayList<Book> books = db.getAllData();
        if (books == null) {
            return;
        }

        for (int i = 0; i < mBookList.size(); i++) {
            for (int j = 0; j < books.size(); j++) {
                Book one = mBookList.get(i);
                Book oneLocal = books.get(j);

                if (one.getBookID().equals(oneLocal.getBookID())) {
                    one.setBookLocalUrl(oneLocal.getBookLocalUrl());
                    one.setBookImageLocalUrl(oneLocal.getBookImageLocalUrl());
                    one.setLibraryPosition(oneLocal.getLibraryPosition());

                    break;
                }
            }
        }
    }


    @Override
    public Pair<Integer, Integer> getNameAndIconRes() {
        return null;
    }

    @Override
    public void notifyFragment() {

    }

    @Override
    public void resetFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    PopupWindow popupWindow1;
    PopupWindow popupWindow2;
    TextView txtAllBook, txtRecommended, txtNatural, txtHuman, txtLiterature, txtFollow;
    LinearLayout linAllBooks, linRecommended, linNatural, linHuman, linLiterature;
    RelativeLayout relFollow;
    LinearLayout linDefault, linRecent;
    RelativeLayout relTitle, relAuthor;
    TextView txtDefault, txtRecent, txtTitle, txtAuthor;
    ImageView imgTitle, imgAuthor;

    public void popupWindowWork(LayoutInflater inflater) {

        popupWindowInit(inflater);

        relTypeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskLayer.setVisibility(View.VISIBLE);
                allTextPopupWindow1();
                judgePopupWindow1();
                popupWindow1.showAsDropDown(view);
                imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.retract_icon));
                popupWindow2.dismiss();
            }
        });
        // for popupWindow to float
        View view1 = inflater.inflate(R.layout.popup1, null);
        TextView allBooks = view1.findViewById(R.id.txtAllBooks);
        allBooks.setText("全都（" + 1000 + ")");
        final PopupWindow popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        relTypeBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow1.showAsDropDown(view);
//            }
//        });

        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTextImagePopupWindow2();
                judgePopupWindow2();
                popupWindow2.showAsDropDown(view);
                maskLayer.setVisibility(View.VISIBLE);
                popupWindow1.dismiss();
            }
        });

        // for event when we pressed item in popupWindow.
        linAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("全都(1000)");
                dismissPopupWindow1();
            }
        });

        linRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("推荐(5)");
                dismissPopupWindow1();
            }
        });

        linNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("自然科学(9)");
                dismissPopupWindow1();
            }
        });

        linHuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("人文社科(2)");
                dismissPopupWindow1();
            }
        });

        linLiterature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("文学艺术(7)");
                dismissPopupWindow1();
            }
        });

        relFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTypeBook.setText("关注(5)");
                maskLayer.setVisibility(View.GONE);
                dismissPopupWindow1();
            }
        });

        linDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "默认";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();
            }
        });

        linRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "最近";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();
            }
        });

        relTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "标题";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();
            }
        });

        relAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempPopupWindow2String = "作者";
                maskLayer.setVisibility(View.GONE);
                popupWindow2.dismiss();
            }
        });

        maskLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPopupWindow1();
                popupWindow2.dismiss();
            }
        });

    }

    public void dismissPopupWindow1() {
        popupWindow1.dismiss();
        maskLayer.setVisibility(View.GONE);
        imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.pulldown_icon));
    }

    public void allTextPopupWindow1() {
        txtAllBook.setTextColor(getResources().getColor(R.color.alpa_90));
        txtRecommended.setTextColor(getResources().getColor(R.color.alpa_90));
        txtNatural.setTextColor(getResources().getColor(R.color.alpa_90));
        txtHuman.setTextColor(getResources().getColor(R.color.alpa_90));
        txtLiterature.setTextColor(getResources().getColor(R.color.alpa_90));
        txtFollow.setTextColor(getResources().getColor(R.color.alpa_90));
    }

    public void allTextImagePopupWindow2() {
        txtDefault.setTextColor(getResources().getColor(R.color.alpa_90));
        txtRecent.setTextColor(getResources().getColor(R.color.alpa_90));
        txtTitle.setTextColor(getResources().getColor(R.color.alpa_90));
        txtAuthor.setTextColor(getResources().getColor(R.color.alpa_90));

        imgTitle.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.positive_sequence_icon)));
        imgAuthor.setImageDrawable(changeImageColor(getResources().getColor(R.color.alpa_90), getResources().getDrawable(R.drawable.reverse_order_icon)));
    }

    public void popupWindowInit(LayoutInflater inflater) {

        // for popupWindow 1
        View view1 = inflater.inflate(R.layout.popup1, null);

        txtAllBook = view1.findViewById(R.id.txtAllBooks);
        txtRecommended = view1.findViewById(R.id.txtRecommended);
        txtNatural = view1.findViewById(R.id.txtNatural);
        txtHuman = view1.findViewById(R.id.txtHuman);
        txtLiterature = view1.findViewById(R.id.txtLiterature);
        txtFollow = view1.findViewById(R.id.txtFollow);

        linAllBooks = view1.findViewById(R.id.linAllBooks);
        linRecommended = view1.findViewById(R.id.linRecommended);
        linNatural = view1.findViewById(R.id.linNatural);
        linHuman = view1.findViewById(R.id.linHuman);
        linLiterature = view1.findViewById(R.id.linLiterature);
        relFollow = view1.findViewById(R.id.relFollow);

        popupWindow1 = new PopupWindow(view1, 180, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow1.setAnimationStyle(R.style.popupwindowAnimation);

        // for popupWindow 2
        View view2 = inflater.inflate(R.layout.popup2, null);

        linDefault = view2.findViewById(R.id.linDefault);
        linRecent = view2.findViewById(R.id.linRecent);
        relTitle = view2.findViewById(R.id.relTitle);
        relAuthor = view2.findViewById(R.id.relAuthor);

        txtDefault = view2.findViewById(R.id.txtDefault);
        txtRecent = view2.findViewById(R.id.txtRecent);
        txtTitle = view2.findViewById(R.id.txtTitle);
        txtAuthor = view2.findViewById(R.id.txtAuthor);

        imgTitle = view2.findViewById(R.id.imgTitle);
        imgAuthor = view2.findViewById(R.id.imgAuthor);

        popupWindow2 = new PopupWindow(view2, 180, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow2.setAnimationStyle(R.style.popupwindowAnimation);
    }

    public void judgePopupWindow1() {

        String tempStr = txtTypeBook.getText().toString();
        if (tempStr.contains("全都")) {
            allTextPopupWindow1();
            txtAllBook.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("推荐")) {
            allTextPopupWindow1();
            txtRecommended.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("自然科学")) {
            allTextPopupWindow1();
            txtNatural.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("人文社科")) {
            allTextPopupWindow1();
            txtHuman.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("文学艺术")) {
            allTextPopupWindow1();
            txtLiterature.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempStr.contains("关注")) {
            allTextPopupWindow1();
            txtFollow.setTextColor(getResources().getColor(R.color.pink));
        }
    }

    public void judgePopupWindow2() {
        if (tempPopupWindow2String.equals("默认")) {
            allTextImagePopupWindow2();
            txtDefault.setTextColor(getResources().getColor(R.color.pink));
        } else if (tempPopupWindow2String.equals("最近")) {
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

    public void event(View rootView) {

        imgSearch = rootView.findViewById(R.id.search_image);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        newsImage = rootView.findViewById(R.id.imageNews);
        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                startActivity(intent);
            }
        });
    }
    private Drawable changeImageColor(int color, Drawable mDrawable){
        Drawable changedDrawable = mDrawable;
        changedDrawable.setColorFilter(new
                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return changedDrawable;
    }

    @Override
    public void onClickBookItem(String uri) {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new DownloadImage(getActivity()).execute(uri);
        }else{
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.write_file), 300, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void insertLocalDB(int libraryPosition) {
        Log.i("HomeBookAdapter", "bookDownloadedPosition => " + Global.bookDownloadedPosition);
        Log.i("HomeBookAdapter", "bookLocalUrl => " + Global.bookLocalUrl);
        Log.i("HomeBookAdapter", "bookImageLocalUrl => " + Global.bookImageLocalUrl);

        mBookList.get(Global.bookDownloadedPosition).setBookLocalUrl(Global.bookLocalUrl);
        mBookList.get(Global.bookDownloadedPosition).setBookImageLocalUrl(Global.bookImageLocalUrl);
        mBookList.get(Global.bookDownloadedPosition).setLibraryPosition(String.valueOf(libraryPosition));

        adapter.notifyItemChanged(Global.bookDownloadedPosition);
    }
}