package ours.china.hours.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.pdf.info.Urls;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DragingPopup;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalBookReadingActivity;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalModeController;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.DB.DBController;
import ours.china.hours.R;

public class TranslateDialog extends DragingPopup {
    Context context;
    SharedPreferencesManager sessionManager;
    DBController db = null;
    HorizontalModeController documentController;

    ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar MyProgressBar;
    TextView searchKeyTxt;
    WebView webviewTranslate;

    String searchString;


    public TranslateDialog(String title, FrameLayout anchor) {
        super(title, anchor);
    }

    public TranslateDialog(int titleResID, FrameLayout anchor, int width, int heigth) {
        super(titleResID, anchor, width, heigth);
    }

    public TranslateDialog(String title, FrameLayout anchor, int width, int heigth) {
        super(title, anchor, width, heigth);
    }


    public void initDialog(Context context, final HorizontalModeController controller, String text) {
        this.context = context;
        this.documentController = controller;
        this.searchString = text;
    }


    @Override
    public View getContentView(LayoutInflater inflater) {
        final View view = inflater.inflate(R.layout.tranlate_dialog, null, false);

        db = new DBController(context);
        sessionManager = new SharedPreferencesManager(context);
        MyProgressBar = (ours.china.hours.BookLib.foobnix.pdf.info.view.MyProgressBar)view.findViewById(R.id.progressBarSearch);


        initListener(view);

        return view;
    }
    private void initListener(View view){

        searchKeyTxt = view.findViewById(R.id.searchKeyTxt);
        searchKeyTxt.setText(searchString);
        webviewTranslate = view.findViewById(R.id.webviewTranslate);
//        MyProgressBar.setVisibility(View.VISIBLE);
        String translateUrl = String.format("https://fanyi.baidu.com/?aldtype=85#en/zh/%s", searchString);
        webviewTranslate.loadUrl(translateUrl);
    }

    public void onTranslate(String keyString){
        String translateUrl = String.format("https://fanyi.baidu.com/?aldtype=85#en/zh/%s", keyString);
        webviewTranslate.loadUrl(translateUrl);
    }

}
