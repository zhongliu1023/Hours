package ours.china.hours.Model;

public class ReadingCompleteStatusBook {

    String bookImageUrl;
    String bookName;
    String readTime;
    String specifiedTime;

    public ReadingCompleteStatusBook(String bookImageUrl, String bookName, String readTime, String specifiedTime) {
        this.bookImageUrl = bookImageUrl;
        this.bookName = bookName;
        this.readTime = readTime;
        this.specifiedTime = specifiedTime;
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

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getSpecifiedTime() {
        return specifiedTime;
    }

    public void setSpecifiedTime(String specifiedTime) {
        this.specifiedTime = specifiedTime;
    }

}
