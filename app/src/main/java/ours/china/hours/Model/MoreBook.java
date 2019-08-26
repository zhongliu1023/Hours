package ours.china.hours.Model;

public class MoreBook {

    String bookImageUrl;
    String bookName;
    String bookAuthor;

    public MoreBook(String bookImageUrl, String bookName, String bookAuthor) {
        this.bookImageUrl = bookImageUrl;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
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
}
