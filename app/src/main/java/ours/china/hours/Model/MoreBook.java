package ours.china.hours.Model;

public class MoreBook {

    String bookImageUrl;
    String downState;
    String readState;
    String bookName;
    String bookAuthor;

    public MoreBook(String bookImageUrl, String downState, String readState, String bookName, String bookAuthor) {
        this.bookImageUrl = bookImageUrl;
        this.downState = downState;
        this.readState = readState;
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

    public String getReadState() {
        return readState;
    }

    public void setReadState(String readState) {
        this.readState = readState;
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
