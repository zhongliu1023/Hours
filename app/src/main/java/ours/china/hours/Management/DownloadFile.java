package ours.china.hours.Management;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import ours.china.hours.Common.Interfaces.DownloadInterface;

public class DownloadFile  extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private Context context;
    private DownloadInterface downloadInterface;

    public DownloadFile(Context context, DownloadInterface downloadInterface) {
        this.context = context;
        this.downloadInterface = downloadInterface;
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

            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
            fileName = timestamp + "_" + fileName;
            folder = Environment.getExternalStorageDirectory() + File.separator + "androiddeft/";

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
        // dismiss the dialog after the file was downloaded
        this.progressDialog.dismiss();
        downloadInterface.onDownloadToPath(message, true);
    }
}