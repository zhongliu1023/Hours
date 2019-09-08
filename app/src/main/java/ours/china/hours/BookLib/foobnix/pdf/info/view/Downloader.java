package ours.china.hours.BookLib.foobnix.pdf.info.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.ext.Fb2Extractor;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.search.view.AsyncProgressTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Downloader {

    public static void openOrDownload(final Activity a, final FileMeta meta, final Runnable onFinish) {
        String displayName = ExtUtils.getFileName(meta.getPath());


        AlertDialogs.showDialog(a, a.getString(R.string.do_you_want_to_download_the_file_) + "\n\"" + displayName + "\"", a.getString(R.string.download), new Runnable() {

            @SuppressLint("StaticFieldLeak")
            @Override
            public void run() {
                new AsyncProgressTask<Boolean>() {

                    @Override
                    public Context getContext() {
                        return a;
                    }

                    @Override
                    protected Boolean doInBackground(Object... params) {
                        try {
//                            LOG.d("Download file", meta.getPath(), path);
//                            InputStream download = Clouds.get().cloud(meta.getPath()).download(path);
//                            FileOutputStream out = new FileOutputStream(fileCache);
//                            Fb2Extractor.zipCopy(download, out);
//                            out.close();
                            return true;
                        } catch (Exception e) {
                            LOG.e(e);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        super.onPostExecute(result);
                        if (result == null) {
                            Toast.makeText(getContext(), R.string.msg_unexpected_error, Toast.LENGTH_SHORT).show();
                        } else {
                            onFinish.run();
//                            if (fileCache.isFile() && fileCache.length() > 0) {
//                                ExtUtils.openFile(a, new FileMeta(fileCache.getPath()));
//                            }
                        }
                    };

                }.execute();

            }
        });

    }

}
