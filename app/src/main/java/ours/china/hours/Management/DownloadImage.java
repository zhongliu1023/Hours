package ours.china.hours.Management;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.DB.DBController;
import ours.china.hours.Model.Book;

public class DownloadImage extends AsyncTask<String, String, String> {

    private String fileName;
    private String folder;
    private Context context;

    public DownloadImage(Context context) {
        this.context = context;
    }
    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
            fileName = timestamp + "_" + fileName;
            folder = Environment.getExternalStorageDirectory() + File.separator + "image/";

            File directory = new File(folder);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            OutputStream output = new FileOutputStream(folder + fileName);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            return folder + fileName;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return "";
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {

    }


    @Override
    protected void onPostExecute(String message) {
        Global.bookImageLocalUrl = message;

        Log.i("HomeBookAdapter", "downloaded image url => " + message);
        Log.i("HomeBookAdapter", "downloaded book url => " + Global.bookUrl);

        new DownloadFile(context).execute(Global.bookUrl);

    }
}
