package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookStatus {
    @SerializedName("pages") @Expose public String pages = "";
    @SerializedName("time") @Expose public String time = "";
    @SerializedName("progress") @Expose public String progress = "";
    @SerializedName("lastRead") @Expose public String lastRead = "";
    @SerializedName("isRead") @Expose public String isRead = "";
    @SerializedName("isAttention") @Expose public String isAttention = "";
    @SerializedName("collection") @Expose public String collection = "";
    @SerializedName("notes") @Expose public String notes = "";
    @SerializedName("bookmarks") @Expose public String bookmarks = "";

}
