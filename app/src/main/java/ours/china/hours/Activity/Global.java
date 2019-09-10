package ours.china.hours.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import ours.china.hours.Dialog.AlertDlg;
<<<<<<< HEAD
import ours.china.hours.Dialog.OutDlg;
import ours.china.hours.MainApplication;
import ours.china.hours.R;

public class Global {

    public static MainApplication mainApplication = MainApplication.getInstance();

    public static String registeredFacePath = "";
    public static String canGetFaceFeature = "no";
    public static String faceFeatureData = "";
    public static String faceImageLocalUrl = "";

    public static String editStateOfFavorite = "no";
    public static String editStateOfFavoritesDetails = "no";

    public static String mobile = "";
    public static String identify = "1";
    public static String faceState = "1";

    /** when login , return key-> value*/
    public static String token = "";
    public static String refresh_token = "";
    public static String expires_in = "";
    public static String token_type = "";
    public static String scope = "userinfo cloud file node";
    public static final int PICK_IMAGE_REQUEST = 20;
    public static final int REQUEST_TAKE_PHOTO_FACE = 1;
    public static final int REQUEST_TAKE_PHOTO_BACK = 2;
    public static final int REQUEST_GALLERY_PHOTO_FACE = 3;
    public static final int REQUEST_GALLERY_PHOTO_BACK = 4;


    public static void alert(Context context, String title, String message, String btnTitle) {

        AlertDlg dlg = new AlertDlg(context, R.style.AppTheme_Alert);
        dlg.txtTitle.setText(title);
        dlg.txtMessage.setText(message);
        dlg.btnOK.setText(btnTitle);
        dlg.show();
    }


    public static void PersonDlg(Context context, String title){

        OutDlg outDlg = new OutDlg(context, R.style.AppTheme_Alert);
        outDlg.tvOutTitle.setText(title);
        outDlg.show();
    }


    public static ProgressDialog mProgressDialog;
    public static void showLoading(Context context, String title)
    {
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
