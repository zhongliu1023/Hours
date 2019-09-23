package ours.china.hours.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import java.util.ArrayList;

import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalModeController;
import ours.china.hours.Common.Interfaces.UpdateDisplayInterface;
import ours.china.hours.Dialog.AlertDlg;
import ours.china.hours.Dialog.OutDlg;

import ours.china.hours.Model.QueryBook;
import ours.china.hours.Model.User;
import ours.china.hours.R;

public class Global {

    public static User currentUser = new User();
    public static String fullFavorites = "";

    public static QueryBook.BookAction bookAction = QueryBook.BookAction.NONE;

    boolean isShowingSpinner = false;
    public static String registeredFacePath = "";
    public static String canGetFaceFeature = "no";
    public static String faceFeatureData = "";
    public static String faceImageLocalUrl = "";

    public static String editStateOfFavorite = "no";
    public static String editStateOfFavoritesDetails = "no";

    public static String mobile = "";
    public static String password = "";

    public static String SearchQuery = "";

    public static String faceState = "";
    public static String identify = "";

    /** when login , return key-> value*/
    public static String access_token = "";
    public static String refresh_token = "";

    public static String KEY_token = "access_token";
    public static String KEY_refresh_token = "";

    public static String expires_in = "";
    public static String token_type = "";
    public static String scope = "userinfo cloud file node";
    public static final int PICK_IMAGE_REQUEST = 20;
    public static final int REQUEST_TAKE_PHOTO_FACE = 1;
    public static final int REQUEST_TAKE_PHOTO_BACK = 2;
    public static final int REQUEST_GALLERY_PHOTO_FACE = 3;
    public static final int REQUEST_GALLERY_PHOTO_BACK = 4;

    public static String receive_json_key_bookId = "bookId";
    public static String receive_json_key_imageUrl = "coverUrl";
    public static String receive_json_key_bookName = "bookName";
    public static String receive_json_key_bookUrl = "bookNameUrl";
    public static String receive_json_key_author = "publisher";
    public static String receive_json_key_introduce = "introduce";
    public static String receive_json_key_readState = "isbn";

    public static String faceFeatureSavedUrl = "";
    public static String faceFeatureSavedImageUrl = "";
    public static String faceHash = "";

    public static int bookDownloadedPosition = 0;
    public static String readingNowOrHistory = "";

    public static UpdateDisplayInterface updateDisplayInterface;
    public static HorizontalModeController globalDC;
    public static ArrayList<AppBookmark> objects = new ArrayList<>();


    public static void alert(Context context, String title, String message, String btnTitle) {

        AlertDlg dlg = new AlertDlg(context, R.style.AppTheme_Alert);
        dlg.txtTitle.setText(title);
        dlg.txtMessage.setText(message);
        dlg.btnOK.setText(btnTitle);

        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.show();
    }

//
//    public static void PersonDlg(Context context, String title){
//
//        OutDlg outDlg = new OutDlg(context, R.style.AppTheme_Alert);
//        outDlg.tvOutTitle.setText(title);
//        outDlg.show();
//    }


    public static ProgressDialog mProgressDialog;
    public static void showLoading(Context context, String title)
    {
        if (mProgressDialog != null){return;}
        String strPleaseWaitAwhile = "请秒后";
        mProgressDialog = new ProgressDialog(context, R.style.DialogTheme);
        mProgressDialog.setMessage(strPleaseWaitAwhile);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.diaolog_loading);
        TextView tvLoadingMsg = (TextView) mProgressDialog.findViewById(R.id.loading_msg);
        tvLoadingMsg.setText(strPleaseWaitAwhile);

    }


    public static void hideLoading()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
