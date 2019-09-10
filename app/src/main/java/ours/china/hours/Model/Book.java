package ours.china.hours.Model;

import ours.china.hours.R;

public class Book {

    String bookName = "";
    String bookAuthor = "";
    String bookLocalUrl = "";
    String bookImageLocalUrl = "";
    String bookUrl = "";
    String bookImageUrl = "";
    String bookSummary = "";
    String averageTime = "";
    String specifiedTime = "";
    String readState = "";
    String publishedDate ="";

    public Book() {
    }

    public Book(String bookName, String bookAuthor, String bookLocalUrl, String bookImageLocalUrl, String bookUrl,
                String bookImageUrl, String bookSummary, String averageTime, String specifiedTime, String readState, String publishedDate) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookLocalUrl = bookLocalUrl;
        this.bookImageLocalUrl = bookImageLocalUrl;
        this.bookUrl = bookUrl;
        this.bookImageUrl = bookImageUrl;
        this.bookSummary = bookSummary;
        this.averageTime = averageTime;
        this.specifiedTime = specifiedTime;
        this.readState = readState;
        this.publishedDate = publishedDate;
    }

    public Book(String bookName, String bookAuthor, String bookLocalUrl, String bookImageLocalUrl) {
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

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(String averageTime) {
        this.averageTime = averageTime;
    }

    public String getSpecifiedTime() {
        return specifiedTime;
    }

    public void setSpecifiedTime(String specifiedTime) {
        this.specifiedTime = specifiedTime;
    }

    public String getReadState() {
        return readState;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
