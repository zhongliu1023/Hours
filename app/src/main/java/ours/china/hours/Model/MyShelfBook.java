package ours.china.hours.Model;

public class MyShelfBook {

    String bookImageUrl;
    String downState;
    String bookName;
    String bookAuthor;

    public MyShelfBook(String bookImageUrl, String downState, String bookName, String bookAuthor) {
        this.bookImageUrl = bookImageUrl;
        this.downState = downState;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getDownState() {
        return downState;
    }

    public void setDownState(String downState) {
        this.downState = downState;
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
