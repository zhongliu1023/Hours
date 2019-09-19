package ours.china.hours.Model;

import java.io.Serializable;

import ours.china.hours.R;

public class Book implements Serializable {
    String averageTime;
    String bookAuthor;
    String bookID;
    String bookImageLocalUrl;
    String bookImageUrl;
    String bookLocalUrl;
    String bookName;
    String bookSummary;
    String bookUrl;
    String lastTime;
    String libraryPosition;
    String pagesArray;
    String publishedDate;
    String readState;
    String readTime;
    String specifiedTime;
    String totalPage;

    public Book(String bookID, String bookName, String bookAuthor, String bookLocalUrl, String bookImageLocalUrl, String bookUrl, String bookImageUrl, String bookSummary, String averageTime, String specifiedTime, String readState, String publishedDate, String totalPage, String pagesArray, String readTime, String lastTime, String libraryPosition) {
        String str = "";
        this.bookID = str;
        this.bookName = str;
        this.bookAuthor = str;
        this.bookLocalUrl = str;
        this.bookImageLocalUrl = str;
        this.bookUrl = str;
        this.bookImageUrl = str;
        this.bookSummary = str;
        this.averageTime = str;
        this.specifiedTime = str;
        this.readState = str;
        this.publishedDate = str;
        this.totalPage = str;
        this.pagesArray = str;
        this.readTime = str;
        this.lastTime = str;
        this.libraryPosition = str;
        this.bookID = bookID;
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
        this.totalPage = totalPage;
        this.pagesArray = pagesArray;
        this.readTime = readTime;
        this.lastTime = lastTime;
        this.libraryPosition = libraryPosition;
    }

    public Book() {
        String str = "";
        this.bookID = str;
        this.bookName = str;
        this.bookAuthor = str;
        this.bookLocalUrl = str;
        this.bookImageLocalUrl = str;
        this.bookUrl = str;
        this.bookImageUrl = str;
        this.bookSummary = str;
        this.averageTime = str;
        this.specifiedTime = str;
        this.readState = str;
        this.publishedDate = str;
        this.totalPage = str;
        this.pagesArray = str;
        this.readTime = str;
        this.lastTime = str;
        this.libraryPosition = str;
    }

    public String getBookID() {
        return this.bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return this.bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookLocalUrl() {
        return this.bookLocalUrl;
    }

    public void setBookLocalUrl(String bookLocalUrl) {
        this.bookLocalUrl = bookLocalUrl;
    }

    public String getBookImageLocalUrl() {
        return this.bookImageLocalUrl;
    }

    public void setBookImageLocalUrl(String bookImageLocalUrl) {
        this.bookImageLocalUrl = bookImageLocalUrl;
    }

    public String getBookUrl() {
        return this.bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getBookImageUrl() {
        return this.bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookSummary() {
        return this.bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getAverageTime() {
        return this.averageTime;
    }

    public void setAverageTime(String averageTime) {
        this.averageTime = averageTime;
    }

    public String getSpecifiedTime() {
        return this.specifiedTime;
    }

    public void setSpecifiedTime(String specifiedTime) {
        this.specifiedTime = specifiedTime;
    }

    public String getReadState() {
        return this.readState;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }

    public String getPublishedDate() {
        return this.publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public String getPagesArray() {
        return this.pagesArray;
    }

    public void setPagesArray(String pagesArray) {
        this.pagesArray = pagesArray;
    }

    public String getReadTime() {
        return this.readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLibraryPosition() {
        return this.libraryPosition;
    }

    public void setLibraryPosition(String libraryPosition) {
        this.libraryPosition = libraryPosition;
    }
}