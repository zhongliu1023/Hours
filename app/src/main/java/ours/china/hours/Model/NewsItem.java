package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsItem {
    @SerializedName("releaseTime") @Expose
    public String releaseTime = "";
    @SerializedName("title") @Expose public String title = "";
    @SerializedName("content") @Expose public String content = "";

}
