package ours.china.hours.BookLib.foobnix.pdf.info.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ours.china.hours.BookLib.foobnix.android.utils.Dips;
import ours.china.hours.BookLib.foobnix.android.utils.Keyboards;
import ours.china.hours.BookLib.foobnix.pdf.info.Urls;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.R;

public class AlertDialogs {

    public static void showResultToasts(Context c, boolean result) {
        Toast.makeText(c, result ? R.string.success : R.string.fail, Toast.LENGTH_LONG).show();
    }

    public static void openUrl(final Activity c, final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.open_web_page);
        builder.setMessage(url);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                Urls.open(c, url);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Keyboards.hideNavigation(c);
            }
        });

        create.show();
    }

    public static void showOkDialog(final Activity c, final String message, final Runnable action) {
        showOkDialog(c, message, action, null);
    }

    public static void showOkDialog(final Activity c, final String message, final Runnable action, final Runnable onDismiss) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (action != null) {
                    action.run();
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (onDismiss != null) {
                    onDismiss.run();
                }
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismiss != null) {
                    onDismiss.run();
                }
                Keyboards.hideNavigation(c);
            }
        });

        create.show();
    }

    public static void showDialog(final Activity c, final String message, String okButton, final Runnable onAction) {
        showDialog(c, message, okButton, onAction, null);
    }

    public static void showDialog(final Activity c, final String message, String okButton, final Runnable onAction, final Runnable onCancel) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(okButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (onAction != null) {
                    onAction.run();
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (onCancel != null) {
                    onCancel.run();
                }
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Keyboards.hideNavigation(c);
            }
        });

        create.show();
    }

    public static AlertDialog showViewDialog(final Activity c, final View child) {
        return showViewDialog(c, null, child);
    }

    public static AlertDialog showViewDialog(final Activity c, final Runnable ondissmiss, final View... childs) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setCancelable(true);

        ScrollView scroll = new ScrollView(c);

        LinearLayout l = new LinearLayout(c);
        l.setPadding(Dips.DP_5, Dips.DP_5, Dips.DP_5, Dips.DP_5);
        l.setOrientation(LinearLayout.VERTICAL);

        for (View child : childs) {
            l.addView(child);
        }

        scroll.addView(l);
        builder.setView(scroll);

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int id) {
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (ondissmiss != null) {
                    ondissmiss.run();
                }
                Keyboards.hideNavigation(c);
            }
        });

        create.show();
        return create;
    }
    public static void showTTSDebug(DocumentController controller){
        TextView t = new TextView(controller.getActivity());
        t.setMinWidth(Dips.DP_800);
        //t.setMinHeight(Dips.DP_800);
        String textForPage = controller.getPageHtml();
        t.setText(textForPage);
        final AlertDialog alertDialog = AlertDialogs.showViewDialog(controller.getActivity(), t);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a) {
                alertDialog.dismiss();
            }
        });
    }

}
