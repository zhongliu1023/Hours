package org.emdev.ui.tasks;

import android.app.AlertDialog;
import android.content.Context;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.pdf.CopyAsyncTask;
import ours.china.hours.BookLib.foobnix.pdf.info.view.Dialogs;

public abstract class BaseAsyncTask<Params, Result> extends CopyAsyncTask<Params, String, Result> {

    protected final Context context;
    protected AlertDialog progressDialog;

    public BaseAsyncTask(Context context) {
        this.context = context;
    }

    public void onBookCancel() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            LOG.e(e);
        }

    }

    @Override
    protected void onPreExecute() {
        progressDialog = Dialogs.loadingBook(context, new Runnable() {

            @Override
            public void run() {
                onBookCancel();
            }
        });
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
        }
    }

}
