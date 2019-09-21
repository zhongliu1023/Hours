package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ours.china.hours.R;

public class Book {
    @SerializedName("bookId") @Expose public String bookId = "";
    @SerializedName("bookSerial") @Expose public String bookSerial = "";
    @SerializedName("coverUrl") @Expose public String coverUrl = "";
    @SerializedName("bookName") @Expose public String bookName = "";
    @SerializedName("bookNameUrl") @Expose public String bookNameUrl = "";
    @SerializedName("publishDate") @Expose public String publishDate = "";
    @SerializedName("author") @Expose public String author = "";
    @SerializedName("averageProgress") @Expose public String averageProgress = "";
    @SerializedName("averageTime") @Expose public String averageTime = "";
    @SerializedName("allAverageTime") @Expose public String allAverageTime = "";
    @SerializedName("deadline") @Expose public String deadline = "";
    @SerializedName("demandTime") @Expose public String demandTime = "";
    @SerializedName("favouriteCount") @Expose public String favouriteCount = "";
    @SerializedName("downloadedCount") @Expose public String downloadedCount = "";
    @SerializedName("attentionCount") @Expose public String attentionCount = "";
    @SerializedName("readingCount") @Expose public String readingCount = "";
    @SerializedName("isReadCount") @Expose public String isReadCount = "";
    @SerializedName("isDeleted") @Expose public String isDeleted = "";
    @SerializedName("summary") @Expose public String summary = "";
    @SerializedName("category") @Expose public String category = "";
    @SerializedName("publishingHouse") @Expose public String publishingHouse = "";
    @SerializedName("pageCount") @Expose public String pageCount = "";
    @SerializedName("isbn") @Expose public String isbn = "";
    @SerializedName("edition") @Expose public String edition = "";
    @SerializedName("fileName") @Expose public String fileName = "";
    @SerializedName("bookStatus") @Expose public BookStatus bookStatus = new BookStatus();

    @SerializedName("bookLocalUrl") @Expose public String bookLocalUrl = "";
    @SerializedName("bookImageLocalUrl") @Expose public String bookImageLocalUrl = "";
    @SerializedName("libraryPosition") @Expose public String libraryPosition = "";
}