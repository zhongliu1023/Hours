package ours.china.hours.BookLib.foobnix.pdf.info.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Pair;

import ours.china.hours.BookLib.foobnix.android.utils.BaseItemLayoutAdapter;
import ours.china.hours.BookLib.foobnix.android.utils.IO;
import ours.china.hours.BookLib.foobnix.android.utils.Keyboards;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.mobi.parser.IOUtils;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.model.AppData;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.BookLib.foobnix.model.AppTemp;
import ours.china.hours.BookLib.foobnix.model.MyPath;
import ours.china.hours.BookLib.foobnix.model.SimpleMeta;
import ours.china.hours.BookLib.foobnix.model.TagData;
import ours.china.hours.BookLib.foobnix.pdf.info.AppsConfig;
import ours.china.hours.BookLib.foobnix.pdf.info.BookmarksData;
import ours.china.hours.BookLib.foobnix.pdf.info.DialogSpeedRead;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.pdf.info.Playlists;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.HorizontalBookReadingActivity;
import ours.china.hours.R;
import ours.china.hours.BookLib.foobnix.pdf.info.TintUtil;
import ours.china.hours.BookLib.foobnix.pdf.info.Urls;
import ours.china.hours.BookLib.foobnix.pdf.info.view.Dialogs;
import ours.china.hours.BookLib.foobnix.pdf.info.view.DialogsPlaylist;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.UITab;
import ours.china.hours.BookLib.foobnix.pdf.search.activity.msg.UpdateAllFragments;
import ours.china.hours.BookLib.foobnix.sys.TempHolder;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.foobnix.ui2.FileMetaCore;
import ours.china.hours.BookLib.foobnix.ui2.MainTabs2;

