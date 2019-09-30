package ours.china.hours.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.ebookdroid.droids.mupdf.codec.TextWord;

import java.util.ArrayList;

import ours.china.hours.Adapter.SearchedContentAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.BaseItemLayoutAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.Keyboards;
import ours.china.hours.BookLib.foobnix.android.utils.ResultResponse;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.android.utils.Views;
import ours.china.hours.BookLib.foobnix.pdf.info.OutlineHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.PageUrl;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DragingDialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DragingPopup;
import ours.china.hours.BookLib.foobnix.pdf.info.view.EditTextHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalModeController;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.Model.SearchContent;
import ours.china.hours.R;

public class SearchContentDialog extends DragingPopup implements SearchedContentAdapter.OnClickSearchedContent {

    Context context;
    SharedPreferencesManager sessionManager;

    EditText searchEdit;
    TextView searchingMsg;
    RecyclerView gridView;
    ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar MyProgressBar;

    DBController db = null;
    OnSelectContentListener onSelectContentListener;

    HorizontalModeController documentController;
    String searchString;

    SearchedContentAdapter searchedContentAdapter;
    ArrayList<SearchContent> searchContents = new ArrayList<SearchContent>(){};
    View onSearch;

    public SearchContentDialog(String title, FrameLayout anchor) {
        super(title, anchor);
    }

    public SearchContentDialog(int titleResID, FrameLayout anchor, int width, int heigth) {
        super(titleResID, anchor, width, heigth);
    }

    public SearchContentDialog(String title, FrameLayout anchor, int width, int heigth) {
        super(title, anchor, width, heigth);
    }


    public void initDialog(Context context, OnSelectContentListener listenner, final HorizontalModeController controller, String text) {
        this.context = context;
        this.onSelectContentListener = listenner;
        this.documentController = controller;
        this.searchString = text;
    }


    @Override
    public View getContentView(LayoutInflater inflater) {
        final View view = inflater.inflate(R.layout.search_dialog, null, false);
        searchEdit = view.findViewById(R.id.edit1);
        searchEdit.setText(searchString);

        db = new DBController(context);
        sessionManager = new SharedPreferencesManager(context);
        MyProgressBar = (ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar)view.findViewById(R.id.progressBarSearch);
        searchingMsg = (TextView)view.findViewById(R.id.searching);
        gridView =  view.findViewById(R.id.grid1);
        searchedContentAdapter = new SearchedContentAdapter(context, searchContents,documentController, SearchContentDialog.this);
        gridView.setLayoutManager(new LinearLayoutManager(context));
        gridView.setAdapter(searchedContentAdapter);

        initListener(view);

        return view;
    }
    private void initListener(View view){

        ImageView onClear = (ImageView) view.findViewById(R.id.imageClear);
        onClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isRun = TempHolder.isSeaching;
                TempHolder.isSeaching = false;
                if (!isRun) {
                    searchEdit.setText("");
                    documentController.clearSelectedText();
                    searchingMsg.setVisibility(View.GONE);
                    searchContents.clear();
                    searchedContentAdapter.reloadContents(searchContents);
                }

            }
        });

        onSearch = view.findViewById(R.id.onSearch);
        TintUtil.setTintBg(onSearch);

        EditTextHelper.enableKeyboardSearch(searchEdit, new Runnable() {

            @Override
            public void run() {
                onSearch.performClick();
            }
        });
        if (TxtUtils.isNotEmpty(searchString)) {
            onSearch.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSearch.performClick();
                }
            }, 250);

        }

        final String searchingString = context.getString(R.string.searching_please_wait_);
        final int count = documentController.getPageCount();

        @SuppressLint("HandlerLeak") final Handler hMessage = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                int pageNumber = msg.what;
                MyProgressBar.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);

                if (pageNumber < -1) {
                    searchingMsg.setVisibility(View.VISIBLE);
                    searchingMsg.setText(searchingString + " " + Math.abs(pageNumber) + "/" + count);
                    return;
                }

                if (pageNumber == -1) {
                    if (searchContents.size() == 0) {
                        searchingMsg.setVisibility(View.VISIBLE);
                        searchingMsg.setText(R.string.msg_no_text_found);
                    } else {
                        searchingMsg.setVisibility(View.GONE);
                    }
                }

                if (pageNumber == Integer.MAX_VALUE) {
                    searchedContentAdapter.reloadContents(searchContents);
                    return;
                }

                if (pageNumber >= 0) {
                    pageNumber = PageUrl.realToFake(pageNumber);
                    searchingMsg.setVisibility(View.VISIBLE);
                    SearchContent searchContent = new SearchContent();
                    searchContent.currentPage = pageNumber;
                    HorizontalModeController mc = null;
                    mc = documentController;
                    mc.setCurrentPage(pageNumber);
                    OutlineHelper.Info info = OutlineHelper.getForamtingInfo(mc, false);
                    searchContent.title = info.chText;
                    TextWord[][] text = documentController.codeDocument.getPage(pageNumber).getText();
                    String bookMarkText = "";
                    int limit = 10;
                    for (TextWord[] textWords : text){
                        for (TextWord textWord : textWords){
                            limit --;
                            bookMarkText += textWord.w;
                        }
                        if (limit < 0){
                            break;
                        }
                    }
                    searchContent.content = bookMarkText+" ...";
                    searchContents.add(searchContent);
                    searchedContentAdapter.reloadContents(searchContents);
                }

            }

            ;
        };

        onSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TempHolder.isSeaching) {
                    TempHolder.isSeaching = false;
                    return;
                }
                String searchString = searchEdit.getText().toString().trim();
                if (searchString.length() < 2) {
                    Toast.makeText(documentController.getActivity(), R.string.please_enter_more_characters_to_search, Toast.LENGTH_SHORT).show();
                    return;
                }
                TempHolder.isSeaching = true;

                searchingMsg.setText(R.string.searching_please_wait_);
                searchingMsg.setVisibility(View.VISIBLE);

                MyProgressBar.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                searchContents.clear();
                searchedContentAdapter.reloadContents(searchContents);

                Keyboards.close(searchEdit);
                hMessage.removeCallbacksAndMessages(null);
                documentController.doSearch(searchString, new ResultResponse<Integer>() {
                    @Override
                    public boolean onResultRecive(final Integer pageNumber) {
                        hMessage.sendEmptyMessage(pageNumber);
                        return false;
                    }
                });
            }
        });
    }

    public void onSearch(String searchText){
        this.searchString = searchText;
        searchEdit.setText(searchText);
        onSearch.performClick();
    }

    @Override
    public void onClickContent(int pageNumber) {
        documentController.onGoToPage(pageNumber+1);
    }
    public interface OnSelectContentListener {
        void onSelectedContent(int page);
        void onDismissSearchDialog();
    }
}

