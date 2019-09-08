package ours.china.hours.BookLib.foobnix.ui2;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ours.china.hours.BookLib.foobnix.android.utils.Apps;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.ext.CacheZipUtils.CacheDir;
import ours.china.hours.BookLib.foobnix.ext.EbookMeta;
import ours.china.hours.BookLib.foobnix.model.AppData;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.model.SimpleMeta;
import ours.china.hours.BookLib.foobnix.model.TagData;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.info.io.SearchCore;
import ours.china.hours.BookLib.foobnix.pdf.info.model.BookCSS;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessageSync;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.MessageSyncFinish;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.UpdateAllFragments;
import ours.china.hours.BookLib.foobnix.sys.ImageExtractor;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.tts.TTSNotification;

import org.ebookdroid.common.settings.books.SharedBooks;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class BooksService extends IntentService {
    private MediaSessionCompat mediaSessionCompat;

    public BooksService() {
        super("BooksService");
        handler = new Handler();
        LOG.d("BooksService", "Create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStartForeground = false;
        LOG.d("BooksService", "onDestroy");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //startMyForeground();
    }

    public static String TAG = "BooksService";

    Handler handler;

    public static String INTENT_NAME = "BooksServiceIntent";
    public static String ACTION_SEARCH_ALL = "ACTION_SEARCH_ALL";
    public static String ACTION_REMOVE_DELETED = "ACTION_REMOVE_DELETED";
    public static String ACTION_SYNC_DROPBOX = "ACTION_SYNC_DROPBOX";
    public static String ACTION_RUN_SYNCRONICATION = "ACTION_RUN_SYNCRONICATION";

    public static String RESULT_SYNC_FINISH = "RESULT_SYNC_FINISH";
    public static String RESULT_SEARCH_FINISH = "RESULT_SEARCH_FINISH";
    public static String RESULT_BUILD_LIBRARY = "RESULT_BUILD_LIBRARY";
    public static String RESULT_SEARCH_COUNT = "RESULT_SEARCH_COUNT";

    private List<FileMeta> itemsMeta = new LinkedList<FileMeta>();

    public static volatile boolean isRunning = false;

    boolean isStartForeground = false;

    public void startMyForeground() {
        if (!isStartForeground) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Notification notification = new NotificationCompat.Builder(this, TTSNotification.DEFAULT) //
                        .setSmallIcon(R.drawable.glyphicons_748_synchronization1) //
                        .setContentTitle(Apps.getApplicationName(this)) //
                        .setContentText(getString(R.string.please_wait_books_are_being_processed_))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)//
                        .build();

                startForeground(TTSNotification.NOT_ID_2, notification);
            }
            AppProfile.init(this);
            isStartForeground = true;
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        //startMyForeground();


        if (intent == null) {
            return;
        }

        try {
            if (isRunning) {
                LOG.d(TAG, "BooksService", "Is-running");
                return;
            }

            isRunning = true;
            LOG.d(TAG, "BooksService", "Action", intent.getAction());

            //TESET


            if (ACTION_RUN_SYNCRONICATION.equals(intent.getAction())) {
                if (AppTemp.get().isEnableSync) {


                    AppProfile.save(this);


                    try {
                        EventBus.getDefault().post(new MessageSync(MessageSync.STATE_VISIBLE));
                        AppTemp.get().syncTimeStatus = MessageSync.STATE_VISIBLE;

                        AppTemp.get().syncTime = System.currentTimeMillis();
                        AppTemp.get().syncTimeStatus = MessageSync.STATE_SUCCESS;
                        EventBus.getDefault().post(new MessageSync(MessageSync.STATE_SUCCESS));
                    }catch (Exception e) {
                        AppTemp.get().syncTimeStatus = MessageSync.STATE_FAILE;
                        EventBus.getDefault().post(new MessageSync(MessageSync.STATE_FAILE));
                        LOG.e(e);
                    }


                }

            }


            if (ACTION_REMOVE_DELETED.equals(intent.getAction())) {
                List<FileMeta> all = AppDB.get().getAll();

                for (FileMeta meta : all) {
                    if (meta == null) {
                        continue;
                    }

                    File bookFile = new File(meta.getPath());
                    if (ExtUtils.isMounted(bookFile)) {
                        if (!bookFile.exists()) {
                            AppDB.get().delete(meta);
                            LOG.d("BooksService Delete-setIsSearchBook", meta.getPath());
                        }
                    }

                }

                List<FileMeta> localMeta = new LinkedList<FileMeta>();

                for (final String path : BookCSS.get().searchPaths.split(",")) {
                    if (path != null && path.trim().length() > 0) {
                        final File root = new File(path);
                        if (root.isDirectory()) {
                            LOG.d(TAG, "Search in " + root.getPath());
                            SearchCore.search(localMeta, root, ExtUtils.seachExts);
                        }
                    }
                }


                for (FileMeta meta : localMeta) {
                    if (!all.contains(meta)) {
                        FileMetaCore.createMetaIfNeed(meta.getPath(), true);
                        LOG.d("BooksService add book", meta.getPath());
                    }
                }

                sendFinishMessage();


            } else if (ACTION_SEARCH_ALL.equals(intent.getAction())) {
                LOG.d(ACTION_SEARCH_ALL);

                AppProfile.init(this);

                IMG.clearDiscCache();
                IMG.clearMemoryCache();
                ImageExtractor.clearErrors();


                AppDB.get().deleteAllData();
                itemsMeta.clear();

                handler.post(timer);


                for (final String path : BookCSS.get().searchPaths.split(",")) {
                    if (path != null && path.trim().length() > 0) {
                        final File root = new File(path);
                        if (root.isDirectory()) {
                            LOG.d("Searcin in " + root.getPath());
                            SearchCore.search(itemsMeta, root, ExtUtils.seachExts);
                        }
                    }
                }


                for (FileMeta meta : itemsMeta) {
                    meta.setIsSearchBook(true);
                }

                final List<SimpleMeta> allExcluded = AppData.get().getAllExcluded();

                if (TxtUtils.isListNotEmpty(allExcluded)) {
                    for (FileMeta meta : itemsMeta) {
                        if (allExcluded.contains(SimpleMeta.SyncSimpleMeta(meta.getPath()))) {
                            meta.setIsSearchBook(false);
                        }
                    }
                }

                final List<FileMeta> allSyncBooks = AppData.get().getAllSyncBooks();
                if (TxtUtils.isListNotEmpty(allSyncBooks)) {
                    for (FileMeta meta : itemsMeta) {
                        for (FileMeta sync : allSyncBooks) {
                            if (meta.getTitle().equals(sync.getTitle()) && !meta.getPath().equals(sync.getPath())) {
                                meta.setIsSearchBook(false);
                                LOG.d(TAG, "remove-dublicate", meta.getPath());
                            }
                        }

                    }
                }


                itemsMeta.addAll(AppData.get().getAllFavoriteFiles());
                itemsMeta.addAll(AppData.get().getAllFavoriteFolders());


                AppDB.get().saveAll(itemsMeta);

                handler.removeCallbacks(timer);

                sendFinishMessage();

                handler.post(timer2);

                for (FileMeta meta : itemsMeta) {
                    File file = new File(meta.getPath());
                    FileMetaCore.get().upadteBasicMeta(meta, file);
                }

                AppDB.get().updateAll(itemsMeta);
                sendFinishMessage();

                for (FileMeta meta : itemsMeta) {
                    EbookMeta ebookMeta = FileMetaCore.get().getEbookMeta(meta.getPath(), CacheDir.ZipService, true);
                    LOG.d("BooksService getAuthor", ebookMeta.getAuthor());
                    FileMetaCore.get().udpateFullMeta(meta, ebookMeta);
                }

                SharedBooks.updateProgress(itemsMeta, true);
                AppDB.get().updateAll(itemsMeta);


                itemsMeta.clear();

                handler.removeCallbacks(timer2);
                sendFinishMessage();
                CacheDir.ZipService.removeCacheContent();


                TagData.restoreTags();

                sendFinishMessage();

            } else if (ACTION_SYNC_DROPBOX.equals(intent.getAction())) {
                sendFinishMessage();
            }


        } finally {
            isRunning = false;
        }
        //stopSelf();
    }

    Runnable timer = new Runnable() {

        @Override
        public void run() {
            sendProggressMessage();
            handler.postDelayed(timer, 250);
        }
    };

    Runnable timer2 = new Runnable() {

        @Override
        public void run() {
            sendBuildingLibrary();
            handler.postDelayed(timer2, 250);
        }
    };

    private void sendFinishMessage() {
        try {
            //AppDB.get().getDao().detachAll();
        } catch (Exception e) {
            LOG.e(e);
        }

        sendFinishMessage(this);
        EventBus.getDefault().post(new MessageSyncFinish());
    }

    public static void sendFinishMessage(Context c) {
        Intent intent = new Intent(INTENT_NAME).putExtra(Intent.EXTRA_TEXT, RESULT_SEARCH_FINISH);
        LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
    }

    private void sendProggressMessage() {
        Intent itent = new Intent(INTENT_NAME).putExtra(Intent.EXTRA_TEXT, RESULT_SEARCH_COUNT).putExtra(Intent.EXTRA_INDEX, itemsMeta.size());
        LocalBroadcastManager.getInstance(this).sendBroadcast(itent);
    }

    private void sendBuildingLibrary() {
        Intent itent = new Intent(INTENT_NAME).putExtra(Intent.EXTRA_TEXT, RESULT_BUILD_LIBRARY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(itent);
    }

    public static void startForeground(Activity a, String action) {
        final Intent intent = new Intent(a, BooksService.class).setAction(action);
        a.startService(intent);

//        if (Build.VERSION.SDK_INT >= 26) {
//            a.startForegroundService(intent);
//        } else {
//            a.startService(intent);
//
//        }

    }

}
