package ours.china.hours.Model;

public class LocalBook {

    String bookName;
    String bookAuthor;
    String bookLocalUrl;
    String bookImageLocalUrl;

    public LocalBook() {
    }

    public LocalBook(String bookName, String bookAuthor, String bookLocalUrl, String bookImageLocalUrl) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookLocalUrl = bookLocalUrl;
        this.bookImageLocalUrl = bookImageLocalUrl;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookLocalUrl() {
        return bookLocalUrl;
    }

    public void setBookLocalUrl(String bookLocalUrl) {
        this.bookLocalUrl = bookLocalUrl;
    }

    public String getBookImageLocalUrl() {
        return bookImageLocalUrl;
    }

    public void setBookImageLocalUrl(String bookImageLocalUrl) {
        this.bookImageLocalUrl = bookImageLocalUrl;
    }
}
