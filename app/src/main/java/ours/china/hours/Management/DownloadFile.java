package ours.china.hours.Management;

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

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.DB.DBController;
import ours.china.hours.Model.Book;

public class DownloadFile  extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private Context context;

    DBController db;

    public DownloadFile(Context context) {
        this.context = context;

        db = new DBController(context);
    }
    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
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

            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
            fileName = timestamp + "_" + fileName;
            folder = Environment.getExternalStorageDirectory() + File.separator + "book/";

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
        // setting progress percentage
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }


    @Override
    protected void onPostExecute(String message) {
        this.progressDialog.dismiss();

        Book tempBook = new Book();
        tempBook.setBookID(Global.bookID);
        tempBook.setBookName(Global.bookName);
        tempBook.setBookLocalUrl(message);
        tempBook.setBookImageLocalUrl(Global.bookImageLocalUrl);
        tempBook.setSpecifiedTime(Global.bookSpecifiedTime);

        if (message.equals("")) {
            Toast.makeText(this.context, "下载错误", Toast.LENGTH_SHORT).show();
            return;
        }

        int tempPosition;
        if (AppDB.get().getAll() == null || AppDB.get().getAll().size() == 0) {
            tempPosition = 0;
        } else {
            tempPosition = AppDB.get().getAll().size();
        }

        Log.i("HomeBookAdapter => ", "LibraryPosition =>" + tempPosition);
        tempBook.setLibraryPosition(String.valueOf(tempPosition));
        db.insertData(tempBook);

        Log.i("HomeBookAdapter => ", "message => " + message);

        Global.bookLocalUrl = message;
        Global.updateDisplayInterface.insertLocalDB(tempPosition);

        LOG.d("message == >>", message);
        if (!ExtUtils.isExteralSD(message)) {

//          message == >> |/storage/emulated/0/Librera/Downloads/William Shakespeare -  Romeo and Juliet.epub|
            FileMeta meta = AppDB.get().getOrCreate(message);
            Log.i("FileMeta", "fileMeta => " + meta);

//            meta.setIsSearchBook(true);
            AppDB.get().updateOrSave(meta);
            IMG.loadCoverPageWithEffect(meta.getPath(), IMG.getImageSize());
        }
        TempHolder.listHash++;

        Log.i("HomeBookAdapter", "Book id => " + Global.bookID);

        Ion.with(context)
                .load(Url.notifyServerBookDownLoaded)
                .setBodyParameter(Global.KEY_token, Global.access_token)
                .setBodyParameter("bookID", Global.bookID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception error, JsonObject result) {

                        if (error == null) {
                            try {
                                JSONObject resObject = new JSONObject(result.toString());
                                if (resObject.getString("res").equals("success")) {

                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
