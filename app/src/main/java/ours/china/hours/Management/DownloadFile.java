package ours.china.hours.Management;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.ImageListener;
import ours.china.hours.DB.DBController;
import ours.china.hours.Model.Book;

public class DownloadFile  extends AsyncTask<String, String, String> {

    private String fileName;
    private String folder;

    OnDownloadStatusListenner onDownloadStatusListenner;

    public DownloadFile(OnDownloadStatusListenner imageListener) {
        this.onDownloadStatusListenner = imageListener;

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
        onDownloadStatusListenner.onStartDownload();
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            String timestamp = Long.toString(System.currentTimeMillis());

            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
            fileName = timestamp + "_" + fileName;
            folder = Environment.getExternalStorageDirectory() + File.separator + "book/";

            Log.i("DownloadFile", "folder + fileName => " + folder + fileName);

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
        onDownloadStatusListenner.onUpdateProgress(Integer.parseInt(progress[0]));
    }


    @Override
    protected void onPostExecute(String message) {
        onDownloadStatusListenner.onFinishDownload(message);
    }


    public interface OnDownloadStatusListenner {
        void onFinishDownload(String path);
        void onStartDownload();
        void onUpdateProgress(int progress);
    }
}