import org.ebookdroid.BookType;
import org.ebookdroid.ui.viewer.VerticalViewActivity;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareDialog {

    public static void showArchive(final Activity a, final File file, final Runnable onDeleteAction) {
        if (ExtUtils.isNotValidFile(file)) {
            Toast.makeText(a, R.string.file_not_found, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> items = new ArrayList<String>();

        if (!ExtUtils.isImageOrEpub(file)) {
            items.add(a.getString(R.string.convert_to) + " EPUB");
            items.add(a.getString(R.string.convert_to) + " PDF");
        }
        final boolean canDelete = ExtUtils.isExteralSD(file.getPath()) ? true : file.canWrite();
        final boolean isShowInfo = !ExtUtils.isExteralSD(file.getPath());

        items.add(a.getString(R.string.open_with));
        items.add(a.getString(R.string.send_file));

        if (canDelete) {
            items.add(a.getString(R.string.delete));
        }

        if (isShowInfo) {
            items.add(a.getString(R.string.file_info));
        }

        final String chooseTitle = file != null ? file.getPath() : a.getString(R.string.choose_);

        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.choose_)//
                .setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        int i = 0;
                        if (!ExtUtils.isImageOrEpub(file)) {
                            if (which == i++) {
                                showsItemsDialog(a, chooseTitle, AppState.CONVERTERS.get("EPUB"));
                            }
                            if (which == i++) {
                                showsItemsDialog(a, chooseTitle, AppState.CONVERTERS.get("PDF"));
                            }
                        }
                        if (which == i++) {
                            ExtUtils.openWith(a, file);
                        } else if (which == i++) {
                            ExtUtils.sendFileTo(a, file);
                        } else if (canDelete && which == i++) {
                            FileInformationDialog.dialogDelete(a, file, onDeleteAction);
                        } else if (isShowInfo && which == i++) {
                            FileInformationDialog.showFileInfoDialog(a, file, onDeleteAction);
                        }

                    }
                });
        builder.show();
    }

    ;

    public static void showsItemsDialog(final Activity a, String title, final String[] items) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(title)//
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        Urls.open(a, items[which]);
                    }
                });

        AlertDialog dialog = builder.show();

    }

    public static void dirLongPress(final Activity a, final String to, final Runnable onRefresh) {
        List<String> items = new ArrayList<String>();


        items.add(a.getString(R.string.paste));
        items.add(a.getString(R.string.move));

        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                int i = 0;
                if (which == i++) {
                    try {
                        String from = TempHolder.get().copyFromPath;
                        File fromFile = new File(from);
                        File toFile = new File(to, fromFile.getName());

                        if (toFile.exists()) {
                            Toast.makeText(a, R.string.the_file_already_exists_, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LOG.d("Copy from to", from, ">>", to);

                        InputStream input = new BufferedInputStream(new FileInputStream(from));
                        OutputStream output = new BufferedOutputStream(new FileOutputStream(toFile));

                        IOUtils.copyClose(input, output);


                        TempHolder.get().listHash++;

                        Toast.makeText(a, R.string.success, Toast.LENGTH_SHORT).show();
                        TempHolder.get().copyFromPath = null;
                        onRefresh.run();
                    } catch (Exception e) {
                        LOG.e(e);
                        Toast.makeText(a, R.string.msg_unexpected_error, Toast.LENGTH_SHORT).show();
                    }
                }
                if (which == i++) {
                    try {
                        String from = TempHolder.get().copyFromPath;
                        File fromFile = new File(from);
                        File toFile = new File(to, fromFile.getName());
                        LOG.d("Copy from to", from, ">>", to);

                        if (toFile.exists()) {
                            Toast.makeText(a, R.string.the_file_already_exists_, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        InputStream input = new BufferedInputStream(new FileInputStream(from));
                        OutputStream output = new BufferedOutputStream(new FileOutputStream(toFile));

                        IOUtils.copyClose(input, output);


                        fromFile.delete();
                        AppDB.get().delete(new FileMeta(fromFile.getPath()));
                        TempHolder.get().listHash++;

                        Toast.makeText(a, R.string.success, Toast.LENGTH_SHORT).show();
                        TempHolder.get().copyFromPath = null;
                        onRefresh.run();
                    } catch (Exception e) {
                        LOG.e(e);
                        Toast.makeText(a, R.string.msg_unexpected_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Keyboards.hideNavigation(a);
            }
        });
        create.show();
    }

    public static void show(final Activity a, final File file, final Runnable onDeleteAction, final int page, final DocumentController dc, final Runnable hideShow) {
        if (file == null) {
            Toast.makeText(a, R.string.file_not_found, Toast.LENGTH_LONG).show();
            return;
        }

        if (!ExtUtils.isExteralSD(file.getPath()) && ExtUtils.isNotValidFile(file)) {
            Toast.makeText(a, R.string.file_not_found, Toast.LENGTH_LONG).show();
            return;
        }
        final boolean isPDF = BookType.PDF.is(file.getPath());
        final boolean isLibrary = false;// a instanceof MainTabs2 ? false :
        // true;
        final boolean isMainTabs = a instanceof MainTabs2;

        List<String> items = new ArrayList<String>();

        if (isLibrary) {
            items.add(a.getString(R.string.library));
        }

        if (dc != null) {
            if (a instanceof VerticalViewActivity || dc.isMusicianMode()) {
                items.add(AppState.get().nameHorizontalMode);
            }
            if (a instanceof HorizontalBookReadingActivity || dc.isMusicianMode()) {
                items.add(AppState.get().nameVerticalMode);
            }

            if (dc.isMusicianMode() == false) {
                items.add(AppState.get().nameMusicianMode);
            }
        }

        if (isPDF) {
            items.add(a.getString(R.string.make_text_reflow));
        }

        if (dc != null) {
            items.add(a.getString(R.string.fast_reading));
        }

        items.add(a.getString(R.string.open_with));
        items.add(a.getString(R.string.send_file));
        final boolean isExternalOrCloud = ExtUtils.isExteralSD(file.getPath());
        boolean canDelete1 = ExtUtils.isExteralSD(file.getPath()) ||  file.canWrite();
        final boolean canCopy = !ExtUtils.isExteralSD(file.getPath());
        final boolean isShowInfo = !ExtUtils.isExteralSD(file.getPath());

        final boolean isRemovedFromLibrary = AppData.get().getAllExcluded().contains(new SimpleMeta(file.getPath()));

        if (file.getPath().contains(AppProfile.PROFILE_PREFIX)) {
            canDelete1 = false;
        }
        final boolean canDelete = canDelete1;

        if (isMainTabs) {
            if (canDelete) {
                items.add(a.getString(R.string.delete));
            }
            if (canCopy) {
                items.add(a.getString(R.string.copy));
            }
            if (!isRemovedFromLibrary) {
                items.add(a.getString(R.string.remove_from_library));
            }
        }

        if (!isExternalOrCloud) {
            items.add(a.getString(R.string.add_tags));
        }

        if (AppsConfig.isCloudsEnable) {
            items.add(a.getString(R.string.upload_to_cloud));
        }
        final boolean isPlaylist = file.getName().endsWith(Playlists.L_PLAYLIST);
        if (!isPlaylist) {
            items.add(a.getString(R.string.add_to_playlist));
        }

        if (isShowInfo) {
            items.add(a.getString(R.string.file_info));
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                int i = 0;

                if (isLibrary && which == i++) {
                    a.finish();
                    MainTabs2.startActivity(a, UITab.getCurrentTabIndex(UITab.SearchFragment));
                }

                if (dc != null && (a instanceof HorizontalBookReadingActivity || dc.isMusicianMode()) && which == i++) {
                    dc.onCloseActivityFinal(new Runnable() {

                        @Override
                        public void run() {
                            if (dc.isMusicianMode()) {
                                AppTemp.get().readingMode = AppState.READING_MODE_BOOK;
                            } else {
                                AppTemp.get().readingMode = AppState.READING_MODE_SCROLL;
                            }
                            ExtUtils.showDocumentWithoutDialog(a, file, a.getIntent().getStringExtra(DocumentController.EXTRA_PLAYLIST));

                        }
                    });

                }
                if (dc != null && (a instanceof VerticalViewActivity || dc.isMusicianMode()) && which == i++) {
                    if (dc != null) {
                        dc.onCloseActivityFinal(new Runnable() {

                            @Override
                            public void run() {
                                if (dc.isMusicianMode()) {
                                    AppTemp.get().readingMode = AppState.READING_MODE_SCROLL;
                                } else {
                                    AppTemp.get().readingMode = AppState.READING_MODE_BOOK;
                                }
                                ExtUtils.showDocumentWithoutDialog(a, file, a.getIntent().getStringExtra(DocumentController.EXTRA_PLAYLIST));
                            }
                        });
                    }
                }
                if (dc != null && dc.isMusicianMode() == false && which == i++) {
                    dc.onCloseActivityFinal(new Runnable() {

                        @Override
                        public void run() {
                            AppTemp.get().readingMode = AppState.READING_MODE_MUSICIAN;
                            ExtUtils.showDocumentWithoutDialog(a, file, a.getIntent().getStringExtra(DocumentController.EXTRA_PLAYLIST));
                        }
                    });
                }
                if (isPDF && which == i++) {
                    ExtUtils.openPDFInTextReflow(a, file, page + 1, dc);
                }
                if (dc != null && which == i++) {
                    if (hideShow != null) {
                        AppState.get().isEditMode = false;
                        hideShow.run();
                    }
                    DialogSpeedRead.show(a, dc);
                } else if (which == i++) {
                    ExtUtils.openWith(a, file);
                } else if (which == i++) {
                    ExtUtils.sendFileTo(a, file);
                } else if (isMainTabs && canDelete && which == i++) {
                    FileInformationDialog.dialogDelete(a, file, onDeleteAction);
                } else if (isMainTabs && canCopy && which == i++) {
                    TempHolder.get().copyFromPath = file.getPath();
                    Toast.makeText(a, R.string.copy, Toast.LENGTH_SHORT).show();
                } else if (isMainTabs && !isRemovedFromLibrary && which == i++) {
                    FileMeta load = AppDB.get().load(file.getPath());
                    if (load != null) {
                        load.setIsSearchBook(false);
                        load.setIsStar(false);
                        load.setTag(null);
                        AppDB.get().update(load);

                        AppData.get().removeFavorite(load);
                        AppData.get().addExclue(load.getPath());

                    }


                    EventBus.getDefault().post(new UpdateAllFragments());
                } else if (!isExternalOrCloud && which == i++) {
                    Dialogs.showTagsDialog(a, file, false, null);
                } else if (AppsConfig.isCloudsEnable && which == i++) {
                    showAddToCloudDialog(a, file);
                } else if (!isPlaylist && which == i++) {
                    DialogsPlaylist.showPlaylistsDialog(a, null, file);
                } else if (which == i++) {
                    final File to = new File(AppProfile.SYNC_FOLDER_BOOKS, file.getName());
                    boolean result = IO.copyFile(file, to);
                    if (result && AppTemp.get().isEnableSync) {

                        AppDB.get().setIsSearchBook(file.getPath(), false);
                        FileMetaCore.createMetaIfNeed(to.getPath(), true);

                        String tags = TagData.getTags(file.getPath());
                        TagData.saveTags(to.getPath(), tags);

                        boolean isRecent = AppData.contains(AppData.get().getAllRecent(), file.getPath());
                        LOG.d("isRecent", isRecent, file.getPath());

                        if (isRecent) {
                            AppData.get().removeRecent(new FileMeta(file.getPath()));

                            final FileMeta load = AppDB.get().load(file.getPath());
                            if (load != null && load.getIsRecentTime() != null) {
                                AppData.get().addRecent(new SimpleMeta(to.getPath(), load.getIsRecentTime()));
                            } else {
                                AppData.get().addRecent(new SimpleMeta(to.getPath()));
                            }
                        }

                        final List<AppBookmark> bookmarks = BookmarksData.get().getBookmarksByBook(file.getPath());
                        for (AppBookmark appBookmark : bookmarks) {
                            appBookmark.path = MyPath.toRelative(to.getPath());
                            BookmarksData.get().add(appBookmark);
                        }
                    }


                    TempHolder.listHash++;
                    EventBus.getDefault().post(new UpdateAllFragments());
                } else if (isShowInfo && which == i++) {
                    FileInformationDialog.showFileInfoDialog(a, file, onDeleteAction);
                }

            }

        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Keyboards.hideNavigation(a);
            }

        });
        create.show();
//        MyPopupMenu menu = new MyPopupMenu(a, null);
//
//        menu.getMenu(R.drawable.glyphicons_basic_578_share, R.string.share, () -> ExtUtils.openPDFInTextReflow(a, file, page + 1, dc));
//        menu.getMenu(R.drawable.glyphicons_2_book_open, R.string.open_with, () -> ExtUtils.openPDFInTextReflow(a, file, page + 1, dc));
//
//        menu.show();
    }


    public static void showAddToCloudDialog(final Activity a, final File file) {
        final AlertDialog.Builder inner = new AlertDialog.Builder(a);
        inner.setTitle(R.string.upload_to_cloud);

        List<Pair<Integer, Integer>> list = Arrays.asList(//
                new Pair<Integer, Integer>(R.string.dropbox, R.drawable.dropbox), //
                new Pair<Integer, Integer>(R.string.google_drive, R.drawable.gdrive), //
                new Pair<Integer, Integer>(R.string.one_drive, R.drawable.onedrive)//
        );

        inner.setAdapter(new BaseItemLayoutAdapter<Pair<Integer, Integer>>(a, R.layout.item_dict_line, list) {
            @Override
            public void populateView(View layout, int position, Pair<Integer, Integer> item) {
                ((TextView) layout.findViewById(R.id.text1)).setText(item.first);
                ImageView imageView = (ImageView) layout.findViewById(R.id.image1);
                imageView.setImageResource(item.second);

                TintUtil.setNoTintImage(imageView);

                if (R.string.dropbox == item.first ) {
                    TintUtil.setTintImageNoAlpha(imageView, Color.LTGRAY);
                }

                if (R.string.google_drive == item.first) {
                    TintUtil.setTintImageNoAlpha(imageView, Color.LTGRAY);

                }
                if (R.string.one_drive == item.first ) {
                    TintUtil.setTintImageNoAlpha(imageView, Color.LTGRAY);

                }

            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }

        });
        inner.show();
    }

}
