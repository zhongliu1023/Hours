package ours.china.hours.BookLib.foobnix.model;

import ours.china.hours.BookLib.foobnix.android.utils.LOG;

import java.io.File;

public class AppBookmark implements MyPath.RelativePath {
    public String path;
    public String text;
    public String subTitle;
    public String note;
    public int type;

    public float p;
    public long t;
    public boolean isF = false;

    transient public File file;

    public AppBookmark() {

    }

    public AppBookmark(String path, String text, float percent) {
        super();
        this.path = MyPath.toRelative(path);
        this.file = new File(path);
        this.text = text;
        this.p = percent;
        t = System.currentTimeMillis();

    }

    public int getPage(int pages) {
        LOG.d("MyMath getPage",p, pages);
        return Math.round(p * pages);
    }

    public int getType() {
        return type;
    }
    public void setType(int t) {
        this.type = t;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String str) {
        this.note = str;
    }
    public String getSubTitle() {
        return subTitle;
    }
    public void setSubTitle(String str) {
        this.subTitle = str;
    }
    public String getText() {
        return text;
    }

    public String getPath() {
        return MyPath.toAbsolute(path);
    }

    public void setPath(String path) {
        this.path = MyPath.toRelative(path);
    }

    public float getPercent() {
        return p;
    }

    public float getTime() {
        return t;
    }

    @Override
    public int hashCode() {
        return (path + text + p).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        AppBookmark a = (AppBookmark) obj;
        return a.t == t;
    }


}
