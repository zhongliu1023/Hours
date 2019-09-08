package ours.china.hours.BookLib.foobnix.ui2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.ResultResponse;
import ours.china.hours.BookLib.foobnix.android.utils.ResultResponse2;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.model.AppData;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.model.SimpleMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.pdf.info.Playlists;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.info.view.Dialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DialogsPlaylist;
import ours.china.hours.BookLib.foobnix.pdf.info.view.Downloader;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.FileInformationDialog;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.RecentUpates;
import ours.china.hours.BookLib.foobnix.pdf.info.widget.ShareDialog;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.UITab;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.NotifyAllFragments;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.OpenDirMessage;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.OpenTagMessage;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.MainTabs2;
import ours.china.hours.BookLib.foobnix.ui2.fragment.UIFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class DefaultListeners {

    public static void bindAdapter(final Activity a, final FileMetaAdapter searchAdapter, final DocumentController dc, final Runnable onClick) {
        searchAdapter.setOnItemClickListener(new ResultResponse<FileMeta>() {

            @Override
            public boolean onResultRecive(final FileMeta result) {
                onClick.run();
                dc.onCloseActivityFinal(new Runnable() {

                    @Override
                    public void run() {
                        ExtUtils.showDocumentWithoutDialog(a, new File(result.getPath()), null);

                    }
                });
                return false;
            }

        });
        searchAdapter.setOnItemLongClickListener(getOnItemLongClickListener(a, searchAdapter));
        searchAdapter.setOnMenuClickListener(getOnMenuClick(a, searchAdapter));
        searchAdapter.setOnStarClickListener(getOnStarClick(a));
    }

    public static void bindAdapter(final Activity a, final FileMetaAdapter searchAdapter) {
        searchAdapter.setOnItemClickListener(getOnItemClickListener(a));
        searchAdapter.setOnItemLongClickListener(getOnItemLongClickListener(a, searchAdapter));
        searchAdapter.setOnMenuClickListener(getOnMenuClick(a, searchAdapter));
        searchAdapter.setOnStarClickListener(getOnStarClick(a));
        searchAdapter.setOnTagClickListner(new ResultResponse<String>() {

            @Override
            public boolean onResultRecive(String result) {
                showBooksByTag(a, result);
                return false;
            }

        });
    }

    public static void showBooksByTag(final Activity a, String result) {
        Intent intent = new Intent(UIFragment.INTENT_TINT_CHANGE)//
                .putExtra(MainTabs2.EXTRA_PAGE_NUMBER, UITab.getCurrentTabIndex(UITab.SearchFragment));//
        LocalBroadcastManager.getInstance(a).sendBroadcast(intent);

        EventBus.getDefault().post(new OpenTagMessage(result));
    }

    public static void bindAdapterAuthorSerias(Activity a, final FileMetaAdapter searchAdapter) {
        searchAdapter.setOnAuthorClickListener(getOnAuthorClickListener(a));
        searchAdapter.setOnSeriesClickListener(getOnSeriesClickListener(a));

    }

    public static ResultResponse<FileMeta> getOnItemClickListener(final Activity a) {
        return new ResultResponse<FileMeta>() {

            @Override
            public boolean onResultRecive(final FileMeta result) {

                if (false) {
                    Dialogs.testWebView(a, result.getPath());
                    return false;
                }

                if (result.getPath().endsWith(Playlists.L_PLAYLIST)) {
                    DialogsPlaylist.showPlayList(a, result.getPath(), null);
                    return true;
                }

                boolean isFolder = AppDB.get().isFolder(result);

                if (!isFolder) {

                    Downloader.openOrDownload(a, result, new Runnable() {

                        @Override
                        public void run() {
                            IMG.clearCache(result.getPath());

                            Intent intent = new Intent(UIFragment.INTENT_TINT_CHANGE)//
                                    .putExtra(MainTabs2.EXTRA_NOTIFY_REFRESH, true)//
                                    .putExtra(MainTabs2.EXTRA_PAGE_NUMBER, -2);//
                            LocalBroadcastManager.getInstance(a).sendBroadcast(intent);
                        }
                    });

                    return false;
                }

                if (isTagCicked(a, result)) {
                    return true;
                }

                // final File item = new File(result.getPath());
                if (isFolder) {
                    final int currentTabIndex = UITab.getCurrentTabIndex(UITab.BrowseFragment);
                    Intent intent = new Intent(UIFragment.INTENT_TINT_CHANGE)//

                            .putExtra(MainTabs2.EXTRA_PAGE_NUMBER, currentTabIndex);//

                    LOG.d("EXTRA_PAGE_NUMBER", AppState.get().tabsOrder7, currentTabIndex);

                    LocalBroadcastManager.getInstance(a).sendBroadcast(intent);

                    EventBus.getDefault().post(new OpenDirMessage(result.getPath()));

                } else {
                    if (AppTemp.get().readingMode == AppState.READING_MODE_TAG_MANAGER && !ExtUtils.isExteralSD(result.getPath())) {
                        Dialogs.showTagsDialog(a, new File(result.getPath()), true, new Runnable() {

                            @Override
                            public void run() {
                                EventBus.getDefault().post(new NotifyAllFragments());
                            }
                        });
                    } else {
                        ExtUtils.openFile(a, result);
                    }
                }
                return false;
            }

        };
    }

    ;

    private static boolean isTagCicked(final Activity a, FileMeta result) {
        if (result.getCusType() != null && result.getCusType() == FileMetaAdapter.DISPALY_TYPE_LAYOUT_TAG) {
            showBooksByTag(a, result.getPathTxt());
            return true;
        }
        return false;
    }

    public static ResultResponse<FileMeta> getOnItemLongClickListener(final Activity a, final FileMetaAdapter searchAdapter) {
        return new ResultResponse<FileMeta>() {

            @Override
            public boolean onResultRecive(final FileMeta result) {
                LOG.d("getOnItemLongClickListener");

                if (result.getPath().endsWith(Playlists.L_PLAYLIST)) {
                    ExtUtils.openFile(a, result);
                    return false;
                }

                if (ExtUtils.isExteralSD(result.getPath())) {
                    return false;
                }

                if (isTagCicked(a, result)) {
                    return true;
                }

                File file = new File(result.getPath());

                if (file.isDirectory()) {
                    Intent intent = new Intent(UIFragment.INTENT_TINT_CHANGE)//
                            .putExtra(MainTabs2.EXTRA_PAGE_NUMBER, UITab.getCurrentTabIndex(UITab.BrowseFragment));//
                    LocalBroadcastManager.getInstance(a).sendBroadcast(intent);

                    EventBus.getDefault().post(new OpenDirMessage(result.getPath()));
                    return true;
                }

                Runnable onDeleteAction = new Runnable() {

                    @Override
                    public void run() {
                        deleteFile(a, searchAdapter, result);
                    }

                };
                if (ExtUtils.doifFileExists(a, file)) {
                    FileInformationDialog.showFileInfoDialog(a, file, onDeleteAction);
                }
                return true;
            }
        };
    }

    ;

    @SuppressLint("NewApi")
    private static void deleteFile(final Activity a, final FileMetaAdapter searchAdapter, final FileMeta result) {
        boolean delete = false;
        if (ExtUtils.isExteralSD(result.getPath())) {
            DocumentFile doc = DocumentFile.fromSingleUri(a, Uri.parse(result.getPath()));
            delete = doc.delete();
        }else {
            final File file = new File(result.getPath());
            delete = file.delete();
        }

        LOG.d("Delete-file", result.getPath(), delete);

        if (delete) {
            TempHolder.listHash++;
            AppDB.get().delete(result);
            searchAdapter.getItemsList().remove(result);
            searchAdapter.notifyDataSetChanged();

        } else {

        }
    }



    public static ResultResponse<FileMeta> getOnMenuClick(final Activity a, final FileMetaAdapter searchAdapter) {
        return new ResultResponse<FileMeta>() {

            @Override
            public boolean onResultRecive(final FileMeta result) {

                File file = new File(result.getPath());

                Runnable onDeleteAction = new Runnable() {

                    @Override
                    public void run() {
                        deleteFile(a, searchAdapter, result);
                    }

                };

                if (ExtUtils.isExteralSD(result.getPath())) {
                    ShareDialog.show(a, file, onDeleteAction, -1, null, null);
                } else {

                    if (ExtUtils.doifFileExists(a, result.getPath())) {

                        if (ExtUtils.isNotSupportedFile(file)) {
                            ShareDialog.showArchive(a, file, onDeleteAction);
                        } else {
                            ShareDialog.show(a, file, onDeleteAction, -1, null, null);
                        }
                    }
                }

                return false;
            }
        };
    }

    ;

    public static ResultResponse<String> getOnAuthorClickListener(final Activity a) {
        return new ResultResponse<String>() {

            @Override
            public boolean onResultRecive(String result) {

                result = AppDB.SEARCH_IN.AUTHOR.getDotPrefix() + " " + result;

                Intent intent = new Intent(UIFragment.INTENT_TINT_CHANGE)//
                        .putExtra(MainTabs2.EXTRA_SEACH_TEXT, result)//
                        .putExtra(MainTabs2.EXTRA_PAGE_NUMBER, UITab.getCurrentTabIndex(UITab.SearchFragment));//

                LocalBroadcastManager.getInstance(a).sendBroadcast(intent);
                return false;
            }
        };
    }

    public static ResultResponse<String> getOnSeriesClickListener(final Activity a) {
        return new ResultResponse<String>() {

            @Override
            public boolean onResultRecive(String result) {

                result = AppDB.SEARCH_IN.SERIES.getDotPrefix() + " " + result;

                Intent intent = new Intent(UIFragment.INTENT_TINT_CHANGE)//
                        .putExtra(MainTabs2.EXTRA_SEACH_TEXT, result)//
                        .putExtra(MainTabs2.EXTRA_PAGE_NUMBER, UITab.getCurrentTabIndex(UITab.SearchFragment));//

                LocalBroadcastManager.getInstance(a).sendBroadcast(intent);
                return false;
            }
        };
    }

    public static ResultResponse2<FileMeta, FileMetaAdapter> getOnStarClick(final Activity a) {
        return new ResultResponse2<FileMeta, FileMetaAdapter>() {

            @Override
            public boolean onResultRecive(FileMeta fileMeta, FileMetaAdapter adapter) {
                Boolean isStar = fileMeta.getIsStar();

                if (isStar == null) {
                    isStar = AppDB.get().isStarFolder(fileMeta.getPath());

                } else {

                }


                fileMeta.setIsStar(!isStar);


                if (fileMeta.getIsStar()) {
                    AppData.get().addFavorite(new SimpleMeta(fileMeta.getPath(), System.currentTimeMillis()));
                } else {
                    AppData.get().removeFavorite(fileMeta);
                }

                fileMeta.setIsStarTime(System.currentTimeMillis());
                AppDB.get().updateOrSave(fileMeta);


                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                TempHolder.listHash++;

                RecentUpates.updateAll(a);
                return false;
            }
        };
    }

    ;

}
