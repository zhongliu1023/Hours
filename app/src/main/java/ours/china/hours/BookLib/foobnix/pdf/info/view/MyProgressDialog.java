package ours.china.hours.BookLib.foobnix.pdf.info.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.Objects;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;


public class MyProgressDialog {

    static Handler handler = new Handler(Looper.getMainLooper());

    public static ProgressDialog show(Context c, String subtitile) {
        final ProgressDialog dialog = android.app.ProgressDialog.show(c, "", subtitile);

        try {
            android.widget.ProgressBar pr = (android.widget.ProgressBar) Objects.getInstanceValue(dialog, "mProgress");
            pr.setSaveEnabled(false);
            TintUtil.setDrawableTint(pr.getIndeterminateDrawable().getCurrent(), AppState.get().isDayNotInvert ? TintUtil.color : Color.WHITE);
        } catch (Exception e) {
            LOG.e(e);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    LOG.e(e);
                }
            }
        }, 30 * 1000);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog1) {
                handler.removeCallbacksAndMessages(null);
            }
        });


        return dialog;
    }
}
