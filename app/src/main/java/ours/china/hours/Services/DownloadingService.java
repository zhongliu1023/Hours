package ours.china.hours.Services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import ours.china.hours.Activity.Global;
import ours.china.hours.Adapter.BookViewAdapterHolder;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.BookManagement;
import ours.china.hours.Management.DownloadFile;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;

import static ours.china.hours.Constants.ActivitiesCodes.CONNECTED_FILE_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.FINISHED_DOWNLOADING_ACTION;
import static ours.china.hours.Constants.ActivitiesCodes.PROGRESS_UPDATE_ACTION;

public class DownloadingService extends IntentService {
    private BaseDownloadTask mTasks = null;
    private LocalBroadcastManager mBroadcastManager;

    public DownloadingService() {
        super("DownloadingService");
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onHandleIntent(Intent intent) {
        assert intent != null;
        ArrayList<BookFile> bookFiles = intent.getParcelableArrayListExtra("bookFile");
        for (BookFile bookFile : bookFiles){
            new DownloadFile(new DownloadFile.OnDownloadStatusListenner() {
                @Override
                public void onFinishDownload(String path) {
                    finishedDownload(bookFile.getId(), path);
                }

                @Override
                public void onStartDownload() {
                    connectedFile(bookFile.getId());
                }

                @Override
                public void onUpdateProgress(int progress) {
                    publishProgress(bookFile.getId(), progress);
                }
            }).execute(bookFile.getUrl());
        }

    }

    private synchronized void connectedFile(String bookID) {
        Intent intent = new Intent();
        intent.setAction(CONNECTED_FILE_ACTION);
        intent.putExtra("bookID", bookID);
        mBroadcastManager.sendBroadcast(intent);
    }
    private synchronized void publishProgress(String bookID, int soFarBytes) {
        Intent intent = new Intent();
        intent.setAction(PROGRESS_UPDATE_ACTION);
        intent.putExtra("progress", soFarBytes);
        intent.putExtra("bookID", bookID);
        mBroadcastManager.sendBroadcast(intent);
    }
    private synchronized void finishedDownload(String bookID, String path) {

        Intent i = new Intent();
        i.setAction(FINISHED_DOWNLOADING_ACTION);
        i.putExtra("bookID", bookID);
        i.putExtra("path", path);
        mBroadcastManager.sendBroadcast(i);
    }
}