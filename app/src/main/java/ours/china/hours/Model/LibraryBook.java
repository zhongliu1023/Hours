package ours.china.hours.Model;

public class LibraryBook {
    String bookImageUrl;
    String bookName;

    public LibraryBook(String bookImageUrl, String bookName) {
        this.bookImageUrl = bookImageUrl;
        this.bookName = bookName;
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
}
