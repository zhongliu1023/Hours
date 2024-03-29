package ours.china.hours.BookLib.foobnix.pdf.info;

import android.content.Context;
import android.util.Log;

import ours.china.hours.BookLib.foobnix.android.utils.IO;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.foobnix.android.utils.Objects;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.model.AppData;
import ours.china.hours.BookLib.foobnix.model.AppProfile;
import ours.china.hours.BookLib.foobnix.model.AppState;
import ours.china.hours.R;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class BookmarksData {


    final static BookmarksData instance = new BookmarksData();

    public static BookmarksData get() {
        return instance;
    }


    public void add(AppBookmark bookmark) {
        LOG.d("BookmarksData", "add", bookmark.p, bookmark.text, bookmark.path);
        if (bookmark.p > 1) {
            bookmark.p = 0;
        }
        try {
            JSONObject obj = IO.readJsonObject(AppProfile.syncBookmarks);
            obj.put("" + bookmark.t, Objects.toJSONObject(bookmark));
            IO.writeObjAsync(AppProfile.syncBookmarks, obj);
            Log.i("Bookmark", "Bookmark writed file => " + AppProfile.syncBookmarks.getPath());
        } catch (Exception e) {
            LOG.e(e);
        }
    }
    public void update(AppBookmark bookmark) {
        LOG.d("BookmarksData", "remove", bookmark.t, bookmark.file);

        try {
            JSONObject obj = IO.readJsonObject(AppProfile.syncBookmarks);
            if (obj.has("" + bookmark.t)) {
                obj.put("" + bookmark.t, Objects.toJSONObject(bookmark));
                IO.writeObjAsync(AppProfile.syncBookmarks, obj);
            }
            IO.writeObjAsync(bookmark.file, obj);
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public void remove(AppBookmark bookmark) {
        LOG.d("BookmarksData", "remove", bookmark.t, bookmark.file);

        try {
            JSONObject obj = IO.readJsonObject(AppProfile.syncBookmarks);
            if (obj.has("" + bookmark.t)) {
                obj.remove("" + bookmark.t);
            }
            IO.writeObjAsync(bookmark.file, obj);
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public AppBookmark getBookMark(File file, int page, int totalPage){
        List<AppBookmark> bookmarks = getBookmarksByBook(file.getPath());
        AppBookmark foundBookMark  = new AppBookmark();
        for (AppBookmark appBookmark : bookmarks){
            if (appBookmark.getPage(totalPage) == page){
                foundBookMark = appBookmark;
            }
        }
        return foundBookMark;
    }

    public List<AppBookmark> getBookmarksByBook(File file) {
        if (file == null) {
            return new ArrayList<>();
        }
        return getBookmarksByBook(file.getPath());
    }

    public synchronized List<AppBookmark> getAll(Context c) {
        final List<AppBookmark> all = getAll();
        final Iterator<AppBookmark> iterator = all.iterator();
        String fast = c.getString(R.string.fast_bookmark);
        while (iterator.hasNext()) {
            final AppBookmark next = iterator.next();

            if (AppState.get().isShowOnlyAvailabeBooks) {
                if (!new File(next.getPath()).isFile()) {
                    iterator.remove();
                    continue;
                }
            }

            if (!AppState.get().isShowFastBookmarks) {
                if (fast.equals(next.text)) {
                    iterator.remove();
                }

            }

        }
        return all;
    }


    private List<AppBookmark> getAll() {

        List<AppBookmark> all = new ArrayList<>();

        try {

            for (File file : AppProfile.getAllFiles(AppProfile.APP_BOOKMARKS_JSON)) {
                JSONObject obj = IO.readJsonObject(file);


                final Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    final String next = keys.next();

                    AppBookmark appBookmark = new AppBookmark();
                    appBookmark.file = file;
                    final JSONObject local = obj.getJSONObject(next);
                    Objects.loadFromJson(appBookmark, local);
                    all.add(appBookmark);
                }
            }
        } catch (Exception e) {
            LOG.e(e);
        }


//        Iterator<AppBookmark> iterator = all.iterator();
//        while (iterator.hasNext()) {
//            AppBookmark next = iterator.next();
//            if (next.getText().equals(quick)) {
//                iterator.remove();
//            }
//        }

        LOG.d("getAll-size", all.size());
        Collections.sort(all, BY_TIME);
        return all;
    }


    public List<AppBookmark> getBookmarksByBook(String path) {

        List<AppBookmark> all = new ArrayList<>();


        for (File file : AppProfile.getAllFiles(AppProfile.APP_BOOKMARKS_JSON)) {
            JSONObject obj = IO.readJsonObject(file);
            try {
                final Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    final String next = keys.next();

                    AppBookmark appBookmark = new AppBookmark();
                    appBookmark.file = file;
                    final JSONObject local = obj.getJSONObject(next);
                    Objects.loadFromJson(appBookmark, local);
                    if (appBookmark.getPath().equals(path)) {
                        all.add(appBookmark);
                    }
                }
            } catch (Exception e) {
                LOG.e(e);
            }
        }


        LOG.d("getBookmarksByBook", path, all.size());
        Collections.sort(all, BY_PERCENT);
        return all;
    }

    public boolean hasBookmark(String lastBookPath, int page, int pages) {
        final List<AppBookmark> bookmarksByBook = getBookmarksByBook(lastBookPath);
        for (AppBookmark appBookmark : bookmarksByBook) {
            if (appBookmark.isF && Math.round(appBookmark.getPercent() * pages) == page) {
                return true;
            }
        }
        return false;
    }

    static final Comparator<AppBookmark> BY_PERCENT = new Comparator<AppBookmark>() {

        @Override
        public int compare(AppBookmark o1, AppBookmark o2) {
            return Float.compare(o1.getPercent(), o2.getPercent());
        }
    };

    static final Comparator<AppBookmark> BY_TIME = new Comparator<AppBookmark>() {

        @Override
        public int compare(AppBookmark o1, AppBookmark o2) {
            return Float.compare(o2.getTime(), o1.getTime());
        }
    };


    public Map<String, List<AppBookmark>> getBookmarksMap() {
        return null;
    }


    public void cleanBookmarks() {
        //IO.writeObj(AppProfile.syncBookmarks.getPath(), "{}");
        AppData.get().clearAll(AppProfile.APP_BOOKMARKS_JSON);
    }


}
