package ours.china.hours.Model;

public class Book {

    String bookImage;
    String bookName;
    String downloadState;
    String readState;

    public Book(String bookImage, String bookName, String downloadState, String readState) {
        this.bookImage = bookImage;
        this.bookName = bookName;
        this.downloadState = downloadState;
        this.readState = readState;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(String downloadState) {
        this.downloadState = downloadState;
    }

    public String getReadState() {
        return readState;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }
}
