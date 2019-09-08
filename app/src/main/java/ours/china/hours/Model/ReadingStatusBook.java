package ours.china.hours.Model;

public class ReadingStatusBook {

    String bookImageUrl;
    String bookName;
    String readPercent;
    String readTime;
    String specifiedTime;
    String lastDate;

    public ReadingStatusBook(String bookImageUrl, String bookName, String readPercent, String readTime, String specifiedTime, String lastDate) {
        this.bookImageUrl = bookImageUrl;
        this.bookName = bookName;
        this.readPercent = readPercent;
        this.readTime = readTime;
        this.specifiedTime = specifiedTime;
        this.lastDate = lastDate;
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

    public String getReadPercent() {
        return readPercent;
    }

    public void setReadPercent(String readPercent) {
        this.readPercent = readPercent;
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

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

}
