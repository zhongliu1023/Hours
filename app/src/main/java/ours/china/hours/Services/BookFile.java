package ours.china.hours.Services;

import android.os.Parcel;
import android.os.Parcelable;

import ours.china.hours.Model.Book;

public class BookFile implements Parcelable {
    private String bookID;
    private String url;
    private int position;
    private int progress;
    private boolean isDownloaded;

    public BookFile(String id, String url) {
        this.bookID = id;
        this.url = url;
        this.position = -1;
        this.progress = 0;
        this.isDownloaded = false;
    }

    public boolean getIsDownloaded() {
        return isDownloaded;
    }
    public String getId() {
        return bookID;
    }
    public String getUrl() {
        return url;
    }
    public int getPosition() {
        return position;
    }
    public int getProgress() {
        return progress;
    }

    public void setIsDownloaded(boolean isStatus){this.isDownloaded = isStatus;}
    public void setId(String bookID) {
        this.bookID = bookID;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setPosition(int pos) {
        this.position = pos;
    }
    public void setProgress(int pros) {
        this.progress = pros;
    }
    @Override
    public String toString() {
        return url + " " + position + " %";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookID);
        dest.writeString(this.url);
        dest.writeInt(this.position);
        dest.writeInt(this.progress);
    }

    private BookFile(Parcel in) {
        this.bookID = in.readString();
        this.url = in.readString();
        this.position = in.readInt();
        this.progress = in.readInt();
    }

    public static final Parcelable.Creator<BookFile> CREATOR = new Parcelable.Creator<BookFile>() {
        public BookFile createFromParcel(Parcel source) {
            return new BookFile(source);
        }

        public BookFile[] newArray(int size) {
            return new BookFile[size];
        }
    };
}