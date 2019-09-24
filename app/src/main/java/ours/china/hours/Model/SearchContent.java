package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchContent {
    @SerializedName("currentPage") @Expose public int currentPage = 0;
    @SerializedName("title") @Expose public String title = "";
    @SerializedName("content") @Expose public String content = "";
}
